package com.patientmgmt.patientservice.service;

import com.patientmgmt.patientservice.dto.PagedPatientResponseDTO;
import com.patientmgmt.patientservice.dto.PatientRequestDTO;
import com.patientmgmt.patientservice.dto.PatientResponseDTO;
import com.patientmgmt.patientservice.exception.EmailAlreadyExistsException;
import com.patientmgmt.patientservice.exception.InvalidPatientIdException;
import com.patientmgmt.patientservice.grpc.BillingServiceGrpcClient;
import com.patientmgmt.patientservice.kafka.KafkaProducer;
import com.patientmgmt.patientservice.mapper.PatientServiceMapper;
import com.patientmgmt.patientservice.model.Patient;
import com.patientmgmt.patientservice.repository.PatientServiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    private static final Logger log = LoggerFactory.getLogger(PatientService.class);
    private PatientServiceRepository patientServiceRepository;
    private BillingServiceGrpcClient billingServiceGrpcClient;
    private KafkaProducer kafkaProducer;
    private S3Client s3Client;
    @Value("${aws.bucket.name}")
    private String bucketName;

    public PatientService(PatientServiceRepository patientServiceRepository,
                          BillingServiceGrpcClient billingServiceGrpcClient, KafkaProducer kafkaProducer,
                          S3Client s3Client) {
        this.patientServiceRepository = patientServiceRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
        this.s3Client = s3Client;
    }

    public List<PatientResponseDTO> getAllPatients() {
        List<Patient> patients = patientServiceRepository.findAll();
        return patients.stream()
                .map(PatientServiceMapper::toDTO)
                .toList();
    }

    @Cacheable(value = "patients",
            key="#page + '-' + #size + '-' + #sort + '-' + #sortField",
            condition = "#searchField == ''")
    public PagedPatientResponseDTO getAllPagedPatients(int page, int size, String sort, String sortField, String searchField) {
        log.info("[REDIS] cache miss - fetching details from DB");
        Pageable pageable = PageRequest.of(page-1, size,
                sort.equalsIgnoreCase("desc") ?
                Sort.by(sortField).descending() : Sort.by(sortField).ascending());
        Page<Patient> patientPage;
        if (null == searchField || searchField.isBlank()) {
            patientPage = patientServiceRepository.findAll(pageable);
        } else {
            patientPage = patientServiceRepository.findByNameContainingIgnoreCase(searchField, pageable);
        }
        List<PatientResponseDTO> patientResponseDTOList = patientPage.getContent().stream()
                .map(PatientServiceMapper::toDTO)
                .toList();
        return new PagedPatientResponseDTO(patientResponseDTOList, patientPage.getNumber()+1, patientPage.getSize(),
                patientPage.getTotalPages(), (int) patientPage.getTotalElements());
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if (patientServiceRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException("A patient with given email id already exists");
        }
        Patient patient = PatientServiceMapper.toModel(patientRequestDTO);
        Patient createdPatient = patientServiceRepository.save(patient);
        billingServiceGrpcClient.createBillingAccount(createdPatient.getId().toString(), createdPatient.getName(),
                createdPatient.getEmail());
        kafkaProducer.sendEvent(createdPatient);
        return PatientServiceMapper.toDTO(createdPatient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        Patient patient = patientServiceRepository.findById(id).orElseThrow(() -> new InvalidPatientIdException(
                "Given patient id is not available in the system"));
        if (patientServiceRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)) {
            throw new EmailAlreadyExistsException("Different patient with given email id already exists");
        }
        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        Patient updatedPatient = patientServiceRepository.save(patient);
        return PatientServiceMapper.toDTO(updatedPatient);
    }

    public void deletePatient(UUID id) {
        patientServiceRepository.deleteById(id);
    }

    public void uploadFile(MultipartFile file) throws IOException {
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(file.getOriginalFilename())
                        .build(),
                RequestBody.fromBytes(file.getBytes()));
    }

    public byte[] downloadFile(String key) {
        ResponseBytes<GetObjectResponse> objectAsBytes =
                s3Client.getObjectAsBytes(GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build());
        return objectAsBytes.asByteArray();
    }
}

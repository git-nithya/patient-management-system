package com.patientmgmt.patientservice.service;

import com.patientmgmt.patientservice.dto.PatientRequestDTO;
import com.patientmgmt.patientservice.dto.PatientResponseDTO;
import com.patientmgmt.patientservice.exception.EmailAlreadyExistsException;
import com.patientmgmt.patientservice.exception.InvalidPatientIdException;
import com.patientmgmt.patientservice.grpc.BillingServiceGrpcClient;
import com.patientmgmt.patientservice.kafka.KafkaProducer;
import com.patientmgmt.patientservice.mapper.PatientServiceMapper;
import com.patientmgmt.patientservice.model.Patient;
import com.patientmgmt.patientservice.repository.PatientServiceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    private PatientServiceRepository patientServiceRepository;
    private BillingServiceGrpcClient billingServiceGrpcClient;
    private KafkaProducer kafkaProducer;

    public PatientService(PatientServiceRepository patientServiceRepository,
                          BillingServiceGrpcClient billingServiceGrpcClient, KafkaProducer kafkaProducer) {
        this.patientServiceRepository = patientServiceRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    public List<PatientResponseDTO> getAllPatients() {
        List<Patient> patients = patientServiceRepository.findAll();
        return patients.stream()
                .map(PatientServiceMapper::toDTO)
                .toList();
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
}

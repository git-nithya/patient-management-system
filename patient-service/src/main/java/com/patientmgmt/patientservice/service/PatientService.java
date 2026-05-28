package com.patientmgmt.patientservice.service;

import com.patientmgmt.patientservice.dto.PatientResponseDTO;
import com.patientmgmt.patientservice.mapper.PatientServiceDtoMapper;
import com.patientmgmt.patientservice.model.Patient;
import com.patientmgmt.patientservice.repository.PatientServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private PatientServiceRepository patientServiceRepository;

    public PatientService(PatientServiceRepository patientServiceRepository) {
        this.patientServiceRepository = patientServiceRepository;
    }

    public List<PatientResponseDTO> getAllPatients() {
        List<Patient> patients = patientServiceRepository.findAll();
        return patients.stream()
                .map(PatientServiceDtoMapper::toDTO)
                .toList();
    }
}

package com.patientmgmt.patientservice.kafka;

import com.patientmgmt.kafka.patient.event.PatientEvent;
import com.patientmgmt.patientservice.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    public static final String TOPIC = "patient";
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);

    @Autowired
    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(Patient patient) {
        PatientEvent patientEvent = PatientEvent.newBuilder()
                .setPatientId(patient.getId().toString())
                .setName(patient.getName())
                .setEmail(patient.getEmail())
                .setEventType("PATIENT_CREATED")
                .build();
        try {
            kafkaTemplate.send(TOPIC, patientEvent.toByteArray());
        } catch (Exception e) {
            log.error("Error sending PatientCreated event: {}", patientEvent);
        }

    }
}

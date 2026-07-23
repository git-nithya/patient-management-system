package com.patientmgmt.patientservice.kafka;

import com.patientmgmt.kafka.billing.event.CreateBillingAccountEvent;
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
            kafkaTemplate.send("patient", patientEvent.toByteArray());
        } catch (Exception e) {
            log.error("Error sending PatientCreated event: {}", patientEvent);
        }

    }

    public void sendCreateBillingAccountEvent(String patientId, String name, String email) {
        CreateBillingAccountEvent createBillingAccountEvent = CreateBillingAccountEvent.newBuilder()
                .setPatientId(patientId)
                .setName(name)
                .setEmail(email)
                .setEventType("CREATE_BILLING_ACCOUNT_EVENT")
                .build();
        try {
            kafkaTemplate.send("billing-account", createBillingAccountEvent.toByteArray());
        } catch (Exception e) {
            log.error("Error sending CreateBillingAccount event: {}", createBillingAccountEvent);
        }

    }
}

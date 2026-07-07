package com.patientmgmt.analytics_service.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import com.patientmgmt.kafka.patient.event.PatientEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "patient", groupId = "analytics-service")
    public void consumeEvents(byte[] event) {
        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(event);
            log.info("Received Patient Event: [PatientId={},PatientName={},PatientEmail={}]",
                    patientEvent.getPatientId(),
                    patientEvent.getName(),
                    patientEvent.getEmail());
        } catch (InvalidProtocolBufferException e) {
            log.error("Exception while parsing PatientCreated event: {}", e.getMessage());
        }
    }
}

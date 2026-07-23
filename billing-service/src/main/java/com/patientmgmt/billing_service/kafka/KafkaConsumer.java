package com.patientmgmt.billing_service.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import com.patientmgmt.kafka.billing.event.CreateBillingAccountEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "billing-account", groupId = "billing-service")
    public void consumeEvent(byte[] event) {
        try {
            CreateBillingAccountEvent createBillingAccountEvent = CreateBillingAccountEvent.parseFrom(event);
            log.info("Create billing account event received for patient id {}",
                    createBillingAccountEvent.getPatientId());
            // save details to db in case email id already doesnt exist
        } catch (InvalidProtocolBufferException exception) {
            log.error("Error while parsing create billing account event {}", exception.getMessage());
        }

    }
}

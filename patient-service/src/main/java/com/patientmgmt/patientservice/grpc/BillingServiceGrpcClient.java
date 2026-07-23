package com.patientmgmt.patientservice.grpc;

import com.patientmgmt.billingservice.BillingRequest;
import com.patientmgmt.billingservice.BillingResponse;
import com.patientmgmt.billingservice.BillingServiceGrpc;
import com.patientmgmt.kafka.billing.event.CreateBillingAccountEvent;
import com.patientmgmt.patientservice.kafka.KafkaProducer;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceGrpcClient {

    private static final Logger log = LoggerFactory.getLogger(BillingServiceGrpcClient.class);
    private final BillingServiceGrpc.BillingServiceBlockingStub billingServiceBlockingStub;
    private final KafkaProducer kafkaProducer;

    @Autowired
    public BillingServiceGrpcClient(BillingServiceGrpc.BillingServiceBlockingStub billingServiceBlockingStub,
                                    KafkaProducer kafkaProducer) {
        this.billingServiceBlockingStub = billingServiceBlockingStub;
        this.kafkaProducer = kafkaProducer;
    }

    @CircuitBreaker(name = "billingService", fallbackMethod = "billingServiceFallback")
    @Retry(name = "billingServiceRetry")
    public BillingResponse createBillingAccount(String patientId, String name, String email) {
        BillingRequest billingRequest = BillingRequest.newBuilder()
                .setPatientId(patientId)
                .setName(name)
                .setEmail(email)
                .build();
        log.info("Request to create billing account received for {}", billingRequest);
        BillingResponse billingResponse = billingServiceBlockingStub.createBillingAccount(billingRequest);
        log.info("Billing account created and received response {}", billingResponse);
        return billingResponse;
    }

    public BillingResponse billingServiceFallback(String patientId, String name, String email, Throwable throwable) {
        log.info("Request to create billing account failed for patient id {} with error {}. Fallback triggered",
                patientId, throwable.getMessage());
        kafkaProducer.sendCreateBillingAccountEvent(patientId, name, email);
        log.info("Create billing account event created for patient with id {}", patientId);
        BillingResponse billingResponse = BillingResponse.newBuilder()
                .setAccountId("")
                .setStatus("PENDING")
                .build();
        return billingResponse;
    }
}

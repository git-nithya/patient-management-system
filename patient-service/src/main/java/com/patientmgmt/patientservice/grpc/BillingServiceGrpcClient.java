package com.patientmgmt.patientservice.grpc;

import com.patientmgmt.billingservice.BillingRequest;
import com.patientmgmt.billingservice.BillingResponse;
import com.patientmgmt.billingservice.BillingServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceGrpcClient {

    private static final Logger log = LoggerFactory.getLogger(BillingServiceGrpcClient.class);
    private final BillingServiceGrpc.BillingServiceBlockingStub billingServiceBlockingStub;

    @Autowired
    public BillingServiceGrpcClient(BillingServiceGrpc.BillingServiceBlockingStub billingServiceBlockingStub) {
        this.billingServiceBlockingStub = billingServiceBlockingStub;
    }

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
}

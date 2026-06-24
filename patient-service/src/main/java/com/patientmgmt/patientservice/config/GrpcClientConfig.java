package com.patientmgmt.patientservice.config;

import com.patientmgmt.billingservice.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    @Bean
    public ManagedChannel billingServiceChannel() {
        return ManagedChannelBuilder
                .forAddress("billing-service", 9001)
                .usePlaintext()
                .build();
    }

    @Bean
    public BillingServiceGrpc.BillingServiceBlockingStub billingServiceBlockingStub(ManagedChannel billingServiceChannel) {
        return BillingServiceGrpc.newBlockingStub(billingServiceChannel);
    }
}

package com.patientmgmt.billing_service.grpc;

import com.patientmgmt.billingservice.BillingRequest;
import com.patientmgmt.billingservice.BillingResponse;
import com.patientmgmt.billingservice.BillingServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class BillingServiceImpl extends BillingServiceGrpc.BillingServiceImplBase {


    private static final Logger log = LoggerFactory.getLogger(BillingServiceImpl.class);

    @Override
    public void createBillingAccount(BillingRequest request, StreamObserver<BillingResponse> responseObserver) {
        log.info("Creating billing account for {}", request);
        //business logic like database connections and calculations
        if (request.getPatientId().isEmpty()) {
           responseObserver.onError(Status.NOT_FOUND
                   .withDescription("Patient not found")
                   .asRuntimeException());
           return;
        }
        BillingResponse billingResponse = BillingResponse.newBuilder()
                .setAccountId("ACC-" + request.getPatientId())
                .setStatus("ACTIVE")
                .build();
        responseObserver.onNext(billingResponse);
        responseObserver.onCompleted();
    }
}

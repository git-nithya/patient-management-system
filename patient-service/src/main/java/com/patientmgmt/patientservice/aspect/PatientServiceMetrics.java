package com.patientmgmt.patientservice.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PatientServiceMetrics {

    private final MeterRegistry meterRegistry;

    public PatientServiceMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Around("execution(* com.patientmgmt.patientservice.service.PatientService.getAllPagedPatients(..))")
    public Object registerCacheMiss(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        meterRegistry.counter("redis.cache.miss", "api-cache", "patients").increment();
        Object result = proceedingJoinPoint.proceed();
        return result;
    }
}

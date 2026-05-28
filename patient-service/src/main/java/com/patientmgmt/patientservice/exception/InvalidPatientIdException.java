package com.patientmgmt.patientservice.exception;

public class InvalidPatientIdException extends RuntimeException {

    public InvalidPatientIdException(String message) {
        super(message);
    }
}

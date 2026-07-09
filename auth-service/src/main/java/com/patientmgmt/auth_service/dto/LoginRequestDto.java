package com.patientmgmt.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LoginRequestDto {
    @NotNull(message = "Email must be provided")
    @Email(message = "Email must be a valid one")
    private String email;

    @NotNull
    @Size(min = 8, message = "Password must be minimum 8 character long")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

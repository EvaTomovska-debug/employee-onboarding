package com.employee.onboarding.demo.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class CustomErrorResponse  {

    @JsonProperty("error_code")
    private int error;
    @JsonProperty("message")
    private String message;

    public CustomErrorResponse(int error, String message) {
        this.error = error;
        this.message = message;
    }
}

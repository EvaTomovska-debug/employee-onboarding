package com.employee.onboarding.demo.fegin;

import feign.Logger;
import feign.Retryer;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

public class EmployeeOnboardingApprovalClientConfig {

    @Value("${onboarding.approval.client.username}")
    private String username;

    @Value("${onboarding.approval.client.password}")
    private String password;

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor(username, password);
    }

    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(
                TimeUnit.SECONDS.toMillis(1),
                TimeUnit.SECONDS.toMillis(20),
                5
        );
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new RetrieveMessageErrorDecoder();
    }
}


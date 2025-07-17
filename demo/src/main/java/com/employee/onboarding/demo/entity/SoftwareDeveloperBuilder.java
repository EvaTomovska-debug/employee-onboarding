package com.employee.onboarding.demo.entity;

import com.employee.onboarding.demo.controller.dto.CreateEmployeeRequest;

import java.util.HashSet;
import java.util.List;

public class SoftwareDeveloperBuilder {
    private final SoftwareDeveloper dev = new SoftwareDeveloper();

    public SoftwareDeveloperBuilder withSkills(List<String> skills) {
        dev.setSkills(new HashSet<>(skills));
        return this;
    }

    public SoftwareDeveloperBuilder withProgramingLanguage(String programingLanguage) {
        dev.setProgrammingLanguage(programingLanguage);
        return this;
    }

    public SoftwareDeveloperBuilder withCommonFields(CreateEmployeeRequest req) {
        dev.setEmail(req.getEmail());
        dev.setLastName(req.getLastName());
        dev.setAddress(req.getAddress());
        dev.setFirstName(req.getFirstName());
        return this;
    }

    public SoftwareDeveloper build() {
        return dev;
    }
}
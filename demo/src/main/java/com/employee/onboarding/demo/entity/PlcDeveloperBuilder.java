package com.employee.onboarding.demo.entity;

import com.employee.onboarding.demo.controller.dto.CreateEmployeeRequest;

public class PlcDeveloperBuilder {
    private final PlcDeveloper plc = new PlcDeveloper();

    public PlcDeveloperBuilder withPlcCertification(String cert) {
        plc.setCertificationLevel(cert);
        return this;
    }

    public PlcDeveloperBuilder withCommonFields(CreateEmployeeRequest req) {
        plc.setEmail(req.getEmail());
        plc.setLastName(req.getLastName());
        plc.setAddress(req.getAddress());
        return this;
    }

    public PlcDeveloper build() {
        return plc;
    }
}

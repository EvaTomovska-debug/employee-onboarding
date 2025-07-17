package com.employee.onboarding.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "plc_developer")
public class PlcDeveloper extends Employee {

    private String plcTool;

    private String certificationLevel;

}

package com.employee.onboarding.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "software_developer")
@AllArgsConstructor
@NoArgsConstructor
public class SoftwareDeveloper extends Employee {

    private String programmingLanguage;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "software_dev_skills", joinColumns = @JoinColumn(name = "software_developer_id"))
    @Column(name = "skill")
    private Set<String> skills;

}

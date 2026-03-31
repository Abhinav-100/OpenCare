package com.ciphertext.opencarebackend.entity;

import com.ciphertext.opencarebackend.enums.ConditionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "health_condition")
public class HealthCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_type", nullable = false, length = 20)
    private ConditionType conditionType;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "severity", length = 20)
    private String severity;

    @Column(name = "reaction", columnDefinition = "TEXT")
    private String reaction;

    @Column(name = "diagnosis_date")
    private LocalDate diagnosisDate;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "icd_code", length = 10)
    private String icdCode;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
}
package com.ciphertext.opencarebackend.entity;

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
@Table(name = "health_medication")
public class HealthMedication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Column(name = "medication_name", nullable = false, length = 100)
    private String medicationName;

    @Column(name = "rxnorm_code", length = 10)
    private String rxnormCode;

    @Column(name = "dosage_amount", length = 20)
    private String dosageAmount;

    @Column(name = "dosage_unit", length = 20)
    private String dosageUnit;

    @Column(name = "frequency", length = 50)
    private String frequency;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescribed_by")
    private Doctor prescribedBy;

    @Column(name = "prescription_notes", columnDefinition = "TEXT")
    private String prescriptionNotes;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "last_taken")
    private LocalDateTime lastTaken;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
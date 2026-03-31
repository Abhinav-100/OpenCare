package com.ciphertext.opencarebackend.entity;

import com.ciphertext.opencarebackend.enums.EncounterType;
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
@Table(name = "health_encounter")
public class HealthEncounter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Enumerated(EnumType.STRING)
    @Column(name = "encounter_type", nullable = false, length = 50)
    private EncounterType encounterType;

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Column(name = "location", length = 100)
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @Column(name = "diagnosis_summary", columnDefinition = "TEXT")
    private String diagnosisSummary;

    @Column(name = "procedures", columnDefinition = "TEXT")
    private String procedures;

    @Column(name = "follow_up_required")
    private Boolean followUpRequired = false;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @Column(name = "report_link", columnDefinition = "TEXT")
    private String reportLink;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    @PrePersist
    protected void onCreate() {
        this.recordedAt = LocalDateTime.now();
    }
}
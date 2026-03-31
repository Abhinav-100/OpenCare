package com.ciphertext.opencarebackend.modules.shared.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "reports")
public class HmsReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lab_test_id", nullable = false)
    private Long labTestId;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "uploaded_by_user_id")
    private Long uploadedByUserId;

    @Column(name = "report_type", nullable = false, length = 50)
    private String reportType;

    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    @Column(name = "summary")
    private String summary;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "AVAILABLE";

    @Column(name = "reported_at", nullable = false)
    private LocalDateTime reportedAt = LocalDateTime.now();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
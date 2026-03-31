package com.ciphertext.opencarebackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "hospital_medical_test", indexes = {
        @Index(name = "idx_hospital_medical_test_hospital", columnList = "hospital_id"),
        @Index(name = "idx_hospital_medical_test_test", columnList = "medical_test_id")
})
public class HospitalMedicalTest extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "hospital_id", insertable = false, updatable = false)
    private Long hospitalId;

    @ManyToOne
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;


    @Size(max = 150)

    @Column(name = "name", length = 150)
    private String name;

    @Size(max = 50)

    @Column(name = "test_code",  unique = true, length = 50)
    private String testCode;

    @Column(name = "medical_test_id")
    private Integer medicalTestId;

    @ManyToOne
    @JoinColumn(name = "medical_test_id", insertable=false, updatable=false)
    private MedicalTest medicalTest;

    @Size(max = 100)
    @Column(name = "category", length = 100)
    private String category;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Size(max = 50)
    @Column(name = "duration", length = 50)
    private Integer duration;

    @Size(max = 500)
    @Column(name = "preparation_instructions", length = 500)
    private String preparationInstructions;

    @ColumnDefault("true")
    @Column(name = "is_available")
    private Boolean isAvailable;

    @Size(max = 200)
    @Column(name = "equipment", length = 200)
    private String equipment;

    @ColumnDefault("false")
    @Column(name = "is_specialist_required")
    private Boolean isSpecialistRequired;


    @NotNull
    @Column(name = "processing_time_minutes", nullable = false)
    @ColumnDefault("120")
    private Integer processingTimeMinutes = 120;

    // When sample was collected (stored in DB)
    @Column(name = "sample_collected_time")
    private LocalDateTime sampleCollectedTime;

    // Expected delivery time (calculated, stored in DB)
    @Column(name = "delivery_time")
    private LocalDateTime deliveryTime;

    @NotNull
    @ColumnDefault("true")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Transient
    public void calculateDeliveryTime() {
        if (sampleCollectedTime != null && processingTimeMinutes != null) {
            this.deliveryTime = sampleCollectedTime.plusMinutes(processingTimeMinutes);
        }
    }


    public void setSampleCollectedTime(LocalDateTime sampleCollectedTime) {
        this.sampleCollectedTime = sampleCollectedTime;
        calculateDeliveryTime();
    }

    public void setProcessingTimeMinutes(Integer processingTimeMinutes) {
        this.processingTimeMinutes = processingTimeMinutes;
        if (this.sampleCollectedTime != null) {
            calculateDeliveryTime();
        }
    }

    @PrePersist
    @PreUpdate
    public void onSave() {
        calculateDeliveryTime();
    }

}
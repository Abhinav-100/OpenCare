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
@Table(name = "hospital_amenity", indexes = {
        @Index(name = "idx_hospital_amenity_hospital", columnList = "hospital_id")
})
public class HospitalAmenity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @Size(max = 50)
    @Column(name = "type", length = 50)
    private String type;

    @Size(max = 100)
    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @ColumnDefault("1")
    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "available")
    private Integer available;

    @NotNull
    @ColumnDefault("true")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

    @Size(max = 100)
    @NotNull
    @ColumnDefault("'admin'")
    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Size(max = 100)
    @NotNull
    @ColumnDefault("'admin'")
    @Column(name = "updated_by", nullable = false, length = 100)
    private String updatedBy;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}
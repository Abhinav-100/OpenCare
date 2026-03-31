package com.ciphertext.opencarebackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "blood_bank")
public class BloodBank extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "license_no", unique = true, length = 100)
    private String licenseNo;

    @Column(name = "contact_number", length = 50)
    private String contactNumber;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "opening_hours", length = 255)
    private String openingHours;

    @Column(name = "is_always_open", nullable = false)
    private Boolean isAlwaysOpen = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "established_date")
    private LocalDate establishedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "social_org_id")
    private SocialOrganization socialOrganization;
}
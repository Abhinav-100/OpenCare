package com.ciphertext.opencarebackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Sadman
 */
@Getter
@Setter
@Entity
@Table(name="doctor", indexes = {
    @Index(name = "idx_doctor_bmdc_no", columnList = "bmdc_no"),
    @Index(name = "idx_doctor_verification", columnList = "is_verified, is_active"),
    @Index(name = "idx_doctor_profile", columnList = "profile_id"),
    @Index(name = "idx_doctor_hospital", columnList = "hospital_id")
})
public class Doctor extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="bmdc_no")
    private String bmdcNo;

    @Column(name="degrees")
    private String degrees;

    @Column(name="specializations")
    private String specializations;

    @Column(name="start_date")
    private LocalDate startDate;

    @Column(name="description")
    private String description;

    @Column(name="consultation_fee_online", precision = 10, scale = 2)
    private BigDecimal consultationFeeOnline;

    @Column(name="consultation_fee_offline", precision = 10, scale = 2)
    private BigDecimal consultationFeeOffline;

    @Column(name="is_verified")
    private Boolean isVerified = false;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @ManyToMany
    @JoinTable(
            name = "doctor_association",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "association_id")
    )
    private List<Association> associations;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "doctor_tag",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"),
            indexes = {
                @Index(name = "idx_doctor_tag_doctor", columnList = "doctor_id"),
                @Index(name = "idx_doctor_tag_tag", columnList = "tag_id")
            }
    )
    private Set<Tag> tags = new HashSet<>();
}
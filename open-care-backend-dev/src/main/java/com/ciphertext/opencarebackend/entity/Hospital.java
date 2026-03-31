package com.ciphertext.opencarebackend.entity;

import com.ciphertext.opencarebackend.enums.HospitalType;
import com.ciphertext.opencarebackend.enums.OrganizationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;


/**
 * @author Sadman
 */
@Getter
@Setter
@Entity
@Table(name="hospital")
public class Hospital extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="bn_name")
    private String bnName;

    @Column(name = "registration_code")
    private String registrationCode;

    @Column(name = "slug")
    private String slug;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name="number_of_bed", nullable = false)
    private Integer numberOfBed;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "upazila_id")
    private Upazila upazila;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "union_id")
    private Union union;

    @Enumerated(EnumType.STRING)
    @Column(name = "hospital_type", nullable = false)
    private HospitalType hospitalType;

    @Enumerated(EnumType.STRING)
    @Column(name = "organization_type", nullable = false)
    private OrganizationType organizationType;

    @Column(name = "lat", precision = 12, scale = 9)
    private BigDecimal lat;

    @Column(name = "lon", precision = 12, scale = 9)
    private BigDecimal lon;

    @Column(name = "facebook_page_url")
    private String facebookPageUrl;

    @Column(name = "twitter_profile_url")
    private String twitterProfileUrl;

    @Column(name="website_url")
    private String websiteUrl;

    @Column(name="email")
    private String email;

    @Column(name="phone")
    private String phone;

    @Column(name="address")
    private String address;

    @Column(name="has_emergency_service", nullable = false)
    private Boolean hasEmergencyService = false;

    @Column(name="has_ambulance_service", nullable = false)
    private Boolean hasAmbulanceService = false;

    @Column(name="has_blood_bank", nullable = false)
    private Boolean hasBloodBank = false;

    @Column(name="is_affiliated", nullable = false)
    private Boolean isAffiliated = false;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @Column(name="is_active", nullable = false)
    private Boolean isActive = true;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "hospital_tag",
            joinColumns = @JoinColumn(name = "hospital_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"),
            indexes = {
                @Index(name = "idx_hospital_tag_hospital", columnList = "hospital_id"),
                @Index(name = "idx_hospital_tag_tag", columnList = "tag_id")
            }
    )
    private Set<Tag> tags = new HashSet<>();
}
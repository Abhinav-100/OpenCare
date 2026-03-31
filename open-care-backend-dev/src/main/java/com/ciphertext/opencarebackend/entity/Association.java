package com.ciphertext.opencarebackend.entity;

import com.ciphertext.opencarebackend.enums.AssociationType;
import com.ciphertext.opencarebackend.enums.Country;
import com.ciphertext.opencarebackend.enums.Domain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "association")
public class Association extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;

    @Column(name = "bn_name", length = 100)
    private String bnName;

    @Column(name = "short_name", length = 50)
    private String shortName;

    @Column(name = "type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private AssociationType associationType;

    @ManyToOne
    @JoinColumn(name = "medical_speciality_id")
    private MedicalSpeciality medicalSpeciality;

    @Column(name = "domain", length = 50)
    @Enumerated(EnumType.STRING)
    private Domain domain;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @Column(name = "founded_date")
    private LocalDate foundedDate;

    @Column(name = "website_url", length = 255)
    private String websiteUrl;

    @Column(name = "facebook_url", length = 255)
    private String facebookUrl;

    @Column(name = "twitter_url", length = 255)
    private String twitterUrl;

    @Column(name = "linkedin_url", length = 255)
    private String linkedinUrl;

    @Column(name = "youtube_url", length = 255)
    private String youtubeUrl;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @ManyToOne
    @JoinColumn(name = "division_id")
    private Division division;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    @ManyToOne
    @JoinColumn(name = "upazila_id")
    private Upazila upazila;

    @Column(name = "origin_country", nullable = false)
    @Enumerated(EnumType.STRING)
    private Country originCountry = Country.INDIA;

    @Column(name = "is_affiliated", nullable = false)
    private Boolean isAffiliated = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
package com.ciphertext.opencarebackend.entity;

import com.ciphertext.opencarebackend.enums.Country;
import com.ciphertext.opencarebackend.enums.InstitutionType;
import com.ciphertext.opencarebackend.enums.OrganizationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;

@Getter
@Setter
@Entity
@Table(name = "institution")
public class Institution extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "acronym", length = 10)
    private String acronym;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "bn_name", nullable = false, length = 100)
    private String bnName;

    @Column(name = "image_url", length = 250)
    private String imageUrl;

    @Column(name = "established_year")
    private Integer establishedYear;

    @Column(name = "enroll")
    private Integer enroll;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "district_id")
    private District district;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "upazila_id")
    private Upazila upazila;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "affiliated_hospital_id")
    private Hospital affiliatedHospital;

    @Column(name = "country", length = 100)
    @Enumerated(EnumType.STRING)
    private Country country;

    @Enumerated(EnumType.STRING)
    @Column(name = "institution_type", nullable = false, length = 50)
    private InstitutionType institutionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "organization_type", nullable = false, length = 50)
    private OrganizationType organizationType;

    @Column(name = "lat", nullable = false, precision = 12, scale = 9)
    private BigDecimal lat;

    @Column(name = "lon", nullable = false, precision = 12, scale = 9)
    private BigDecimal lon;

    @Column(name = "location", columnDefinition = "geography(Point,4326)")
    private Point location;

    @Column(name = "website_url", nullable = false, length = 500)
    private String websiteUrl;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 100)
    private String phone;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "is_affiliated", nullable = false)
    private boolean affiliated = false;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "institution_tag",
            joinColumns = @JoinColumn(name = "institution_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"),
            indexes = {
                @Index(name = "idx_institution_tag_org", columnList = "institution_id"),
                @Index(name = "idx_institution_tag_tag", columnList = "tag_id")
            }
    )
    private Set<Tag> tags = new HashSet<>();
}
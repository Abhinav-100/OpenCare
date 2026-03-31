package com.ciphertext.opencarebackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@Entity
@Table(name = "nurse")
public class Nurse extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bnmc_no", unique = true, length = 100)
    private String bnmcNo;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "description")
    private String description;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "is_affiliated")
    private Boolean isAffiliated = false;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id")
    private Profile profile;
}
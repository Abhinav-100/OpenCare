package com.ciphertext.opencarebackend.entity;

import com.ciphertext.opencarebackend.enums.MembershipType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * @author Sadman
 */
@Getter
@Setter
@Entity
@Table(name = "doctor_association")
public class DoctorAssociation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "association_id")
    private Association association;

    @Column(name = "membership_type", length = 50)
    @Enumerated(EnumType.STRING)
    private MembershipType membershipType;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
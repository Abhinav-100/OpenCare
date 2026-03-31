package com.ciphertext.opencarebackend.entity;

import com.ciphertext.opencarebackend.enums.Country;
import com.ciphertext.opencarebackend.enums.SocialOrganizationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Sadman
 */
@Getter
@Setter
@Entity
@Table(name="social_organization")
public class SocialOrganization extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="bn_name")
    private String bnName;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_organization_type", nullable = false)
    private SocialOrganizationType socialOrganizationType;

    @Column(name="founded_date")
    private LocalDateTime foundedDate;

    @Column(name="description")
    private String description;

    @Column(name="address")
    private String address;

    @Column(name="website_url")
    private String websiteUrl;

    @Column(name="facebook_url")
    private String facebookUrl;

    @Column(name="twitter_url")
    private String twitterUrl;

    @Column(name="linkedin_url")
    private String linkedinUrl;

    @Column(name="youtube_url")
    private String youtubeUrl;

    @Column(name="email")
    private String email;

    @Column(name="phone")
    private String phone;

    @Column(name="origin_country")
    @Enumerated(EnumType.STRING)
    private Country originCountry;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "social_organization_tag",
            joinColumns = @JoinColumn(name = "social_organization_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"),
            indexes = {
                    @Index(name = "idx_organization_tag_org", columnList = "social_organization_id"),
                    @Index(name = "idx_organization_tag_tag", columnList = "tag_id")
            }
    )
    private Set<Tag> tags = new HashSet<>();
}
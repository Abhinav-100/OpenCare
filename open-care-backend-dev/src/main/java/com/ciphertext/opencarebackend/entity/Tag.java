package com.ciphertext.opencarebackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tag", indexes = {
    @Index(name = "idx_tag_name", columnList = "name"),
    @Index(name = "idx_tag_category", columnList = "category"),
    @Index(name = "idx_tag_search", columnList = "name, category"),
    @Index(name = "idx_tag_display_name", columnList = "display_name")
})
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    public String getDisplayName() {
        return displayName != null ? displayName : name;
    }
}

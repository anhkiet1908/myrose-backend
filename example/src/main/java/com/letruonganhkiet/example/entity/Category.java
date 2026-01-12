package com.letruonganhkiet.example.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    private String description;

    private String imageUrl;

    // ======== Danh mục cha (Self-Reference) ========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnoreProperties({"subCategories", "products"})
    private Category parentCategory;

    // ======== Danh mục con ========
    @OneToMany(mappedBy = "parentCategory", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Category> subCategories;

    // ======== Quan hệ với Product ========
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @JsonIgnore // tránh vòng lặp JSON khi GET product
    private List<Product> products;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

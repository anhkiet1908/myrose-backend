package com.letruonganhkiet.example.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "promotions")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;

    private LocalDate startDate;
    private LocalDate endDate;

    private Boolean isActive = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

  @ManyToMany
@JoinTable(
    name = "promotion_products",
    joinColumns = @JoinColumn(name = "promotion_id"),
    inverseJoinColumns = @JoinColumn(name = "product_id")
)
@JsonIgnoreProperties(value = {"promotions", "category"}, allowSetters = true)
private List<Product> products = new ArrayList<>();
}

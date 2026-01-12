package com.letruonganhkiet.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false, unique = true)
    private ERole name; // enum ROLE_USER, ROLE_EMPLOYEE, ROLE_ADMIN

    // Constructor không tham số
    public Role() {}

    // Constructor có tham số
    public Role(ERole name) {
        this.name = name;
    }

    // --- Getters & Setters ---
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ERole getName() {
        return name;
    }

    public void setName(ERole name) {
        this.name = name;
    }
}

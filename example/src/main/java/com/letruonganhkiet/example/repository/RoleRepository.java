package com.letruonganhkiet.example.repository;

import com.letruonganhkiet.example.model.ERole;
import com.letruonganhkiet.example.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);

}

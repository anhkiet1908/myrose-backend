package com.letruonganhkiet.example.controllers;

import com.letruonganhkiet.example.entity.Promotion;
import com.letruonganhkiet.example.security.services.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public ResponseEntity<List<Promotion>> getAll() {
        return ResponseEntity.ok(promotionService.getAll());
    }

    // ✅ GET theo ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(promotionService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Promotion not found: " + e.getMessage());
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN')")
    public ResponseEntity<?> create(@RequestBody Promotion promotion) {
        try {
            return ResponseEntity.ok(promotionService.create(promotion));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating promotion: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Promotion promotion) {
        try {
            return ResponseEntity.ok(promotionService.update(id, promotion));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating promotion: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            promotionService.delete(id);
            return ResponseEntity.ok("Promotion deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting promotion: " + e.getMessage());
        }
    }
}
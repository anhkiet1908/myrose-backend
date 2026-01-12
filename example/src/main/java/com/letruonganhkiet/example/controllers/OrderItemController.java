package com.letruonganhkiet.example.controllers;

import com.letruonganhkiet.example.entity.OrderItem;
import com.letruonganhkiet.example.security.services.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    @Autowired
    private OrderItemService itemService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public List<OrderItem> getAll() {
        return itemService.getAll();
    }

    // ✅ GET theo ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(itemService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body("OrderItem not found: " + e.getMessage());
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN')")
    public ResponseEntity<?> create(@RequestBody OrderItem item) {
        try {
            return ResponseEntity.ok(itemService.create(item));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating item: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody OrderItem item) {
        try {
            return ResponseEntity.ok(itemService.update(id, item));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating item: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            itemService.delete(id);
            return ResponseEntity.ok("Item deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting item: " + e.getMessage());
        }
    }
}
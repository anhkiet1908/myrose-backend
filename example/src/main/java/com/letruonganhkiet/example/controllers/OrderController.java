package com.letruonganhkiet.example.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.letruonganhkiet.example.entity.Order;
import com.letruonganhkiet.example.security.services.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public List<Order> getAll() {
        return orderService.getAll();
    }

    // ✅ GET theo ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(orderService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Order not found: " + e.getMessage());
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN')")
    public ResponseEntity<?> create(@RequestBody Order order) {
        try {
            return ResponseEntity.ok(orderService.create(order));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating order: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Order order) {
        try {
            return ResponseEntity.ok(orderService.update(id, order));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating order: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            orderService.delete(id);
            return ResponseEntity.ok("Order deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting order: " + e.getMessage());
        }
    }
}
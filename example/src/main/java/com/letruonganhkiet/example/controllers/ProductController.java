package com.letruonganhkiet.example.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.letruonganhkiet.example.entity.Product;
import com.letruonganhkiet.example.security.services.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // GET: ai cũng xem được
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public List<Product> getAll() {
        return productService.getAll();
    }

    // ✅ GET theo ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public Product getById(@PathVariable Long id) {
        return productService.findById(id);
    }

    // POST: chỉ nhân viên và admin
    @PostMapping
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN')")
    public Product create(@RequestBody Product product) {
        return productService.create(product);
    }

    // PUT: chỉ nhân viên và admin
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN')")
    public Product update(@PathVariable Long id, @RequestBody Product product) {
        return productService.update(id, product);
    }

    // DELETE: chỉ admin
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}
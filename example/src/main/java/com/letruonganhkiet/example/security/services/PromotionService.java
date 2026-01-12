package com.letruonganhkiet.example.security.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.letruonganhkiet.example.entity.Product;
import com.letruonganhkiet.example.entity.Promotion;
import com.letruonganhkiet.example.repository.ProductRepository;
import com.letruonganhkiet.example.repository.PromotionRepository;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<Promotion> getAll() {
        List<Promotion> promotions = promotionRepository.findAll();
        promotions.forEach(p -> p.getProducts().size()); // tránh lazy-loading
        return promotions;
    }

    // ✅ Lấy theo ID
    @Transactional(readOnly = true)
    public Promotion findById(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found: " + id));
        promotion.getProducts().size(); // tránh lazy-loading
        return promotion;
    }

    @Transactional
    public Promotion create(Promotion promotion) {
        if (promotion.getProducts() != null && !promotion.getProducts().isEmpty()) {
            List<Long> productIds = promotion.getProducts().stream()
                    .map(Product::getId)
                    .toList();
            var products = productRepository.findAllById(productIds);
            promotion.setProducts(products);
        }

        promotion.setCreatedAt(LocalDateTime.now());
        promotion.setUpdatedAt(LocalDateTime.now());
        return promotionRepository.save(promotion);
    }

    @Transactional
    public Promotion update(Long id, Promotion promotion) {
        Promotion existing = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found: " + id));

        existing.setName(promotion.getName());
        existing.setDiscountPercentage(promotion.getDiscountPercentage());
        existing.setDiscountAmount(promotion.getDiscountAmount());
        existing.setStartDate(promotion.getStartDate());
        existing.setEndDate(promotion.getEndDate());
        existing.setIsActive(promotion.getIsActive());
        existing.setUpdatedAt(LocalDateTime.now());

        existing.getProducts().clear();
        List<Product> updatedProducts = new ArrayList<>();

        if (promotion.getProducts() != null) {
            for (Product p : promotion.getProducts()) {
                Product product = productRepository.findById(p.getId())
                        .orElseThrow(() -> new RuntimeException("Product not found: " + p.getId()));
                updatedProducts.add(product);

                if (!product.getPromotions().contains(existing)) {
                    product.getPromotions().add(existing);
                }
            }
        }

        existing.setProducts(updatedProducts);
        return promotionRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found: " + id));

        for (Product p : promotion.getProducts()) {
            p.getPromotions().remove(promotion);
        }

        promotionRepository.deleteById(id);
    }
}
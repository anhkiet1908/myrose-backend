package com.letruonganhkiet.example.security.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.letruonganhkiet.example.entity.Order;
import com.letruonganhkiet.example.entity.OrderItem;
import com.letruonganhkiet.example.entity.Product;
import com.letruonganhkiet.example.repository.OrderItemRepository;
import com.letruonganhkiet.example.repository.OrderRepository;
import com.letruonganhkiet.example.repository.ProductRepository;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository itemRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private OrderRepository orderRepo;

    @Transactional(readOnly = true)
    public List<OrderItem> getAll() {
        return itemRepo.findAll();
    }

    // ✅ Lấy theo ID
    @Transactional(readOnly = true)
    public OrderItem findById(Long id) {
        return itemRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("OrderItem not found: " + id));
    }

    @Transactional
    public OrderItem create(OrderItem item) {
        Order order = orderRepo.findById(item.getOrder().getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));
        item.setOrder(order);

        Product product = productRepo.findById(item.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        item.setProduct(product);

        item.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());

        return itemRepo.save(item);
    }

    @Transactional
    public OrderItem update(Long id, OrderItem item) {
        OrderItem existing = findById(id);

        Product product = productRepo.findById(item.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        existing.setProduct(product);

        existing.setQuantity(item.getQuantity());
        existing.setPrice(item.getPrice());
        existing.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        existing.setUpdatedAt(LocalDateTime.now());

        return itemRepo.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!itemRepo.existsById(id)) {
            throw new RuntimeException("OrderItem not found: " + id);
        }
        itemRepo.deleteById(id);
    }
}
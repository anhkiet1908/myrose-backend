package com.letruonganhkiet.example.security.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.letruonganhkiet.example.entity.Order;
import com.letruonganhkiet.example.entity.OrderItem;
import com.letruonganhkiet.example.entity.Promotion;
import com.letruonganhkiet.example.repository.OrderRepository;
import com.letruonganhkiet.example.repository.PromotionRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepo;


    @Autowired
    private PromotionRepository promoRepo;

    @Transactional(readOnly = true)
    public List<Order> getAll() {
        return orderRepo.findAll();
    }

    // ✅ Lấy theo ID
    @Transactional(readOnly = true)
    public Order findById(Long id) {
        return orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }

  @Transactional
public Order create(Order order) {

    if (order.getPromotion() != null) {
        Promotion promo = promoRepo.findById(order.getPromotion().getId())
                .orElseThrow(() -> new RuntimeException("Promotion not found"));
        order.setPromotion(promo);
    }

    BigDecimal totalAmount = BigDecimal.ZERO;
    if (order.getItems() != null) {
        List<OrderItem> items = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            item.setOrder(order);
            item.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            totalAmount = totalAmount.add(item.getSubtotal());
            items.add(item);
        }
        order.setItems(items);
    }

    order.setTotalAmount(totalAmount);
    order.setCreatedAt(LocalDateTime.now());
    order.setUpdatedAt(LocalDateTime.now());

    return orderRepo.save(order);
}

    @Transactional
public Order update(Long id, Order order) {
    Order existing = findById(id);

    if (order.getPromotion() != null) {
        Promotion promo = promoRepo.findById(order.getPromotion().getId())
                .orElseThrow(() -> new RuntimeException("Promotion not found"));
        existing.setPromotion(promo);
    } else {
        existing.setPromotion(null);
    }

    existing.setEmployee(order.getEmployee());
    existing.setStatus(order.getStatus());
    existing.setNotes(order.getNotes());
    existing.setUpdatedAt(LocalDateTime.now());

    if (order.getItems() != null) {
        existing.getItems().clear();
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            item.setOrder(existing);
            item.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            totalAmount = totalAmount.add(item.getSubtotal());
            items.add(item);
        }
        existing.setItems(items);
        existing.setTotalAmount(totalAmount);
    }

    return orderRepo.save(existing);
}


    @Transactional
    public void delete(Long id) {
        Order order = findById(id);
        orderRepo.delete(order);
    }
}
package com.letruonganhkiet.example.security.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letruonganhkiet.example.entity.Bill;
import com.letruonganhkiet.example.entity.Order;
import com.letruonganhkiet.example.entity.enums.PaymentMethod;
import com.letruonganhkiet.example.entity.enums.PaymentStatus;
import com.letruonganhkiet.example.repository.BillRepository;
import com.letruonganhkiet.example.repository.OrderRepository;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepo;

    @Autowired
    private OrderRepository orderRepo;

    public List<Bill> getAll() {
        return billRepo.findAll();
    }

    // ✅ Lấy theo ID
    public Bill findById(Long id) {
        return billRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found with id: " + id));
    }

    public Bill create(Long orderId, PaymentMethod paymentMethod, PaymentStatus paymentStatus) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        Bill bill = new Bill();
        bill.setOrder(order);
        bill.setTotalAmount(order.getTotalAmount());
        bill.setPaymentMethod(paymentMethod);
        bill.setPaymentStatus(paymentStatus);
        bill.setCreatedAt(LocalDateTime.now());
        bill.setUpdatedAt(LocalDateTime.now());
        bill.setIssuedAt(LocalDateTime.now());

        return billRepo.save(bill);
    }

    public Bill update(Long id, Long orderId, PaymentMethod paymentMethod, PaymentStatus paymentStatus) {
        Bill existing = findById(id);

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        existing.setOrder(order);
        existing.setTotalAmount(order.getTotalAmount());
        existing.setPaymentMethod(paymentMethod);
        existing.setPaymentStatus(paymentStatus);
        existing.setUpdatedAt(LocalDateTime.now());

        return billRepo.save(existing);
    }

    public void delete(Long id) {
        if (!billRepo.existsById(id)) {
            throw new RuntimeException("Bill not found with id: " + id);
        }
        billRepo.deleteById(id);
    }
}
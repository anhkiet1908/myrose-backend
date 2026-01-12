package com.letruonganhkiet.example.controllers;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.letruonganhkiet.example.dto.BillDto;
import com.letruonganhkiet.example.entity.Bill;
import com.letruonganhkiet.example.entity.enums.PaymentMethod;
import com.letruonganhkiet.example.entity.enums.PaymentStatus;
import com.letruonganhkiet.example.security.services.BillService;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "*")
public class BillController {

    @Autowired
    private BillService billService;

    @Autowired(required = false)
    private OrderService orderService;      // OPTIONAL

    @Autowired(required = false)
    private SocketPublisher socket;         // OPTIONAL

    /* ===== GET ALL ===== */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public List<Bill> getAll() {
        return billService.getAll();
    }

    /* ===== CREATE ===== */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public ResponseEntity<Bill> create(@RequestBody Map<String, Object> body) {
        Long orderId = getAsLong(body, "sourceOrderId");
        if (orderId == null) orderId = getAsLong(body, "orderId");
        if (orderId == null) throw new RuntimeException("orderId/sourceOrderId is required");

        // --- Dạng A: payload cũ {orderId, paymentMethod, paymentStatus} ---
        if (body.containsKey("paymentMethod") && !body.containsKey("payment")) {
            String pmStr = String.valueOf(body.get("paymentMethod"));  // cash/card/bank...
            Object psObj = body.get("paymentStatus");                  // COMPLETED/PENDING/FAILED/PAID

            PaymentMethod pm = PaymentMethod.fromClientKey(pmStr);
            PaymentStatus ps = mapPaymentStatus(psObj, pm);

            Bill bill = billService.create(orderId, pm, ps);
            safeCloseOrderAndEmit(orderId, bill, pm.name(), ps);
            return ResponseEntity.ok(bill);
        }

        // --- Dạng B: payload mới có "payment" hoặc "status" ---
        String pmStr = "cash";
        if (body.containsKey("payment") && body.get("payment") instanceof Map) {
            Map<?,?> p = (Map<?,?>) body.get("payment");
            Object m = p.get("method");
            if (m != null) pmStr = String.valueOf(m);
        } else if (body.containsKey("paymentMethod")) {
            pmStr = String.valueOf(body.get("paymentMethod"));
        }
        PaymentMethod pm = PaymentMethod.fromClientKey(pmStr);

        PaymentStatus ps = mapPaymentStatus(body.get("status"), pm);

        Bill bill = billService.create(orderId, pm, ps);
        safeCloseOrderAndEmit(orderId, bill, pm.name(), ps);
        return ResponseEntity.ok(bill);
    }

    /* ===== UPDATE ===== */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN')")
    public ResponseEntity<Bill> update(@PathVariable Long id, @Valid @RequestBody BillDto dto) {
        Bill bill = billService.update(id, dto.getOrderId(), dto.getPaymentMethod(), dto.getPaymentStatus());
        return ResponseEntity.ok(bill);
    }

    /* ===== DELETE ===== */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        billService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /* ===== Helpers ===== */

    private Long getAsLong(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key) || map.get(key) == null) return null;
        Object v = map.get(key);
        if (v instanceof Number) return ((Number) v).longValue();
        try { return Long.parseLong(String.valueOf(v)); } catch (Exception e) { return null; }
    }

    /** Map status từ FE -> enum của bạn (PENDING/COMPLETED/FAILED).
     *  - CASH/CARD mà không gửi status => COMPLETED
     *  - "PAID"/"SUCCESS" => COMPLETED
     *  - "EXPIRED"/"CANCELLED" => FAILED
     */
    private PaymentStatus mapPaymentStatus(Object v, PaymentMethod method) {
        if (v == null) {
            return (method == PaymentMethod.CASH || method == PaymentMethod.CARD)
                   ? PaymentStatus.COMPLETED : PaymentStatus.PENDING;
        }
        String s = String.valueOf(v).trim().toUpperCase();
        if (s.equals("COMPLETED") || s.equals("PAID") || s.equals("SUCCESS")) return PaymentStatus.COMPLETED;
        if (s.equals("PENDING")   || s.equals("PROCESSING")) return PaymentStatus.PENDING;
        if (s.equals("FAILED")    || s.equals("FAIL") || s.equals("CANCELLED") || s.equals("CANCELED") || s.equals("EXPIRED"))
            return PaymentStatus.FAILED;
        return PaymentStatus.PENDING;
    }

    private void safeCloseOrderAndEmit(Long orderId, Bill bill, String paymentMethod, PaymentStatus ps) {
        // Đóng order nếu có service
        try {
            if (orderService != null && orderId != null && ps == PaymentStatus.COMPLETED) {
                orderService.close(orderId, "COMPLETED");
            }
        } catch (Exception ignored) {}

        // Bắn socket nếu có
        try {
            if (socket != null) {
                socket.emit("bill:created", Map.of(
                    "billId", bill.getId(),
                    "orderId", orderId,
                    "total", bill.getTotalAmount() != null ? bill.getTotalAmount() : 0L,
                    "paymentMethod", paymentMethod
                ));
                socket.emit("order:updated", Map.of("id", orderId, "status", ps.name()));
            }
        } catch (Exception ignored) {}
    }

    /* ===== OPTIONAL service interfaces ===== */
    public interface OrderService { void close(Long orderId, String status); }
    public interface SocketPublisher { void emit(String event, Object payload); }
}

package com.letruonganhkiet.example.entity.enums;

import java.util.Locale;

public enum PaymentMethod {
    CASH,           // Tiền mặt
    CARD,           // Thẻ POS
    VNPAY,          // QR/gateway VNPAY
    MOMO,           // Ví MoMo (nếu dùng)
    PAYOS,          // PayOS (nếu dùng)
    BANK_TRANSFER;  // Chuyển khoản tay (không qua cổng)

    /** Map từ key client (cash/card/bank/qr/...) hoặc tên gateway -> enum */
    public static PaymentMethod fromClientKey(String key) {
        if (key == null) return CASH;
        String k = key.trim().toUpperCase(Locale.ROOT);
        switch (k) {
            case "CASH": return CASH;
            case "CARD": return CARD;
            case "VNPAY": return VNPAY;
            case "MOMO": return MOMO;
            case "PAYOS": return PAYOS;

            // Các synonym phổ biến từ FE
            case "BANK":
            case "QR":
            case "BANK_TRANSFER":
            case "TRANSFER":
                // Nếu trên FE nút "bank" = quét QR qua VNPAY,
                // đổi return thành BANK_TRANSFER nếu bạn không dùng cổng thanh toán.
                return VNPAY;

            default:
                // fallback an toàn
                return CASH;
        }
    }
}

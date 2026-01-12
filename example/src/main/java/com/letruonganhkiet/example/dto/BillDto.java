package com.letruonganhkiet.example.dto;

import com.letruonganhkiet.example.entity.enums.PaymentMethod;
import com.letruonganhkiet.example.entity.enums.PaymentStatus;
import lombok.Data;

@Data
public class BillDto {
    private Long orderId;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
}

package com.payment.dto;

import com.payment.model.PaymentTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private UUID id;
    private UUID dealerId;
    private BigDecimal amount;
    private PaymentTransaction.PaymentMethod method;
    private PaymentTransaction.PaymentStatus status;
    private String requestId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package com.payment.controller;

import com.payment.dto.PaymentRequestDTO;
import com.payment.dto.PaymentResponseDTO;
import com.payment.service.PaymentTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentGatewayController {

    private final PaymentTransactionService paymentTransactionService;

    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponseDTO> initiatePayment(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody PaymentRequestDTO request) {
        
        String key = idempotencyKey != null ? idempotencyKey : UUID.randomUUID().toString();
        PaymentResponseDTO response = paymentTransactionService.initiatePayment(request, tenantId, key);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> getPaymentStatus(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @PathVariable UUID id) {
            
        PaymentResponseDTO response = paymentTransactionService.getPaymentStatus(id, tenantId);
        return ResponseEntity.ok(response);
    }
}

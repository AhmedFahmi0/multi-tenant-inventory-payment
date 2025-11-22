package com.payment.controller;

import com.payment.dto.PaymentRequestDTO;
import com.payment.dto.PaymentResponseDTO;
import com.payment.service.PaymentTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PaymentTransactionController {

    private final PaymentTransactionService paymentTransactionService;

    @PostMapping("/initiate")
    @Operation(summary = "Initiate a new payment transaction")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaymentResponseDTO> initiatePayment(
            @Valid @RequestBody PaymentRequestDTO request,
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        
        PaymentResponseDTO response = paymentTransactionService.initiatePayment(request, tenantId, idempotencyKey);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment status by ID")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaymentResponseDTO> getPaymentStatus(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-Id") String tenantId) {
        
        PaymentResponseDTO response = paymentTransactionService.getPaymentStatus(id, tenantId);
        return ResponseEntity.ok(response);
    }
}

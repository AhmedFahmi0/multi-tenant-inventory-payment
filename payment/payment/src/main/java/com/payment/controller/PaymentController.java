package com.payment.controller;

import com.payment.dto.PaymentRequest;
import com.payment.dto.PaymentResponse;
import com.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Create a new payment")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody PaymentRequest request,
            @RequestHeader("X-Tenant-Id") String tenantId) {
        return ResponseEntity.ok(paymentService.createPayment(request, tenantId));
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment by ID")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PaymentResponse> getPayment(
            @PathVariable UUID paymentId,
            @RequestHeader("X-Tenant-Id") String tenantId) {
        return ResponseEntity.ok(paymentService.getPayment(paymentId, tenantId));
    }

    @GetMapping
    @Operation(summary = "Get all payments with pagination")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Page<PaymentResponse>> getAllPayments(
            @RequestHeader("X-Tenant-Id") String tenantId,
            Pageable pageable) {
        return ResponseEntity.ok(paymentService.getAllPayments(tenantId, pageable));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payments by order ID")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<PaymentResponse>> getPaymentsByOrderId(
            @PathVariable String orderId,
            @RequestHeader("X-Tenant-Id") String tenantId,
            Pageable pageable) {
        return ResponseEntity.ok(paymentService.getPaymentsByOrderId(orderId, tenantId, pageable));
    }

    @PostMapping("/{paymentId}/process")
    @Operation(summary = "Process a payment")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PaymentResponse> processPayment(
            @PathVariable UUID paymentId,
            @RequestHeader("X-Tenant-Id") String tenantId) {
        return ResponseEntity.ok(paymentService.processPayment(paymentId, tenantId));
    }

    @PostMapping("/{paymentId}/refund")
    @Operation(summary = "Refund a payment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> refundPayment(
            @PathVariable UUID paymentId,
            @RequestHeader("X-Tenant-Id") String tenantId) {
        return ResponseEntity.ok(paymentService.refundPayment(paymentId, tenantId));
    }

    @GetMapping("/order/{orderId}/status")
    @Operation(summary = "Check if payment is processed for an order")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Boolean> isPaymentProcessed(
            @PathVariable String orderId,
            @RequestHeader("X-Tenant-Id") String tenantId) {
        return ResponseEntity.ok(paymentService.isPaymentProcessed(orderId, tenantId));
    }
}

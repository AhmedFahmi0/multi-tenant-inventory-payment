package com.payment.service;

import com.payment.dto.PaymentRequestDTO;
import com.payment.dto.PaymentResponseDTO;

import java.util.UUID;

public interface PaymentTransactionService {
    
    /**
     * Initiates a new payment transaction
     * 
     * @param request The payment request details
     * @param tenantId The tenant ID for multi-tenancy
     * @param idempotencyKey Unique key to ensure idempotency
     * @return The created payment transaction
     */
    PaymentResponseDTO initiatePayment(PaymentRequestDTO request, String tenantId, String idempotencyKey);
    
    /**
     * Retrieves the status of a payment transaction
     * 
     * @param paymentId The ID of the payment transaction
     * @param tenantId The tenant ID for multi-tenancy
     * @return The payment transaction details
     */
    PaymentResponseDTO getPaymentStatus(UUID paymentId, String tenantId);
}

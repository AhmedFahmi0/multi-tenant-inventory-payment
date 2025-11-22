package com.payment.service;

import com.payment.dto.PaymentRequest;
import com.payment.dto.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest request, String tenantId);
    PaymentResponse getPayment(UUID paymentId, String tenantId);
    Page<PaymentResponse> getAllPayments(String tenantId, Pageable pageable);
    Page<PaymentResponse> getPaymentsByOrderId(String orderId, String tenantId, Pageable pageable);
    PaymentResponse processPayment(UUID paymentId, String tenantId);
    PaymentResponse refundPayment(UUID paymentId, String tenantId);
    boolean isPaymentProcessed(String orderId, String tenantId);
}

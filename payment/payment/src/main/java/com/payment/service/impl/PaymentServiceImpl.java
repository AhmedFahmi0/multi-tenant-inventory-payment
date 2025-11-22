package com.payment.service.impl;

import com.payment.dto.PaymentRequest;
import com.payment.dto.PaymentResponse;
import com.payment.exception.PaymentProcessingException;
import com.payment.exception.ResourceNotFoundException;
import com.payment.mapper.PaymentMapper;
import com.payment.model.Payment;
import com.payment.repository.PaymentRepository;
import com.payment.service.PaymentService;
import com.payment.service.PaymentGatewayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentGatewayService paymentGatewayService;

    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentRequest request, String tenantId) {
        // Check for duplicate payment for the same order
        if (paymentRepository.existsByTenantIdAndOrderId(tenantId, request.getOrderId())) {
            throw new PaymentProcessingException("Payment already exists for order ID: " + request.getOrderId());
        }

        // Create and save payment
        Payment payment = paymentMapper.toEntity(request);
        payment.setTenantId(tenantId);
        payment.setStatus(Payment.Status.PENDING);
        
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Created payment with ID: {} for order ID: {}", savedPayment.getId(), request.getOrderId());
        
        return paymentMapper.toResponse(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(UUID paymentId, String tenantId) {
        Payment payment = paymentRepository.findByIdAndTenantId(paymentId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getAllPayments(String tenantId, Pageable pageable) {
        return paymentRepository.findByTenantId(tenantId, pageable)
                .map(paymentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getPaymentsByOrderId(String orderId, String tenantId, Pageable pageable) {
        return paymentRepository.findByTenantIdAndOrderId(tenantId, orderId, pageable)
                .map(paymentMapper::toResponse);
    }

    @Override
    @Transactional
    public PaymentResponse processPayment(UUID paymentId, String tenantId) {
        Payment payment = paymentRepository.findByIdAndTenantId(paymentId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));

        if (payment.getStatus() != Payment.Status.PENDING) {
            throw new PaymentProcessingException("Payment is already processed");
        }

        try {
            // Simulate payment processing with external gateway
            boolean success = paymentGatewayService.processPayment(
                    payment.getAmount(),
                    payment.getCurrency(),
                    payment.getPaymentMethod()
            );

            if (success) {
                payment.setStatus(Payment.Status.COMPLETED);
                payment.setTransactionId("TXN_" + System.currentTimeMillis());
                log.info("Payment processed successfully: {}", paymentId);
            } else {
                payment.setStatus(Payment.Status.FAILED);
                log.warn("Payment processing failed: {}", paymentId);
            }
        } catch (Exception e) {
            payment.setStatus(Payment.Status.FAILED);
            log.error("Error processing payment: {}", paymentId, e);
            throw new PaymentProcessingException("Error processing payment: " + e.getMessage());
        }

        Payment updatedPayment = paymentRepository.save(payment);
        return paymentMapper.toResponse(updatedPayment);
    }

    @Override
    @Transactional
    public PaymentResponse refundPayment(UUID paymentId, String tenantId) {
        Payment payment = paymentRepository.findByIdAndTenantId(paymentId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));

        if (payment.getStatus() != Payment.Status.COMPLETED) {
            throw new PaymentProcessingException("Only completed payments can be refunded");
        }

        try {
            // Simulate refund processing with external gateway
            boolean success = paymentGatewayService.processRefund(
                    payment.getTransactionId(),
                    payment.getAmount(),
                    payment.getCurrency()
            );

            if (success) {
                payment.setStatus(Payment.Status.REFUNDED);
                log.info("Payment refunded successfully: {}", paymentId);
            } else {
                throw new PaymentProcessingException("Refund processing failed");
            }
        } catch (Exception e) {
            log.error("Error processing refund for payment: {}", paymentId, e);
            throw new PaymentProcessingException("Error processing refund: " + e.getMessage());
        }

        Payment updatedPayment = paymentRepository.save(payment);
        return paymentMapper.toResponse(updatedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPaymentProcessed(String orderId, String tenantId) {
        return paymentRepository.findByTenantIdAndOrderId(tenantId, orderId, Pageable.unpaged())
                .stream()
                .anyMatch(p -> p.getStatus() == Payment.Status.COMPLETED);
    }

    @Async
    @Transactional
    public CompletableFuture<PaymentResponse> processPaymentAsync(UUID paymentId, String tenantId) {
        return CompletableFuture.completedFuture(processPayment(paymentId, tenantId));
    }
}

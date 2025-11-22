package com.payment.service.impl;

import com.payment.dto.PaymentRequestDTO;
import com.payment.dto.PaymentResponseDTO;
import com.payment.model.PaymentTransaction;
import com.payment.repository.PaymentTransactionRepository;
import com.payment.service.PaymentTransactionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentTransactionServiceImpl implements PaymentTransactionService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final ModelMapper modelMapper;
    private static final long PAYMENT_PROCESSING_DELAY_MS = 5000; // 5 seconds

    @Override
    @Transactional
    public PaymentResponseDTO initiatePayment(PaymentRequestDTO request, String tenantId, String idempotencyKey) {
        // Check for duplicate request using idempotency key
        if (idempotencyKey != null) {
            var existingPaymentOpt = paymentTransactionRepository.findByRequestIdAndTenantId(idempotencyKey, tenantId);
            if (existingPaymentOpt.isPresent()) {
                log.info("Returning existing payment for idempotency key: {}", idempotencyKey);
                return mapToDTO(existingPaymentOpt.get());
            }
        }

        // Create and save the payment transaction
        PaymentTransaction payment = PaymentTransaction.builder()
                .dealerId(request.getDealerId())
                .amount(request.getAmount())
                .method(request.getMethod())
                .status(PaymentTransaction.PaymentStatus.PENDING)
                .requestId(idempotencyKey != null ? idempotencyKey : UUID.randomUUID().toString())
                .tenantId(tenantId)
                .build();

        PaymentTransaction savedPayment = paymentTransactionRepository.save(payment);
        log.info("Created payment transaction with ID: {}", savedPayment.getId());

        // Process payment asynchronously
        processPaymentAsync(savedPayment.getId(), tenantId);

        return mapToDTO(savedPayment);
    }

    @Async
    protected void processPaymentAsync(UUID paymentId, String tenantId) {
        try {
            // Simulate payment processing delay
            Thread.sleep(PAYMENT_PROCESSING_DELAY_MS);
            
            // Update payment status to SUCCESS
            updatePaymentStatus(paymentId, tenantId, PaymentTransaction.PaymentStatus.SUCCESS);
            log.info("Successfully processed payment: {}", paymentId);
        } catch (Exception e) {
            log.error("Error processing payment: {}", paymentId, e);
            updatePaymentStatus(paymentId, tenantId, PaymentTransaction.PaymentStatus.FAILED);
        }
    }

    @Transactional
    protected void updatePaymentStatus(UUID paymentId, String tenantId, PaymentTransaction.PaymentStatus status) {
        paymentTransactionRepository.findByIdAndTenantId(paymentId, tenantId).ifPresent(payment -> {
            payment.setStatus(status);
            paymentTransactionRepository.save(payment);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentStatus(UUID paymentId, String tenantId) {
        return paymentTransactionRepository.findByIdAndTenantId(paymentId, tenantId)
                .map(this::mapToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + paymentId));
    }

    @Scheduled(fixedDelay = 60000) // Run every minute
    @Transactional
    public void processPendingPayments() {
        log.info("Processing pending payments...");
        List<PaymentTransaction> pendingPayments = paymentTransactionRepository
                .findByStatus(PaymentTransaction.PaymentStatus.PENDING);
        
        pendingPayments.forEach(payment -> {
            try {
                // Process payment (in a real scenario, this would involve calling a payment gateway)
                log.info("Processing payment: {}", payment.getId());
                payment.setStatus(PaymentTransaction.PaymentStatus.SUCCESS);
                paymentTransactionRepository.save(payment);
            } catch (Exception e) {
                log.error("Error processing payment: {}", payment.getId(), e);
                payment.setStatus(PaymentTransaction.PaymentStatus.FAILED);
                paymentTransactionRepository.save(payment);
            }
        });
    }

    private PaymentResponseDTO mapToDTO(PaymentTransaction payment) {
        return modelMapper.map(payment, PaymentResponseDTO.class);
    }
}

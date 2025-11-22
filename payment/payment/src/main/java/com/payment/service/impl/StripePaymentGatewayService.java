package com.payment.service.impl;

import com.payment.service.PaymentGatewayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

/**
 * Mock implementation of PaymentGatewayService for development and testing.
 * In a production environment, this would be replaced with actual payment gateway integration.
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "payment.gateway.mock.enabled", havingValue = "true", matchIfMissing = true)
public class StripePaymentGatewayService implements PaymentGatewayService {

    private final Random random = new Random();
    
    @Override
    public boolean processPayment(BigDecimal amount, String currency, String paymentMethod) {
        log.info("Processing payment of {} {} with method: {}", amount, currency, paymentMethod);
        
        // Simulate API call delay
        try {
            Thread.sleep(500 + random.nextInt(1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Payment processing was interrupted");
            return false;
        }
        
        // Simulate 90% success rate for testing
        boolean success = random.nextFloat() < 0.9f;
        log.info("Payment processing {}", success ? "succeeded" : "failed");
        return success;
    }

    @Override
    public boolean processRefund(String transactionId, BigDecimal amount, String currency) {
        log.info("Processing refund of {} {} for transaction: {}", amount, currency, transactionId);
        
        if (transactionId == null || !transactionId.startsWith("TXN_")) {
            log.warn("Invalid transaction ID for refund: {}", transactionId);
            return false;
        }
        
        // Simulate API call delay
        try {
            Thread.sleep(500 + random.nextInt(1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Refund processing was interrupted");
            return false;
        }
        
        // Simulate 95% success rate for refunds
        boolean success = random.nextFloat() < 0.95f;
        log.info("Refund processing {}", success ? "succeeded" : "failed");
        return success;
    }

    @Override
    public PaymentStatus checkPaymentStatus(String transactionId) {
        log.info("Checking status for transaction: {}", transactionId);
        
        if (transactionId == null || !transactionId.startsWith("TXN_")) {
            return PaymentStatus.FAILED;
        }
        
        // Return a random status for testing
        int statusIndex = random.nextInt(PaymentStatus.values().length);
        return PaymentStatus.values()[statusIndex];
    }
}

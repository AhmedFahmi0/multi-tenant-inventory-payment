package com.payment.service;

import java.math.BigDecimal;

/**
 * Interface for payment gateway integration.
 * Implementations of this interface will handle communication with external payment providers.
 */
public interface PaymentGatewayService {
    
    /**
     * Process a payment with the external payment gateway.
     * 
     * @param amount The amount to be charged
     * @param currency The currency of the payment
     * @param paymentMethod The payment method to be used
     * @return true if the payment was successful, false otherwise
     */
    boolean processPayment(BigDecimal amount, String currency, String paymentMethod);
    
    /**
     * Process a refund with the external payment gateway.
     * 
     * @param transactionId The original transaction ID to be refunded
     * @param amount The amount to be refunded
     * @param currency The currency of the refund
     * @return true if the refund was successful, false otherwise
     */
    boolean processRefund(String transactionId, BigDecimal amount, String currency);
    
    /**
     * Check the status of a payment.
     * 
     * @param transactionId The transaction ID to check
     * @return The status of the payment
     */
    PaymentStatus checkPaymentStatus(String transactionId);
    
    /**
     * Payment status enumeration.
     */
    enum PaymentStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED,
        REFUNDED,
        CANCELLED
    }
}

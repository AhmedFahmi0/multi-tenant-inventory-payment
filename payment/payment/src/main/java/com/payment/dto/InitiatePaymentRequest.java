package com.payment.dto;

import com.payment.model.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record InitiatePaymentRequest(
    @NotBlank(message = "Dealer ID is required")
    String dealerId,
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    BigDecimal amount,
    
    @NotNull(message = "Payment method is required")
    PaymentMethod method
) {}

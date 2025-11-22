package com.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class IdempotencyKeyException extends RuntimeException {
    
    public IdempotencyKeyException(String message) {
        super(message);
    }
    
    public IdempotencyKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}

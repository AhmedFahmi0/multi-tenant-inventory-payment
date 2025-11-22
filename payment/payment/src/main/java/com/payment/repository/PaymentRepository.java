package com.payment.repository;

import com.payment.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    
    Page<Payment> findByTenantId(String tenantId, Pageable pageable);
    
    Optional<Payment> findByIdAndTenantId(UUID id, String tenantId);
    
    Page<Payment> findByTenantIdAndOrderId(String tenantId, String orderId, Pageable pageable);
    
    Page<Payment> findByTenantIdAndStatus(String tenantId, Payment.Status status, Pageable pageable);
    
    boolean existsByTenantIdAndOrderId(String tenantId, String orderId);
}

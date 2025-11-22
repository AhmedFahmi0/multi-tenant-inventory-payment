package com.payment.repository;

import com.payment.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.payment.model.PaymentTransaction.PaymentStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {
    
    Optional<PaymentTransaction> findByIdAndTenantId(UUID id, String tenantId);
    
    boolean existsByIdAndTenantId(UUID id, String tenantId);
    
    @Query("SELECT p FROM PaymentTransaction p WHERE p.requestId = :requestId AND p.tenantId = :tenantId")
    Optional<PaymentTransaction> findByRequestIdAndTenantId(
            @Param("requestId") String requestId,
            @Param("tenantId") String tenantId
    );
    
    /**
     * Find all payment transactions with the given status
     * @param status The payment status to search for
     * @return List of payment transactions with the given status
     */
    List<PaymentTransaction> findByStatus(PaymentStatus status);
    
    /**
     * Find all payment transactions with the given status and tenant ID
     * @param status The payment status to search for
     * @param tenantId The tenant ID
     * @return List of payment transactions with the given status and tenant ID
     */
    List<PaymentTransaction> findByStatusAndTenantId(PaymentStatus status, String tenantId);
    
    @Query("SELECT p FROM PaymentTransaction p WHERE p.id = :id AND p.tenantId = :tenantId")
    Optional<PaymentTransaction> findByIdAndTenantIdWithLock(
            @Param("id") UUID id,
            @Param("tenantId") String tenantId
    );
}

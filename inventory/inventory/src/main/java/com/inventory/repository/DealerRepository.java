package com.inventory.repository;

import com.inventory.model.Dealer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DealerRepository extends JpaRepository<Dealer, UUID> {
    
    /**
     * Find all dealers for a specific tenant with pagination
     * @param tenantId the tenant ID
     * @param pageable pagination information
     * @return a page of dealers for the specified tenant
     */
    Page<Dealer> findByTenantId(String tenantId, Pageable pageable);
    
    /**
     * Find all dealers for a specific tenant without pagination
     * @param tenantId the tenant ID
     * @return a list of dealers for the specified tenant
     */
    List<Dealer> findByTenantId(String tenantId);
    
    Optional<Dealer> findByIdAndTenantId(UUID id, String tenantId);
    
    boolean existsByEmailAndTenantId(String email, String tenantId);
    
    boolean existsByIdAndTenantId(UUID id, String tenantId);
    
    void deleteByIdAndTenantId(UUID id, String tenantId);
    
    /**
     * Counts the number of dealers with a specific subscription type
     * @param subscriptionType the subscription type to count
     * @return the count of dealers with the specified subscription type
     */
    long countBySubscriptionType(Dealer.SubscriptionType subscriptionType);
}

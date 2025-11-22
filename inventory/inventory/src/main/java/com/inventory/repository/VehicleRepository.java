package com.inventory.repository;

import com.inventory.model.Dealer;
import com.inventory.model.Vehicle;
import com.inventory.model.Vehicle.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    
    Page<Vehicle> findByTenantIdAndDealerId(String tenantId, UUID dealerId, Pageable pageable);
    
    Optional<Vehicle> findByIdAndTenantId(UUID id, String tenantId);
    
    Page<Vehicle> findByTenantId(String tenantId, Pageable pageable);
    
    @Query("SELECT v FROM Vehicle v JOIN v.dealer d " +
           "WHERE v.tenantId = :tenantId " +
           "AND (:model IS NULL OR LOWER(v.model) LIKE LOWER(CONCAT('%', :model, '%'))) " +
           "AND (:status IS NULL OR v.status = :status) " +
           "AND (:minPrice IS NULL OR v.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR v.price <= :maxPrice)")
    Page<Vehicle> searchVehicles(
            @Param("tenantId") String tenantId,
            @Param("model") String model,
            @Param("status") Status status,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );
    
    @Query("SELECT v FROM Vehicle v JOIN v.dealer d " +
           "WHERE v.tenantId = :tenantId " +
           "AND d.subscriptionType = 'PREMIUM'")
    Page<Vehicle> findPremiumDealerVehicles(
            @Param("tenantId") String tenantId,
            Pageable pageable
    );
    
    boolean existsByIdAndTenantId(UUID id, String tenantId);
    
    void deleteByIdAndTenantId(UUID id, String tenantId);
}

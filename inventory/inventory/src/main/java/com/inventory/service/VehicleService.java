package com.inventory.service;

import com.inventory.dto.VehicleDTO;
import com.inventory.model.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface VehicleService extends BaseService<UUID, UUID, VehicleDTO> {
    
    Page<VehicleDTO> searchVehicles(
            String tenantId,
            String model,
            Vehicle.Status status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    );
    
    Page<VehicleDTO> getPremiumDealerVehicles(String tenantId, Pageable pageable);
}

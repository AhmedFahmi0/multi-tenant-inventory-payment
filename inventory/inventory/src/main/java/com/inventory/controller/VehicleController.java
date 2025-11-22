package com.inventory.controller;

import com.inventory.dto.VehicleDTO;
import com.inventory.model.Vehicle;
import com.inventory.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<VehicleDTO> createVehicle(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @Valid @RequestBody VehicleDTO vehicleDTO
    ) {
        VehicleDTO createdVehicle = vehicleService.create(vehicleDTO, tenantId);
        return new ResponseEntity<>(createdVehicle, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleDTO> getVehicleById(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @PathVariable UUID id
    ) {
        VehicleDTO vehicle = vehicleService.getById(id, tenantId);
        return ResponseEntity.ok(vehicle);
    }

    @GetMapping
    public ResponseEntity<Page<VehicleDTO>> getAllVehicles(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Vehicle.Status status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String subscription,
            Pageable pageable
    ) {
        if ("PREMIUM".equalsIgnoreCase(subscription)) {
            return ResponseEntity.ok(vehicleService.getPremiumDealerVehicles(tenantId, pageable));
        }
        
        if (model != null || status != null || minPrice != null || maxPrice != null) {
            return ResponseEntity.ok(vehicleService.searchVehicles(
                    tenantId, model, status, minPrice, maxPrice, pageable));
        }
        
        return ResponseEntity.ok(vehicleService.getAll(tenantId, pageable));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VehicleDTO> updateVehicle(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @PathVariable UUID id,
            @Valid @RequestBody VehicleDTO vehicleDTO
    ) {
        VehicleDTO updatedVehicle = vehicleService.update(id, vehicleDTO, tenantId);
        return ResponseEntity.ok(updatedVehicle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @PathVariable UUID id
    ) {
        vehicleService.delete(id, tenantId);
        return ResponseEntity.noContent().build();
    }
}

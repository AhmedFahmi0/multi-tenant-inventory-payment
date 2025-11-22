package com.inventory.service.impl;

import com.inventory.dto.VehicleDTO;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.mapper.VehicleMapper;
import com.inventory.model.Dealer;
import com.inventory.model.Vehicle;
import com.inventory.repository.DealerRepository;
import com.inventory.repository.VehicleRepository;
import com.inventory.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final DealerRepository dealerRepository;
    private final ModelMapper modelMapper;
    private final VehicleMapper vehicleMapper;

    @Override
    @Transactional
    public VehicleDTO create(VehicleDTO vehicleDTO, String tenantId) {
        // Check if dealer exists
        Dealer dealer = dealerRepository.findByIdAndTenantId(
                UUID.fromString(vehicleDTO.getDealerId()), 
                tenantId
        ).orElseThrow(() -> new ResourceNotFoundException("Dealer not found with id: " + vehicleDTO.getDealerId()));
        
        // Convert DTO to entity
        Vehicle vehicle = vehicleMapper.toEntity(vehicleDTO);
        vehicle.setDealer(dealer);
        vehicle.setTenantId(tenantId);
        vehicle.setMake(vehicleDTO.getMake()); // Ensure make is set
        vehicle.setStatus(vehicleDTO.getStatus() != null ? vehicleDTO.getStatus() : Vehicle.Status.AVAILABLE);
        
        // Save the vehicle
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        
        // Convert back to DTO and return
        return toVehicleDTO(savedVehicle);
    }

    @Override
    @Transactional
    public VehicleDTO update(UUID id, VehicleDTO vehicleDTO, String tenantId) {
        // Find existing vehicle
        Vehicle existingVehicle = vehicleRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
        
        // Update fields
        if (vehicleDTO.getMake() != null) {
            existingVehicle.setMake(vehicleDTO.getMake());
        }
        
        if (vehicleDTO.getModel() != null) {
            existingVehicle.setModel(vehicleDTO.getModel());
        }
        
        if (vehicleDTO.getPrice() != null) {
            existingVehicle.setPrice(vehicleDTO.getPrice());
        }
        
        if (vehicleDTO.getStatus() != null) {
            existingVehicle.setStatus(vehicleDTO.getStatus());
        }
        
        // If dealer is being updated
        if (vehicleDTO.getDealerId() != null && !existingVehicle.getDealer().getId().toString().equals(vehicleDTO.getDealerId())) {
            Dealer newDealer = dealerRepository.findByIdAndTenantId(
                    UUID.fromString(vehicleDTO.getDealerId()), 
                    tenantId
            ).orElseThrow(() -> new ResourceNotFoundException("Dealer not found with id: " + vehicleDTO.getDealerId()));
            existingVehicle.setDealer(newDealer);
        }
        
        // Save updated vehicle
        Vehicle updatedVehicle = vehicleRepository.save(existingVehicle);
        
        return toVehicleDTO(updatedVehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleDTO getById(UUID id, String tenantId) {
        Vehicle vehicle = vehicleRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
        return toVehicleDTO(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehicleDTO> getAll(String tenantId, Pageable pageable) {
        Page<Vehicle> vehicles = vehicleRepository.findByTenantId(tenantId, pageable);
        return mapToVehicleDTOPage(vehicles, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehicleDTO> searchVehicles(
            String tenantId,
            String model,
            Vehicle.Status status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    ) {
        // Validate price range
        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Minimum price cannot be negative");
        }
        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Maximum price cannot be negative");
        }
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }

        Page<Vehicle> vehicles = vehicleRepository.searchVehicles(
                tenantId,
                model,
                status,
                minPrice != null ? minPrice : BigDecimal.ZERO,
                maxPrice != null ? maxPrice : BigDecimal.valueOf(Double.MAX_VALUE),
                pageable
        );
        
        return vehicles.map(vehicleMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehicleDTO> getPremiumDealerVehicles(String tenantId, Pageable pageable) {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new IllegalArgumentException("Tenant ID is required");
        }
        
        Page<Vehicle> premiumVehicles = vehicleRepository.findPremiumDealerVehicles(tenantId, pageable);
        return premiumVehicles.map(vehicleMapper::toDTO);
    }

    @Override
    @Transactional
    public void delete(UUID id, String tenantId) {
        if (id == null) {
            throw new IllegalArgumentException("Vehicle ID cannot be null");
        }
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new IllegalArgumentException("Tenant ID is required");
        }
        
        if (!vehicleRepository.existsByIdAndTenantId(id, tenantId)) {
            throw new ResourceNotFoundException("Vehicle not found with id: " + id);
        }
        vehicleRepository.deleteByIdAndTenantId(id, tenantId);
    }

    @Override
    public boolean existsById(UUID id, String tenantId) {
        return vehicleRepository.existsByIdAndTenantId(id, tenantId);
    }
    
    private Page<VehicleDTO> mapToVehicleDTOPage(Page<Vehicle> vehicles, Pageable pageable) {
        List<VehicleDTO> vehicleDTOs = vehicles.getContent().stream()
                .map(this::toVehicleDTO)
                .collect(Collectors.toList());
                
        return new PageImpl<>(vehicleDTOs, pageable, vehicles.getTotalElements());
    }
    
    private VehicleDTO toVehicleDTO(Vehicle vehicle) {
        VehicleDTO dto = vehicleMapper.toDTO(vehicle);
        return dto;
    }
}

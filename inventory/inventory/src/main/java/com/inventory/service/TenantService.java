package com.inventory.service;

import com.inventory.dto.TenantDTO;
import com.inventory.exception.ResourceAlreadyExistsException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.mapper.TenantMapper;
import com.inventory.model.Tenant;
import com.inventory.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public interface TenantService {
    TenantDTO createTenant(TenantDTO tenantDTO);
    TenantDTO getTenantById(String id);
    TenantDTO getTenantBySchemaName(String schemaName);
    List<TenantDTO> getAllTenants();
    TenantDTO updateTenant(String id, TenantDTO tenantDTO);
    void deleteTenant(String id);
}

@Service
@RequiredArgsConstructor
class TenantServiceImpl implements TenantService {
    
    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;
    
    @Override
    @Transactional
    public TenantDTO createTenant(TenantDTO tenantDTO) {
        // Check if schema name already exists
        if (tenantRepository.existsBySchemaName(tenantDTO.getSchemaName())) {
            throw new ResourceAlreadyExistsException("Tenant with schema name " + tenantDTO.getSchemaName() + " already exists");
        }
        
        // Generate ID if not provided
        if (tenantDTO.getId() == null || tenantDTO.getId().trim().isEmpty()) {
            tenantDTO.setId(UUID.randomUUID().toString());
        }
        
        Tenant tenant = tenantMapper.toEntity(tenantDTO);
        Tenant savedTenant = tenantRepository.save(tenant);
        return tenantMapper.toDTO(savedTenant);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TenantDTO getTenantById(String id) {
        return tenantRepository.findById(id)
                .map(tenantMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));
    }
    
    @Override
    @Transactional(readOnly = true)
    public TenantDTO getTenantBySchemaName(String schemaName) {
        return tenantRepository.findBySchemaName(schemaName)
                .map(tenantMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with schema name: " + schemaName));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TenantDTO> getAllTenants() {
        return tenantRepository.findAll().stream()
                .map(tenantMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public TenantDTO updateTenant(String id, TenantDTO tenantDTO) {
        return tenantRepository.findById(id)
                .map(existingTenant -> {
                    // Update fields
                    existingTenant.setName(tenantDTO.getName());
                    // Don't allow updating schema name as it might break multi-tenancy
                    return tenantMapper.toDTO(tenantRepository.save(existingTenant));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));
    }
    
    @Override
    @Transactional
    public void deleteTenant(String id) {
        if (!tenantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tenant not found with id: " + id);
        }
        // Note: In a real application, you might want to add additional checks
        // to ensure no resources are associated with this tenant before deletion
        tenantRepository.deleteById(id);
    }
}

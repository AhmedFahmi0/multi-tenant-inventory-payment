package com.inventory.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BaseService<T, ID, DTO> {
    DTO create(DTO dto, String tenantId);
    
    DTO update(ID id, DTO dto, String tenantId);
    
    DTO getById(ID id, String tenantId);
    
    Page<DTO> getAll(String tenantId, Pageable pageable);
    
    void delete(ID id, String tenantId);
    
    boolean existsById(ID id, String tenantId);
}

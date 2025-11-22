package com.inventory.service;

import com.inventory.dto.DealerDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DealerService extends BaseService<UUID, UUID, DealerDTO> {
    // Additional dealer-specific methods can be added here if needed
}

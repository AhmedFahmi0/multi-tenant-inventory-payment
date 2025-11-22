package com.inventory.service.impl;

import com.inventory.dto.DealerDTO;
import com.inventory.exception.ResourceAlreadyExistsException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.mapper.DealerMapper;
import com.inventory.model.Dealer;
import com.inventory.repository.DealerRepository;
import com.inventory.service.DealerService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DealerServiceImpl implements DealerService {

    private final DealerRepository dealerRepository;
    private final ModelMapper modelMapper;
    private final DealerMapper dealerMapper;

    @Override
    @Transactional
    public DealerDTO create(DealerDTO dealerDTO, String tenantId) {
        // Check if dealer with the same email already exists for this tenant
        if (dealerRepository.existsByEmailAndTenantId(dealerDTO.getEmail(), tenantId)) {
            throw new ResourceAlreadyExistsException("Dealer with email " + dealerDTO.getEmail() + " already exists");
        }

        // Convert DTO to entity
        Dealer dealer = dealerMapper.toEntity(dealerDTO);
        dealer.setTenantId(tenantId);
        
        // Save the dealer
        Dealer savedDealer = dealerRepository.save(dealer);
        
        // Convert back to DTO and return
        return dealerMapper.toDTO(savedDealer);
    }

    @Override
    @Transactional
    public DealerDTO update(UUID id, DealerDTO dealerDTO, String tenantId) {
        // Find existing dealer
        Dealer existingDealer = dealerRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with id: " + id));

        // Check if email is being updated to an existing one
        if (!existingDealer.getEmail().equals(dealerDTO.getEmail()) && 
            dealerRepository.existsByEmailAndTenantId(dealerDTO.getEmail(), tenantId)) {
            throw new ResourceAlreadyExistsException("Dealer with email " + dealerDTO.getEmail() + " already exists");
        }

        // Update fields
        existingDealer.setName(dealerDTO.getName());
        existingDealer.setEmail(dealerDTO.getEmail());
        existingDealer.setSubscriptionType(dealerDTO.getSubscriptionType());

        // Save updated dealer
        Dealer updatedDealer = dealerRepository.save(existingDealer);
        
        return dealerMapper.toDTO(updatedDealer);
    }

    @Override
    @Transactional(readOnly = true)
    public DealerDTO getById(UUID id, String tenantId) {
        Dealer dealer = dealerRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with id: " + id));
        return dealerMapper.toDTO(dealer);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DealerDTO> getAll(String tenantId, Pageable pageable) {
        Page<Dealer> dealers = dealerRepository.findByTenantId(tenantId, pageable);
        
        List<DealerDTO> dealerDTOs = dealers.getContent().stream()
                .map(dealerMapper::toDTO)
                .collect(Collectors.toList());
                
        return new PageImpl<>(dealerDTOs, pageable, dealers.getTotalElements());
    }

    @Override
    @Transactional
    public void delete(UUID id, String tenantId) {
        if (!dealerRepository.existsByIdAndTenantId(id, tenantId)) {
            throw new ResourceNotFoundException("Dealer not found with id: " + id);
        }
        dealerRepository.deleteByIdAndTenantId(id, tenantId);
    }

    @Override
    public boolean existsById(UUID id, String tenantId) {
        return dealerRepository.existsByIdAndTenantId(id, tenantId);
    }
}

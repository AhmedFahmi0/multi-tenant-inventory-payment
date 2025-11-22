package com.inventory.controller;

import com.inventory.dto.DealerDTO;
import com.inventory.service.DealerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/dealers")
@RequiredArgsConstructor
public class DealerController {

    private final DealerService dealerService;

    @PostMapping
    public ResponseEntity<DealerDTO> createDealer(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @Valid @RequestBody DealerDTO dealerDTO
    ) {
        DealerDTO createdDealer = dealerService.create(dealerDTO, tenantId);
        return new ResponseEntity<>(createdDealer, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DealerDTO> getDealerById(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @PathVariable UUID id
    ) {
        DealerDTO dealer = dealerService.getById(id, tenantId);
        return ResponseEntity.ok(dealer);
    }

    @GetMapping
    public ResponseEntity<Page<DealerDTO>> getAllDealers(
            @RequestHeader("X-Tenant-Id") String tenantId,
            Pageable pageable
    ) {
        Page<DealerDTO> dealers = dealerService.getAll(tenantId, pageable);
        return ResponseEntity.ok(dealers);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DealerDTO> updateDealer(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @PathVariable UUID id,
            @Valid @RequestBody DealerDTO dealerDTO
    ) {
        DealerDTO updatedDealer = dealerService.update(id, dealerDTO, tenantId);
        return ResponseEntity.ok(updatedDealer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDealer(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @PathVariable UUID id
    ) {
        dealerService.delete(id, tenantId);
        return ResponseEntity.noContent().build();
    }
}

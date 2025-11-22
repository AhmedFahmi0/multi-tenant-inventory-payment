package com.inventory.controller;

import com.inventory.dto.TenantDTO;
import com.inventory.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
@Tag(name = "Tenant Management", description = "APIs for managing tenants")
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    @Operation(summary = "Create a new tenant")
    @PreAuthorize("hasRole('GLOBAL_ADMIN')")
    public ResponseEntity<TenantDTO> createTenant(@Valid @RequestBody TenantDTO tenantDTO) {
        TenantDTO createdTenant = tenantService.createTenant(tenantDTO);
        return new ResponseEntity<>(createdTenant, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tenant by ID")
    @PreAuthorize("hasRole('GLOBAL_ADMIN')")
    public ResponseEntity<TenantDTO> getTenantById(@PathVariable String id) {
        return ResponseEntity.ok(tenantService.getTenantById(id));
    }

    @GetMapping("/schema/{schemaName}")
    @Operation(summary = "Get tenant by schema name")
    @PreAuthorize("hasRole('GLOBAL_ADMIN')")
    public ResponseEntity<TenantDTO> getTenantBySchemaName(@PathVariable String schemaName) {
        return ResponseEntity.ok(tenantService.getTenantBySchemaName(schemaName));
    }

    @GetMapping
    @Operation(summary = "Get all tenants")
    @PreAuthorize("hasRole('GLOBAL_ADMIN')")
    public ResponseEntity<List<TenantDTO>> getAllTenants() {
        return ResponseEntity.ok(tenantService.getAllTenants());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a tenant")
    @PreAuthorize("hasRole('GLOBAL_ADMIN')")
    public ResponseEntity<TenantDTO> updateTenant(
            @PathVariable String id,
            @Valid @RequestBody TenantDTO tenantDTO) {
        return ResponseEntity.ok(tenantService.updateTenant(id, tenantDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a tenant")
    @PreAuthorize("hasRole('GLOBAL_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTenant(@PathVariable String id) {
        tenantService.deleteTenant(id);
    }
}

package com.inventory.mapper;

import com.inventory.dto.VehicleDTO;
import com.inventory.model.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VehicleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "dealer", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", source = "status", defaultExpression = "java(Vehicle.Status.AVAILABLE)")
    @Mapping(target = "make", source = "make")
    @Mapping(target = "year", source = "year")
    Vehicle toEntity(VehicleDTO dto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "tenantId", source = "tenantId")
    @Mapping(target = "dealerId", ignore = true) // Will be set manually
    @Mapping(target = "dealer", source = "dealer")
    @Mapping(target = "year", source = "year")
    @Mapping(target = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    VehicleDTO toDTO(Vehicle entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "dealer", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "make", source = "make")
    @Mapping(target = "year", source = "year")
    void updateFromDTO(VehicleDTO dto, @MappingTarget Vehicle entity);
}

package com.inventory.mapper;

import com.inventory.dto.TenantDTO;
import com.inventory.model.Tenant;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-21T20:12:32+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Oracle Corporation)"
)
@Component
public class TenantMapperImpl implements TenantMapper {

    @Override
    public TenantDTO toDTO(Tenant tenant) {
        if ( tenant == null ) {
            return null;
        }

        TenantDTO.TenantDTOBuilder tenantDTO = TenantDTO.builder();

        tenantDTO.id( tenant.getId() );
        tenantDTO.name( tenant.getName() );
        tenantDTO.schemaName( tenant.getSchemaName() );
        tenantDTO.createdAt( tenant.getCreatedAt() );
        tenantDTO.updatedAt( tenant.getUpdatedAt() );

        return tenantDTO.build();
    }

    @Override
    public Tenant toEntity(TenantDTO tenantDTO) {
        if ( tenantDTO == null ) {
            return null;
        }

        Tenant.TenantBuilder tenant = Tenant.builder();

        tenant.id( tenantDTO.getId() );
        tenant.name( tenantDTO.getName() );
        tenant.schemaName( tenantDTO.getSchemaName() );

        return tenant.build();
    }
}

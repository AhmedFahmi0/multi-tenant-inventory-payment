package com.inventory.mapper;

import com.inventory.dto.DealerDTO;
import com.inventory.model.Dealer;
import java.time.format.DateTimeFormatter;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-20T21:47:10+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Oracle Corporation)"
)
@Component
public class DealerMapperImpl implements DealerMapper {

    private final DateTimeFormatter dateTimeFormatter_yyyy_MM_dd_HH_mm_ss_11333195168 = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss" );

    @Override
    public Dealer toEntity(DealerDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Dealer.DealerBuilder dealer = Dealer.builder();

        dealer.name( dto.getName() );
        dealer.email( dto.getEmail() );
        dealer.subscriptionType( dto.getSubscriptionType() );

        return dealer.build();
    }

    @Override
    public DealerDTO toDTO(Dealer entity) {
        if ( entity == null ) {
            return null;
        }

        DealerDTO.DealerDTOBuilder dealerDTO = DealerDTO.builder();

        if ( entity.getId() != null ) {
            dealerDTO.id( entity.getId().toString() );
        }
        dealerDTO.tenantId( entity.getTenantId() );
        if ( entity.getCreatedAt() != null ) {
            dealerDTO.createdAt( dateTimeFormatter_yyyy_MM_dd_HH_mm_ss_11333195168.format( entity.getCreatedAt() ) );
        }
        if ( entity.getUpdatedAt() != null ) {
            dealerDTO.updatedAt( dateTimeFormatter_yyyy_MM_dd_HH_mm_ss_11333195168.format( entity.getUpdatedAt() ) );
        }
        dealerDTO.name( entity.getName() );
        dealerDTO.email( entity.getEmail() );
        dealerDTO.subscriptionType( entity.getSubscriptionType() );

        return dealerDTO.build();
    }

    @Override
    public void updateFromDTO(DealerDTO dto, Dealer entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getName() != null ) {
            entity.setName( dto.getName() );
        }
        if ( dto.getEmail() != null ) {
            entity.setEmail( dto.getEmail() );
        }
        if ( dto.getSubscriptionType() != null ) {
            entity.setSubscriptionType( dto.getSubscriptionType() );
        }
    }
}

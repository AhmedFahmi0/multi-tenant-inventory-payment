package com.inventory.mapper;

import com.inventory.dto.DealerDTO;
import com.inventory.dto.VehicleDTO;
import com.inventory.model.Dealer;
import com.inventory.model.Vehicle;
import java.time.format.DateTimeFormatter;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-21T20:31:48+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Oracle Corporation)"
)
@Component
public class VehicleMapperImpl implements VehicleMapper {

    private final DateTimeFormatter dateTimeFormatter_yyyy_MM_dd_HH_mm_ss_11333195168 = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss" );

    @Override
    public Vehicle toEntity(VehicleDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Vehicle.VehicleBuilder vehicle = Vehicle.builder();

        if ( dto.getStatus() != null ) {
            vehicle.status( dto.getStatus() );
        }
        else {
            vehicle.status( Vehicle.Status.AVAILABLE );
        }
        vehicle.make( dto.getMake() );
        vehicle.year( dto.getYear() );
        vehicle.model( dto.getModel() );
        vehicle.price( dto.getPrice() );

        return vehicle.build();
    }

    @Override
    public VehicleDTO toDTO(Vehicle entity) {
        if ( entity == null ) {
            return null;
        }

        VehicleDTO.VehicleDTOBuilder vehicleDTO = VehicleDTO.builder();

        if ( entity.getId() != null ) {
            vehicleDTO.id( entity.getId().toString() );
        }
        vehicleDTO.tenantId( entity.getTenantId() );
        vehicleDTO.dealer( dealerToDealerDTO( entity.getDealer() ) );
        vehicleDTO.year( entity.getYear() );
        if ( entity.getCreatedAt() != null ) {
            vehicleDTO.createdAt( dateTimeFormatter_yyyy_MM_dd_HH_mm_ss_11333195168.format( entity.getCreatedAt() ) );
        }
        if ( entity.getUpdatedAt() != null ) {
            vehicleDTO.updatedAt( dateTimeFormatter_yyyy_MM_dd_HH_mm_ss_11333195168.format( entity.getUpdatedAt() ) );
        }
        vehicleDTO.make( entity.getMake() );
        vehicleDTO.model( entity.getModel() );
        vehicleDTO.price( entity.getPrice() );
        vehicleDTO.status( entity.getStatus() );

        return vehicleDTO.build();
    }

    @Override
    public void updateFromDTO(VehicleDTO dto, Vehicle entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getMake() != null ) {
            entity.setMake( dto.getMake() );
        }
        if ( dto.getYear() != null ) {
            entity.setYear( dto.getYear() );
        }
        if ( dto.getModel() != null ) {
            entity.setModel( dto.getModel() );
        }
        if ( dto.getPrice() != null ) {
            entity.setPrice( dto.getPrice() );
        }
        if ( dto.getStatus() != null ) {
            entity.setStatus( dto.getStatus() );
        }
    }

    protected DealerDTO dealerToDealerDTO(Dealer dealer) {
        if ( dealer == null ) {
            return null;
        }

        DealerDTO.DealerDTOBuilder dealerDTO = DealerDTO.builder();

        if ( dealer.getId() != null ) {
            dealerDTO.id( dealer.getId().toString() );
        }
        dealerDTO.name( dealer.getName() );
        dealerDTO.email( dealer.getEmail() );
        dealerDTO.subscriptionType( dealer.getSubscriptionType() );
        dealerDTO.tenantId( dealer.getTenantId() );
        if ( dealer.getCreatedAt() != null ) {
            dealerDTO.createdAt( DateTimeFormatter.ISO_LOCAL_DATE_TIME.format( dealer.getCreatedAt() ) );
        }
        if ( dealer.getUpdatedAt() != null ) {
            dealerDTO.updatedAt( DateTimeFormatter.ISO_LOCAL_DATE_TIME.format( dealer.getUpdatedAt() ) );
        }

        return dealerDTO.build();
    }
}

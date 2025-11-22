package com.payment.mapper;

import com.payment.dto.PaymentRequest;
import com.payment.dto.PaymentResponse;
import com.payment.model.Payment;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-21T23:37:44+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Oracle Corporation)"
)
@Component
public class PaymentMapperImpl implements PaymentMapper {

    @Override
    public Payment toEntity(PaymentRequest request) {
        if ( request == null ) {
            return null;
        }

        Payment.PaymentBuilder payment = Payment.builder();

        payment.orderId( request.getOrderId() );
        payment.amount( request.getAmount() );
        payment.currency( request.getCurrency() );
        payment.paymentMethod( request.getPaymentMethod() );
        payment.customerEmail( request.getCustomerEmail() );
        payment.description( request.getDescription() );

        return payment.build();
    }

    @Override
    public PaymentResponse toResponse(Payment payment) {
        if ( payment == null ) {
            return null;
        }

        PaymentResponse.PaymentResponseBuilder paymentResponse = PaymentResponse.builder();

        if ( payment.getId() != null ) {
            paymentResponse.id( payment.getId().toString() );
        }
        paymentResponse.orderId( payment.getOrderId() );
        paymentResponse.amount( payment.getAmount() );
        paymentResponse.currency( payment.getCurrency() );
        paymentResponse.status( payment.getStatus() );
        paymentResponse.paymentMethod( payment.getPaymentMethod() );
        paymentResponse.transactionId( payment.getTransactionId() );
        paymentResponse.customerEmail( payment.getCustomerEmail() );
        paymentResponse.description( payment.getDescription() );
        paymentResponse.createdAt( payment.getCreatedAt() );
        paymentResponse.updatedAt( payment.getUpdatedAt() );

        return paymentResponse.build();
    }

    @Override
    public void updateEntityFromRequest(PaymentRequest request, Payment payment) {
        if ( request == null ) {
            return;
        }

        if ( request.getOrderId() != null ) {
            payment.setOrderId( request.getOrderId() );
        }
        if ( request.getAmount() != null ) {
            payment.setAmount( request.getAmount() );
        }
        if ( request.getCurrency() != null ) {
            payment.setCurrency( request.getCurrency() );
        }
        if ( request.getPaymentMethod() != null ) {
            payment.setPaymentMethod( request.getPaymentMethod() );
        }
        if ( request.getCustomerEmail() != null ) {
            payment.setCustomerEmail( request.getCustomerEmail() );
        }
        if ( request.getDescription() != null ) {
            payment.setDescription( request.getDescription() );
        }
    }
}

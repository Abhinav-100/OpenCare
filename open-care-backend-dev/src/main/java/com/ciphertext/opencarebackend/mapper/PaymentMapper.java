package com.ciphertext.opencarebackend.mapper;
import com.ciphertext.opencarebackend.modules.payment.dto.response.PaymentResponse;
import com.ciphertext.opencarebackend.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Component
public interface PaymentMapper {

    PaymentResponse toResponse(Payment payment);

    Payment toEntity(PaymentResponse response);
}

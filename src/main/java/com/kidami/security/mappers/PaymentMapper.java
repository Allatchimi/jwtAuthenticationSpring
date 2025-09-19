package com.kidami.security.mappers;

import com.kidami.security.dto.paymentDTO.PaymentCreateDTO;
import com.kidami.security.dto.paymentDTO.PaymentDTO;
import com.kidami.security.models.Payment;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "purchase.id", target = "purchaseId")
    PaymentDTO toDTO(Payment payment);

    @InheritInverseConfiguration
    Payment toEntity(PaymentDTO paymentDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "purchaseId", target = "purchase.id")
    @Mapping(source = "method", target = "paymentMethod")
    @Mapping(target = "status", expression = "java(createDTO.isSuccess() ? com.kidami.security.enums.PaymentStatus.SUCCESS : com.kidami.security.enums.PaymentStatus.FAILED)")
    Payment fromCreateDTO(PaymentCreateDTO createDTO);
}

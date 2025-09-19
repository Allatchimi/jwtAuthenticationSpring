package com.kidami.security.mappers;

import com.kidami.security.dto.purchaseDTO.PurchaseCreateDTO;
import com.kidami.security.dto.purchaseDTO.PurchaseDTO;
import com.kidami.security.models.Purchase;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {PaymentMapper.class, CourMapper.class})
public interface PurchaseMapper {

    @Mapping(source = "buyer.name", target = "buyerName")
    PurchaseDTO toDTO(Purchase purchase);

    @InheritInverseConfiguration
    Purchase toEntity(PurchaseDTO purchaseDTO);

    // ⚠️ pas de mapping courseIds ici, on le gère dans le service
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "userId", target = "buyer.id")
    Purchase fromCreateDTO(PurchaseCreateDTO createDTO);
}

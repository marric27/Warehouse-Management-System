package com.relatech.warehouse_management_system.outbound.release.service;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.outbound.dto.OrderDto;
import com.relatech.warehouse_management_system.outbound.dto.PickListDto;
import com.relatech.warehouse_management_system.outbound.dto.PickListItemDto;
import com.relatech.warehouse_management_system.outbound.dto.SalesOrderLineDto;
import com.relatech.warehouse_management_system.outbound.entity.PickList;
import com.relatech.warehouse_management_system.outbound.entity.mapper.PickListMapper;
import com.relatech.warehouse_management_system.outbound.entity.service.OrderService;
import com.relatech.warehouse_management_system.outbound.entity.service.PickListService;
import com.relatech.warehouse_management_system.product.service.ProductService;
import com.relatech.warehouse_management_system.warehouse.service.SlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PickListGen {

    private final OrderService orderService;
    private final ProductService productService;
    private final SlotService slotService;
    private final PickListService pickListService;

    @Transactional
    public PickListDto generatePickList(Long orderId) throws ResourceNotFoundException {

        // 1️⃣ Recupero OrderDto
        OrderDto orderDto = orderService.getOrderById(orderId);

        // 2️⃣ Creo PickListDTO base
        PickListDto pickListDTO = PickListDto.builder()
                .customerCode(orderDto.getCustomerCode())
                .build();

        // 3️⃣ Creo PickListItemDTO usando mapper
        for (SalesOrderLineDto line : orderDto.getSalesOrderLineList()) {

            String productCode = String.valueOf(line.getProductCode());
            String slotCode = "SLT-000"; // slotService.getBestSlotForProduct(line.getProductCode());

            PickListItemDto itemDTO = PickListItemDto.builder()
                    .productCode(productCode)
                    .quantity(line.getQuantity())
                    .slotCode(slotCode)
                    .salesOrderCode(orderDto.getCode())
                    .salesOrderLineNumber(line.getSalesOrderNumber())
                    .build();

            pickListDTO.getPickListItemList().add(itemDTO);
        }

        // 4️⃣ Conversione in Entity usando il mapper (pronta per persistere)
        PickList pickListEntity = PickListMapper.toEntity(pickListDTO);

        log.info("Generated PickListEntity {} with {} items for order {}",
                pickListEntity.getCode(),
                pickListEntity.getPickListItemList().size(),
                orderDto.getCode());

        // 5️⃣ Se vuoi, qui puoi salvarlo con repository.save(pickListEntity)
        // pickListRepository.save(pickListEntity);
        pickListService.create(pickListDTO);

        // 6️⃣ Ritorno DTO (anche aggiornato con eventuale codice generato dal DB)
        return PickListMapper.toDto(pickListEntity);
    }
}

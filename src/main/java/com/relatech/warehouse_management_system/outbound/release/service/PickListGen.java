package com.relatech.warehouse_management_system.outbound.release.service;

import com.github.f4b6a3.ulid.UlidCreator;
import com.relatech.warehouse_management_system.common.util.OrderState;
import com.relatech.warehouse_management_system.common.util.PickListItemState;
import com.relatech.warehouse_management_system.outbound.dto.OrderDto;
import com.relatech.warehouse_management_system.outbound.dto.PickListDto;
import com.relatech.warehouse_management_system.outbound.dto.PickListItemDto;
import com.relatech.warehouse_management_system.outbound.dto.SalesOrderLineDto;
import com.relatech.warehouse_management_system.outbound.entity.service.OrderService;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDto;
import com.relatech.warehouse_management_system.warehouse.service.SlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PickListGen {

    private final OrderService orderService;
    private final SlotService slotService;
    private final PickListTransactionalService transactionalService;

    public List<PickListDto> generatePickLists(List<Long> orderIds) {

        // 1️⃣ Lettura FUORI transazione
        List<OrderDto> ordersOpen = orderService.getOrdersByStateInIds(OrderState.OPEN, orderIds);

        if (ordersOpen.isEmpty())
            return List.of();

        // 2️⃣ Preload slot (no N+1)
        List<String> productCodes = ordersOpen.stream()
                .flatMap(o -> o.getSalesOrderLineList().stream())
                .map(SalesOrderLineDto::getProductCode)
                .distinct()
                .toList();

        Map<String, SlotDto> slotsByProduct = slotService.getBestSlotsForProducts(productCodes);

        // 3️⃣ CPU-bound: costruzione picklist
        Map<String, PickListDto> pickListMap = new HashMap<>();
        String releaseNumber = "RLS-" + UlidCreator.getUlid().toString().substring(0, 10).toUpperCase();

        for (OrderDto order : ordersOpen) {
            PickListDto pickList = pickListMap.computeIfAbsent(
                    order.getCustomerCode(),
                    c -> PickListDto.builder()
                            .customerCode(c)
                            .releaseNumber(releaseNumber)
                            .pickListItemList(new ArrayList<>())
                            .build()
            );

            for (SalesOrderLineDto line : order.getSalesOrderLineList()) {

                SlotDto slot = slotsByProduct.get(line.getProductCode());
                if (slot == null)
                    throw new IllegalStateException("No slot for product " + line.getProductCode());

                pickList.getPickListItemList().add(
                        PickListItemDto.builder()
                                .productCode(line.getProductCode())
                                .state(PickListItemState.OPEN)
                                .qty(line.getQuantity())
                                .pickedQty(0)
                                .pickingSequence(slot.getPickingSequence())
                                .slotCode(slot.getCode())
                                .salesOrderCode(order.getCode())
                                .salesOrderLineNumber(line.getSalesOrderLineNumber())
                                .build()
                );
            }
        }

        List<Long> ids = ordersOpen.stream().map(OrderDto::getId).toList();

        // 4️⃣ Transazione breve SOLO per scrittura
        return transactionalService.doTransactionalUpdate(ids, pickListMap.values());
    }
}
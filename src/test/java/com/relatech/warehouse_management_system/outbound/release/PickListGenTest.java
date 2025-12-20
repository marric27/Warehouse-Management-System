package com.relatech.warehouse_management_system.outbound.release;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.common.util.OrderState;
import com.relatech.warehouse_management_system.common.util.PickListItemState;
import com.relatech.warehouse_management_system.outbound.dto.*;
import com.relatech.warehouse_management_system.outbound.entity.service.OrderService;
import com.relatech.warehouse_management_system.outbound.entity.service.PickListService;
import com.relatech.warehouse_management_system.outbound.release.service.PickListGen;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDto;
import com.relatech.warehouse_management_system.warehouse.service.SlotService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PickListGenTest {

    @Mock
    private OrderService orderService;

    @Mock
    private PickListService pickListService;

    @Mock
    private SlotService slotService;

    @InjectMocks
    private PickListGen pickListGen;

    @Test
    void generatePickLists_success() throws Exception {
        // Arrange
        SalesOrderLineDto line = SalesOrderLineDto.builder()
                .productCode("PROD-01")
                .quantity(5)
                .salesOrderLineNumber(1)
                .build();

        OrderDto order = OrderDto.builder()
                .id(1L)
                .code("ORD-01")
                .customerCode("CUST-01")
                .state(OrderState.OPEN)
                .salesOrderLineList(List.of(line))
                .build();

        SlotDto slot = SlotDto.builder()
                .code("SLOT-01")
                .pickingSequence(10)
                .build();

        when(orderService.getOrdersByStateInIds(eq(OrderState.OPEN), anyList())).thenReturn(List.of(order));
        when(slotService.getSlotContainingProduct("PROD-01", 5)).thenReturn(Optional.of(slot));

        // Act
        List<PickListDto> result = pickListGen.generatePickLists(List.of(1L));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        PickListDto pickList = result.getFirst();
        assertEquals("CUST-01", pickList.getCustomerCode());
        assertEquals(1, pickList.getPickListItemList().size());

        PickListItemDto item = pickList.getPickListItemList().getFirst();
        assertEquals("PROD-01", item.getProductCode());
        assertEquals(5, item.getQuantity());
        assertEquals(0, item.getPickedQty());
        assertEquals(PickListItemState.OPEN, item.getState());
        assertEquals("SLOT-01", item.getSlotCode());

        verify(orderService).updateOrderState(1L, OrderState.PICKING);
        verify(pickListService).create(any(PickListDto.class));
    }

    @Test
    void generatePickLists_noSlotFound_throwsException() throws ResourceNotFoundException {
        // Arrange
        SalesOrderLineDto line = SalesOrderLineDto.builder()
                .productCode("PROD-01")
                .quantity(5)
                .salesOrderLineNumber(1)
                .build();

        OrderDto order = OrderDto.builder()
                .id(1L)
                .customerCode("CUST-01")
                .state(OrderState.OPEN)
                .salesOrderLineList(List.of(line))
                .build();

        when(orderService.getOrdersByStateInIds(eq(OrderState.OPEN), anyList())).thenReturn(List.of(order));

        when(slotService.getSlotContainingProduct("PROD-01", 5)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> pickListGen.generatePickLists(List.of(1L))
        );

        assertTrue(ex.getMessage().contains("No slot found"));

        verify(pickListService, never()).create(any());
        verify(orderService, never()).updateOrderState(anyLong(), any());
    }
}
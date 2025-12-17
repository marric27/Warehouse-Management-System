package com.relatech.warehouse_management_system.picking;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDto;
import com.relatech.warehouse_management_system.outbound.dto.PickListDto;
import com.relatech.warehouse_management_system.outbound.dto.PickListItemDto;
import com.relatech.warehouse_management_system.outbound.entity.service.PickListService;
import com.relatech.warehouse_management_system.picking.controller.PickingController;
import com.relatech.warehouse_management_system.picking.service.PickingService;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDto;
import com.relatech.warehouse_management_system.warehouse.service.SlotService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PickingServiceTest {

    @InjectMocks
    private PickingService pickingService;

    @Mock
    private PickListService pickListService;

    @Mock
    private SlotService slotService;

    /* =====================================================
       CASO OK
       ===================================================== */
    @Test
    void check_ok() throws ResourceNotFoundException {
        // GIVEN
        PickingController.Request request = new PickingController.Request();
        request.setPickListCode("PL-001");
        request.setPickListItemCode("PLI-001");
        request.setStockUnitQuantities(Map.of("SU-001", 10));

        PickListItemDto pli = PickListItemDto.builder()
                .code("PLI-001")
                .slotCode("SLOT-01")
                .build();

        PickListDto pickListDto = PickListDto.builder()
                .code("PL-001")
                .pickListItemList(List.of(pli))
                .build();

        StockUnitDto stockUnitDto = StockUnitDto.builder()
                .code("SU-001")
                .quantity(10)
                .build();

        SlotDto slotDto = SlotDto.builder()
                .code("SLOT-01")
                .stockUnits(List.of(stockUnitDto))
                .build();

        when(pickListService.getPickListByCode("PL-001")).thenReturn(pickListDto);
        when(slotService.getSlotByCode("SLOT-01")).thenReturn(slotDto);

        // WHEN / THEN
        assertDoesNotThrow(() -> pickingService.check(request));
    }

    /* =====================================================
       PickListItem NON TROVATO
       ===================================================== */
    @Test
    void check_pickListItemNotFound() throws ResourceNotFoundException {
        PickingController.Request request = new PickingController.Request();
        request.setPickListCode("PL-001");
        request.setPickListItemCode("PLI-404");

        PickListDto pickListDto = PickListDto.builder()
                .pickListItemList(List.of())
                .build();

        when(pickListService.getPickListByCode("PL-001")).thenReturn(pickListDto);

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> pickingService.check(request)
        );

        assertEquals(
                "PickListItem not found with id: PLI-404",
                ex.getMessage()
        );
    }

    /* =====================================================
       StockUnit NON PRESENTE nello slot
       ===================================================== */
    @Test
    void check_stockUnitNotFound() throws ResourceNotFoundException {
        PickingController.Request request = new PickingController.Request();
        request.setPickListCode("PL-001");
        request.setPickListItemCode("PLI-001");
        request.setStockUnitQuantities(Map.of("SU-404", 1));

        PickListItemDto pli = PickListItemDto.builder()
                .code("PLI-001")
                .slotCode("SLOT-01")
                .build();

        PickListDto pickListDto = PickListDto.builder()
                .pickListItemList(List.of(pli))
                .build();

        SlotDto slotDto = SlotDto.builder()
                .stockUnits(List.of())
                .build();

        when(pickListService.getPickListByCode("PL-001")).thenReturn(pickListDto);
        when(slotService.getSlotByCode("SLOT-01")).thenReturn(slotDto);

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> pickingService.check(request)
        );

        assertEquals(
                "StockUnit not found with id: SU-404",
                ex.getMessage()
        );
    }

    /* =====================================================
       Quantità NON VALIDA
       ===================================================== */
    @Test
    void check_invalidQuantity() throws ResourceNotFoundException {
        PickingController.Request request = new PickingController.Request();
        request.setPickListCode("PL-001");
        request.setPickListItemCode("PLI-001");
        request.setStockUnitQuantities(Map.of("SU-001", 20));

        PickListItemDto pli = PickListItemDto.builder()
                .code("PLI-001")
                .slotCode("SLOT-01")
                .build();

        PickListDto pickListDto = PickListDto.builder()
                .pickListItemList(List.of(pli))
                .build();

        StockUnitDto stockUnitDto = StockUnitDto.builder()
                .code("SU-001")
                .quantity(10)
                .build();

        SlotDto slotDto = SlotDto.builder()
                .stockUnits(List.of(stockUnitDto))
                .build();

        when(pickListService.getPickListByCode(any())).thenReturn(pickListDto);
        when(slotService.getSlotByCode(any())).thenReturn(slotDto);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> pickingService.check(request)
        );

        assertTrue(ex.getMessage().contains("Quantità non valida"));
    }
}


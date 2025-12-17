package com.relatech.warehouse_management_system.picking;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.common.util.ErrorReason;
import com.relatech.warehouse_management_system.common.util.PickListItemState;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDto;
import com.relatech.warehouse_management_system.goodsIn.entity.service.StockUnitService;
import com.relatech.warehouse_management_system.outbound.dto.PickListDto;
import com.relatech.warehouse_management_system.outbound.dto.PickListItemDto;
import com.relatech.warehouse_management_system.outbound.entity.service.PickListItemService;
import com.relatech.warehouse_management_system.outbound.entity.service.PickListService;
import com.relatech.warehouse_management_system.picking.controller.PickingController;
import com.relatech.warehouse_management_system.picking.entity.PickingInfoDto;
import com.relatech.warehouse_management_system.picking.entity.service.PickingInfoService;
import com.relatech.warehouse_management_system.picking.service.PickingService;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDto;
import com.relatech.warehouse_management_system.warehouse.service.SlotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PickingServiceTest {

    private PickListService pickListService;
    private PickListItemService pickListItemService;
    private SlotService slotService;
    private PickingInfoService pickingInfoService;
    private StockUnitService stockUnitService;

    private PickingService pickingService;

    @BeforeEach
    void setUp() {
        pickListService = mock(PickListService.class);
        pickListItemService = mock(PickListItemService.class);
        slotService = mock(SlotService.class);
        pickingInfoService = mock(PickingInfoService.class);
        stockUnitService = mock(StockUnitService.class);

        pickingService = new PickingService(
                pickListService,
                pickListItemService,
                slotService,
                pickingInfoService,
                stockUnitService
        );
    }

    @Test
    void testConfirmPicking_fullPick_success() throws ResourceNotFoundException {
        // Mock request con più stock unit
        PickingController.Request request = new PickingController.Request();
        request.setPickListCode("PL-001");
        request.setPickListItemCode("PLI-001");
        Map<String, Integer> quantities = new HashMap<>();
        quantities.put("SU-001", 2);
        quantities.put("SU-002", 3);
        request.setStockUnitQuantities(quantities);
        request.setErrorReason(null);

        // Mock PickListItem con quantità totale = 5
        PickListItemDto itemDto = new PickListItemDto();
        itemDto.setCode("PLI-001");
        itemDto.setSlotCode("SLOT-001");
        itemDto.setQuantity(5);
        itemDto.setState(PickListItemState.OPEN);

        PickListDto pickListDto = new PickListDto();
        pickListDto.setPickListItemList(List.of(itemDto));
        when(pickListService.getPickListByCode("PL-001")).thenReturn(pickListDto);

        // Mock Slot con tutte le stock unit
        StockUnitDto su1 = new StockUnitDto();
        su1.setCode("SU-001");
        su1.setQuantity(2);
        StockUnitDto su2 = new StockUnitDto();
        su2.setCode("SU-002");
        su2.setQuantity(3);

        SlotDto slotDto = new SlotDto();
        slotDto.setStockUnits(List.of(su1, su2));
        when(slotService.getSlotByCode("SLOT-001")).thenReturn(slotDto);

        // Mock pickingInfoService
        when(pickingInfoService.create(any(PickingInfoDto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Call service
        pickingService.confirmPicking(request);

        // Verify stock updates
        verify(stockUnitService).updateQuantity("SU-001", 0);
        verify(stockUnitService).updateQuantity("SU-002", 0);

        // Verify picklist item state updated
        verify(pickListItemService).updateState("PLI-001", PickListItemState.PICKED);

        // Verify picking info created per stock unit
        ArgumentCaptor<PickingInfoDto> captor = ArgumentCaptor.forClass(PickingInfoDto.class);
        verify(pickingInfoService, times(2)).create(captor.capture());
        List<PickingInfoDto> createdInfos = captor.getAllValues();

        assertTrue(createdInfos.stream().anyMatch(info ->
                info.getStockUnitCode().equals("SU-001") && info.getQuantity() == 2));
        assertTrue(createdInfos.stream().anyMatch(info ->
                info.getStockUnitCode().equals("SU-002") && info.getQuantity() == 3));
    }

    @Test
    void testConfirmPicking_partialPick_setsErrorReason() throws ResourceNotFoundException {
        PickingController.Request request = new PickingController.Request();
        request.setPickListCode("PL-002");
        request.setPickListItemCode("PLI-002");

        // Pick parziale con più stock unit
        Map<String, Integer> quantities = new HashMap<>();
        quantities.put("SU-002", 2);
        quantities.put("SU-003", 1); // totale 3 pickati, item richiesto = 5
        request.setStockUnitQuantities(quantities);
        request.setErrorReason(null);

        // Mock PickListItem con quantità totale = 5
        PickListItemDto itemDto = new PickListItemDto();
        itemDto.setCode("PLI-002");
        itemDto.setSlotCode("SLOT-002");
        itemDto.setQuantity(5);
        itemDto.setState(PickListItemState.OPEN);

        PickListDto pickListDto = new PickListDto();
        pickListDto.setPickListItemList(List.of(itemDto));
        when(pickListService.getPickListByCode("PL-002")).thenReturn(pickListDto);

        // Mock Slot con tutte le stock unit
        StockUnitDto su2 = new StockUnitDto();
        su2.setCode("SU-002");
        su2.setQuantity(2);
        StockUnitDto su3 = new StockUnitDto();
        su3.setCode("SU-003");
        su3.setQuantity(1);

        SlotDto slotDto = new SlotDto();
        slotDto.setStockUnits(List.of(su2, su3));
        when(slotService.getSlotByCode("SLOT-002")).thenReturn(slotDto);

        // Mock pickingInfoService
        when(pickingInfoService.create(any(PickingInfoDto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Call service
        pickingService.confirmPicking(request);

        // Verify stock updated
        verify(stockUnitService).updateQuantity("SU-002", 0);
        verify(stockUnitService).updateQuantity("SU-003", 0);

        // Verify picklist item quantity decreased (restante = 2)
        verify(pickListItemService).updateQuantity("PLI-002", 2);

        // Verify error reason set
        verify(pickListItemService).updateErrorReason("PLI-002", ErrorReason.MISSING_QTY);

        // Verify picking info created per stock unit
        ArgumentCaptor<PickingInfoDto> captor = ArgumentCaptor.forClass(PickingInfoDto.class);
        verify(pickingInfoService, times(2)).create(captor.capture());
        List<PickingInfoDto> createdInfos = captor.getAllValues();

        assertTrue(createdInfos.stream().anyMatch(info ->
                info.getStockUnitCode().equals("SU-002") && info.getQuantity() == 2));
        assertTrue(createdInfos.stream().anyMatch(info ->
                info.getStockUnitCode().equals("SU-003") && info.getQuantity() == 1));
    }
}

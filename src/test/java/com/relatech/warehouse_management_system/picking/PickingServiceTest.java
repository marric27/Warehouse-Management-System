package com.relatech.warehouse_management_system.picking;

import com.relatech.warehouse_management_system.common.exception.MatchingDifferentCategoryException;
import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.common.util.ErrorReason;
import com.relatech.warehouse_management_system.common.util.PickListItemState;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDto;
import com.relatech.warehouse_management_system.goodsIn.entity.service.StockUnitService;
import com.relatech.warehouse_management_system.goodsIn.exception.QuantityMismatchException;
import com.relatech.warehouse_management_system.outbound.dto.PickListDto;
import com.relatech.warehouse_management_system.outbound.dto.PickListItemDto;
import com.relatech.warehouse_management_system.outbound.entity.service.PickListItemService;
import com.relatech.warehouse_management_system.outbound.entity.service.PickListService;
import com.relatech.warehouse_management_system.picking.dto.ConfirmPickingRequest;
import com.relatech.warehouse_management_system.picking.entity.PickingInfoDto;
import com.relatech.warehouse_management_system.picking.entity.service.PickingInfoService;
import com.relatech.warehouse_management_system.picking.service.PickingService;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDto;
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
    private PickingInfoService pickingInfoService;
    private StockUnitService stockUnitService;

    private PickingService pickingService;

    @BeforeEach
    void setUp() {
        pickListService = mock(PickListService.class);
        pickListItemService = mock(PickListItemService.class);
        pickingInfoService = mock(PickingInfoService.class);
        stockUnitService = mock(StockUnitService.class);

        pickingService = new PickingService(
                pickListService,
                pickListItemService,
                pickingInfoService,
                stockUnitService
        );
    }

    @Test
    void confirmPicking_fullPick_success() throws Exception {
        ConfirmPickingRequest request = new ConfirmPickingRequest();
        request.setPickListCode("PL-1");
        request.setPickListItemCode("PLI-1");
        request.setStockUnitQuantities(Map.of("SU-1", 5));

        PickListItemDto item = new PickListItemDto();
        item.setId(1L);
        item.setCode("PLI-1");
        item.setSlotCode("SLOT-1");
        item.setProductCode("PROD-1");
        item.setQuantity(5);
        item.setPickedQty(0);
        item.setState(PickListItemState.OPEN);

        PickListDto pl = new PickListDto();
        pl.setPickListItemList(List.of(item));
        when(pickListService.getPickListByCode("PL-1")).thenReturn(pl);

        StockUnitDto su = new StockUnitDto();
        su.setId(10L);
        su.setCode("SU-1");
        su.setProductCode("PROD-1");
        su.setQuantity(5);

        SlotDto slot = new SlotDto();
        slot.setStockUnits(List.of(su));

        when(pickingInfoService.create(any())).thenAnswer(i -> i.getArgument(0));
        when(stockUnitService.getStockUnitByCode("SU-1")).thenReturn(su);

        pickingService.confirmPicking(request);

        ArgumentCaptor<StockUnitDto> suCaptor = ArgumentCaptor.forClass(StockUnitDto.class);
        verify(stockUnitService).updateStockUnit(eq(10L), suCaptor.capture());
        assertEquals(0, suCaptor.getValue().getQuantity());

        ArgumentCaptor<PickListItemDto> pliCaptor = ArgumentCaptor.forClass(PickListItemDto.class);
        verify(pickListItemService).update(eq("PLI-1"), pliCaptor.capture());

        PickListItemDto updated = pliCaptor.getValue();
        assertEquals(5, updated.getPickedQty());
        assertEquals(PickListItemState.PICKED, updated.getState());

        verify(pickingInfoService).create(any(PickingInfoDto.class));
    }

    @Test
    void confirmPicking_partialPick_withErrorReason() throws Exception {
        ConfirmPickingRequest request = new ConfirmPickingRequest();
        request.setPickListCode("PL-2");
        request.setPickListItemCode("PLI-2");
        request.setStockUnitQuantities(Map.of("SU-2", 2));
        request.setErrorReason(ErrorReason.MISSING_QTY);

        PickListItemDto item = new PickListItemDto();
        item.setId(2L);
        item.setCode("PLI-2");
        item.setSlotCode("SLOT-2");
        item.setProductCode("PROD-2");
        item.setQuantity(5);
        item.setPickedQty(0);
        item.setState(PickListItemState.OPEN);

        PickListDto pl = new PickListDto();
        pl.setPickListItemList(List.of(item));
        when(pickListService.getPickListByCode("PL-2")).thenReturn(pl);

        StockUnitDto su = new StockUnitDto();
        su.setId(20L);
        su.setCode("SU-2");
        su.setProductCode("PROD-2");
        su.setQuantity(2);

        SlotDto slot = new SlotDto();
        slot.setStockUnits(List.of(su));

        when(pickingInfoService.create(any())).thenAnswer(i -> i.getArgument(0));
        when(stockUnitService.getStockUnitByCode("SU-2")).thenReturn(su);

        pickingService.confirmPicking(request);

        ArgumentCaptor<PickListItemDto> captor = ArgumentCaptor.forClass(PickListItemDto.class);
        verify(pickListItemService).update(eq("PLI-2"), captor.capture());

        PickListItemDto updated = captor.getValue();
        assertEquals(2, updated.getPickedQty());
        assertEquals(ErrorReason.MISSING_QTY, updated.getErrorReason());
        assertEquals(PickListItemState.PICKED, updated.getState());
    }

    @Test
    void confirmPicking_partialPick_withoutErrorReason_throwsException() throws ResourceNotFoundException {
        ConfirmPickingRequest request = new ConfirmPickingRequest();
        request.setPickListCode("PL-3");
        request.setPickListItemCode("PLI-3");
        request.setStockUnitQuantities(Map.of("SU-3", 1));

        PickListItemDto item = new PickListItemDto();
        item.setCode("PLI-3");
        item.setSlotCode("SLOT-3");
        item.setProductCode("PROD-3");
        item.setQuantity(5);
        item.setPickedQty(0);
        item.setState(PickListItemState.OPEN);

        PickListDto pl = new PickListDto();
        pl.setPickListItemList(List.of(item));
        when(pickListService.getPickListByCode("PL-3")).thenReturn(pl);

        StockUnitDto su = new StockUnitDto();
        su.setCode("SU-3");
        su.setProductCode("PROD-3");
        su.setQuantity(1);

        SlotDto slot = new SlotDto();
        slot.setStockUnits(List.of(su));

        assertThrows(Exception.class, () -> pickingService.confirmPicking(request));
    }

    @Test
    void testConfirmPicking_itemNotOpen_throwsException() throws ResourceNotFoundException {
        ConfirmPickingRequest request = new ConfirmPickingRequest();
        request.setPickListCode("PL-003");
        request.setPickListItemCode("PLI-003");
        request.setStockUnitQuantities(Map.of("SU-001", 1));

        PickListItemDto itemDto = new PickListItemDto();
        itemDto.setCode("PLI-003");
        itemDto.setState(PickListItemState.PICKED);

        PickListDto pickListDto = new PickListDto();
        pickListDto.setPickListItemList(List.of(itemDto));

        when(pickListService.getPickListByCode("PL-003")).thenReturn(pickListDto);

        assertThrows(IllegalStateException.class, () -> pickingService.confirmPicking(request));
    }

    @Test
    void testConfirmPicking_stockUnitNotFound_throwsException() throws ResourceNotFoundException {
        ConfirmPickingRequest request = new ConfirmPickingRequest();
        request.setPickListCode("PL-004");
        request.setPickListItemCode("PLI-004");
        request.setStockUnitQuantities(Map.of("SU-404", 1));

        PickListItemDto itemDto = new PickListItemDto();
        itemDto.setCode("PLI-004");
        itemDto.setSlotCode("SLOT-004");
        itemDto.setQuantity(1);
        itemDto.setState(PickListItemState.OPEN);

        PickListDto pickListDto = new PickListDto();
        pickListDto.setPickListItemList(List.of(itemDto));
        when(pickListService.getPickListByCode("PL-004")).thenReturn(pickListDto);

        SlotDto slotDto = new SlotDto();
        slotDto.setStockUnits(List.of()); // vuoto

        assertThrows(Exception.class, () -> pickingService.confirmPicking(request));
    }

    @Test
    void testConfirmPicking_quantityGreaterThanAvailable_throwsException() throws ResourceNotFoundException {
        ConfirmPickingRequest request = new ConfirmPickingRequest();
        request.setPickListCode("PL-006");
        request.setPickListItemCode("PLI-006");
        request.setStockUnitQuantities(Map.of("SU-006", 10));

        PickListItemDto itemDto = new PickListItemDto();
        itemDto.setCode("PLI-006");
        itemDto.setSlotCode("SLOT-006");
        itemDto.setQuantity(10);
        itemDto.setState(PickListItemState.OPEN);
        itemDto.setProductCode("PRD-001");

        PickListDto pickListDto = new PickListDto();
        pickListDto.setPickListItemList(List.of(itemDto));
        when(pickListService.getPickListByCode("PL-006")).thenReturn(pickListDto);

        StockUnitDto su = new StockUnitDto();
        su.setCode("SU-006");
        su.setProductCode("PRD-001");
        su.setQuantity(3);

        when(stockUnitService.getStockUnitByCode("SU-006")).thenReturn(su);

        SlotDto slotDto = new SlotDto();
        slotDto.setStockUnits(List.of(su));

        assertThrows(QuantityMismatchException.class, () -> pickingService.confirmPicking(request));
    }

    @Test
    void testConfirmPicking_totalPickedGreaterThanRequired_throwsException() throws ResourceNotFoundException {
        ConfirmPickingRequest request = new ConfirmPickingRequest();
        request.setPickListCode("PL-007");
        request.setPickListItemCode("PLI-007");
        request.setStockUnitQuantities(Map.of("SU-007", 6));

        PickListItemDto itemDto = new PickListItemDto();
        itemDto.setCode("PLI-007");
        itemDto.setSlotCode("SLOT-007");
        itemDto.setQuantity(5);
        itemDto.setState(PickListItemState.OPEN);

        PickListDto pickListDto = new PickListDto();
        pickListDto.setPickListItemList(List.of(itemDto));
        when(pickListService.getPickListByCode("PL-007")).thenReturn(pickListDto);

        StockUnitDto su = new StockUnitDto();
        su.setCode("SU-007");
        su.setQuantity(10);

        SlotDto slotDto = new SlotDto();
        slotDto.setStockUnits(List.of(su));

        assertThrows(QuantityMismatchException.class, () -> pickingService.confirmPicking(request));
    }

    @Test
    void testConfirmPicking_pickingDifferentProduct_throwsException() throws ResourceNotFoundException {
        ConfirmPickingRequest request = new ConfirmPickingRequest();
        request.setPickListCode("PL-006");
        request.setPickListItemCode("PLI-006");
        request.setStockUnitQuantities(Map.of("SU-006", 10));

        PickListItemDto itemDto = new PickListItemDto();
        itemDto.setCode("PLI-006");
        itemDto.setSlotCode("SLOT-006");
        itemDto.setQuantity(10);
        itemDto.setState(PickListItemState.OPEN);
        itemDto.setProductCode("PRD-002");

        PickListDto pickListDto = new PickListDto();
        pickListDto.setPickListItemList(List.of(itemDto));
        when(pickListService.getPickListByCode("PL-006")).thenReturn(pickListDto);

        StockUnitDto su = new StockUnitDto();
        su.setCode("SU-006");
        su.setProductCode("PRD-001");
        su.setQuantity(30);
        when(stockUnitService.getStockUnitByCode("SU-006")).thenReturn(su);

        SlotDto slotDto = new SlotDto();
        slotDto.setStockUnits(List.of(su));

        MatchingDifferentCategoryException ex = assertThrows(MatchingDifferentCategoryException.class, () -> pickingService.confirmPicking(request));
        assertTrue(ex.getMessage().contains("StockUnit SU-006 contains product PRD-001 but PickListItem requires product PRD-002"));

    }

    @Test
    void testCanPickFromSU_quantityExceeds_throwsException() {
        // Arrange
        Map<String, Integer> requested = new HashMap<>();
        requested.put("SU-001", 10); // richiede 10

        StockUnitDto su = StockUnitDto.builder()
                .code("SU-001")
                .productCode("PROD-01")
                .quantity(5) // ma ne abbiamo solo 5
                .build();

        Map<String, StockUnitDto> stockUnits = new HashMap<>();
        stockUnits.put(su.getCode(), su);

        PickListItemDto pickListItem = PickListItemDto.builder()
                .productCode("PROD-01")
                .build();

        // Act & Assert
        QuantityMismatchException exception = assertThrows(
                QuantityMismatchException.class,
                () -> pickingService.canPickFromSU(requested, stockUnits, pickListItem)
        );

        assertEquals("Requested quantity > available qty for stock unit: SU-001", exception.getMessage());
    }
}

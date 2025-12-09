package com.relatech.warehouse_management_system.goodsIn.serviceUnitTest;

import com.relatech.warehouse_management_system.common.util.Category;
import com.relatech.warehouse_management_system.common.util.State;
import com.relatech.warehouse_management_system.goodsIn.GrnItemStateService;
import com.relatech.warehouse_management_system.goodsIn.dto.*;
import com.relatech.warehouse_management_system.goodsIn.entity.service.*;
import com.relatech.warehouse_management_system.goodsIn.putaway.service.PutawayService;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDTO;
import com.relatech.warehouse_management_system.warehouse.service.SlotService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PutawayServiceTest {

    @Mock
    private SlotService slotService;
    @Mock
    private StockUnitService stockUnitService;
    @Mock
    private CheckingInfoService checkingInfoService;
    @Mock
    private GrnItemService grnItemService;
    @Mock
    private GrnItemStateService stateService;

    @InjectMocks
    private PutawayService service;

    @Test
    void assignStockUnitToSlot_categoryMismatch_throws() throws Exception {

        SlotDTO slot = new SlotDTO();
        slot.setId(1L);
        slot.setAllowedCategory(Category.STANDARD);
        slot.setStockUnits(new ArrayList<>());

        StockUnitDTO su = new StockUnitDTO();
        su.setId(10L);
        su.setCategory(Category.FLAMMABLE);

        when(slotService.getSlotById(1L)).thenReturn(slot);
        when(stockUnitService.getStockUnitById(10L)).thenReturn(su);

        assertThrows(IllegalArgumentException.class,
                () -> service.assignStockUnitToSlot(10L, 1L));

        verify(slotService, never()).updateSlot(anyLong(), any());
    }

    @Test
    void assignStockUnitToSlot_success() throws Exception {

        Long slotId = 1L;
        Long suId = 10L;

        // ------ SLOT ------
        SlotDTO slot = new SlotDTO();
        slot.setId(slotId);
        slot.setAllowedCategory(Category.STANDARD);
        slot.setStockUnits(new ArrayList<>());

        // ------ STOCK UNIT ------
        StockUnitDTO su = new StockUnitDTO();
        su.setId(suId);
        su.setCategory(Category.STANDARD);

        // ------ CHECKING INFO ------
        CheckingInfoDto ci = new CheckingInfoDto();
        ci.setId(20L);
        ci.setGrnItemId(100L);

        // ------ GRN ITEM ------
        GrnItemDto item = new GrnItemDto();
        item.setId(100L);

        // MOCK GET
        when(slotService.getSlotById(slotId)).thenReturn(slot);
        when(stockUnitService.getStockUnitById(suId)).thenReturn(su);
        when(checkingInfoService.getByStockUnitId(suId)).thenReturn(ci);
        when(grnItemService.getGrnItemById(ci.getGrnItemId())).thenReturn(item);

        // MOCK UPDATE
        SlotDTO savedSlot = new SlotDTO();
        savedSlot.setId(slotId);
        savedSlot.setStockUnits(new ArrayList<>());
        savedSlot.getStockUnits().add(su);

        when(slotService.updateSlot(slotId, slot)).thenReturn(savedSlot);
        when(checkingInfoService.update(ci.getId(), ci)).thenReturn(ci);

        doNothing().when(stateService).evaluateAndProgressItemState(item);

        // ------ CALL SERVICE ------
        SlotDTO result = service.assignStockUnitToSlot(suId, slotId);

        // ------ VERIFY SLOT + STOCK UNIT UPDATES ------
        assertEquals(slotId, result.getId());
        assertEquals(1, result.getStockUnits().size());
        assertEquals(slotId, su.getSlotId());

        verify(slotService).updateSlot(slotId, slot);

        // ------ VERIFY CHECKING INFO ------
        assertEquals(State.PUTAWAY, ci.getState());
        verify(checkingInfoService).update(ci.getId(), ci);

        // ------ VERIFY ITEM STATE PROGRESSION ------
        verify(stateService).evaluateAndProgressItemState(item);

        assertNotNull(result);
    }
}

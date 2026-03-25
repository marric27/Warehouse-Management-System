package com.relatech.warehouse_management_system.goodsIn.serviceUnitTest;

import com.relatech.warehouse_management_system.common.util.Category;
import com.relatech.warehouse_management_system.common.util.State;
import com.relatech.warehouse_management_system.goodsIn.dto.*;
import com.relatech.warehouse_management_system.goodsIn.entity.service.*;
import com.relatech.warehouse_management_system.goodsIn.events.GrnItemPutawayAssignedEvent;
import com.relatech.warehouse_management_system.goodsIn.putaway.service.PutawayService;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDto;
import com.relatech.warehouse_management_system.warehouse.service.SlotService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import org.springframework.context.ApplicationEventPublisher;

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
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PutawayService service;

    @Test
    void assignStockUnitToSlot_categoryMismatch_throws() throws Exception {

        SlotDto slot = new SlotDto();
        slot.setId(1L);
        slot.setCode("slotcode");
        slot.setCategory(Category.STANDARD);
        slot.setStockUnits(new ArrayList<>());

        StockUnitDto su = new StockUnitDto();
        su.setId(10L);
        su.setCode("sucode");
        su.setCategory(Category.FLAMMABLE);

        when(slotService.getSlotByCode(slot.getCode())).thenReturn(slot);
        when(stockUnitService.getStockUnitByCode(su.getCode())).thenReturn(su);

        assertThrows(IllegalArgumentException.class,
                () -> service.assignStockUnitToSlot("sucode", "slotcode"));

        verify(slotService, never()).updateSlot(anyLong(), any());
    }

    @Test
    void assignStockUnitToSlot_success() throws Exception {

        Long slotId = 1L;
        Long suId = 10L;
        String slotCode = "slotCode";
        String suCode = "suCode";

        // ------ SLOT ------
        SlotDto slot = new SlotDto();
        slot.setId(slotId);
        slot.setCode(slotCode);
        slot.setCategory(Category.STANDARD);
        slot.setStockUnits(new ArrayList<>());

        // ------ STOCK UNIT ------
        StockUnitDto su = new StockUnitDto();
        su.setId(suId);
        su.setCode(suCode);
        su.setCategory(Category.STANDARD);

        // ------ CHECKING INFO ------
        CheckingInfoDto ci = new CheckingInfoDto();
        ci.setId(20L);
        ci.setGrnItemId(100L);

        // ------ GRN ITEM ------
        GrnItemDto item = new GrnItemDto();
        item.setId(100L);
        item.setState(State.CHECKING);
        item.setGrnId(200L);

        // MOCK GET
        when(slotService.getSlotByCode(slot.getCode())).thenReturn(slot);
        when(stockUnitService.getStockUnitByCode(su.getCode())).thenReturn(su);
        when(checkingInfoService.getByStockUnitId(suId)).thenReturn(ci);
        when(grnItemService.getGrnItemById(ci.getGrnItemId())).thenReturn(item);

        // MOCK UPDATE
        SlotDto savedSlot = new SlotDto();
        savedSlot.setId(slotId);
        savedSlot.setStockUnits(new ArrayList<>());
        savedSlot.getStockUnits().add(su);

        when(slotService.updateSlot(slotId, slot)).thenReturn(savedSlot);
        when(checkingInfoService.update(ci.getId(), ci)).thenReturn(ci);


        // ------ CALL SERVICE ------
        SlotDto result = service.assignStockUnitToSlot(suCode, slotCode);

        // ------ VERIFY SLOT + STOCK UNIT UPDATES ------
        assertEquals(slotId, result.getId());
        assertEquals(1, result.getStockUnits().size());
        assertEquals(slotId, su.getSlotId());

        verify(slotService).updateSlot(slotId, slot);

        // ------ VERIFY CHECKING INFO ------
        assertEquals(State.PUTAWAY, ci.getState());
        verify(checkingInfoService).update(ci.getId(), ci);

        // ------ VERIFY PUTAWAY EVENT ------
        ArgumentCaptor<GrnItemPutawayAssignedEvent> eventCaptor = ArgumentCaptor.forClass(GrnItemPutawayAssignedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        GrnItemPutawayAssignedEvent event = eventCaptor.getValue();
        assertEquals(item.getId(), event.grnItemId());
        assertEquals(item.getState(), event.oldState());
        assertEquals(item.getState(), event.newState());
        assertEquals(item.getGrnId(), event.grnId());

        assertNotNull(result);
    }
}

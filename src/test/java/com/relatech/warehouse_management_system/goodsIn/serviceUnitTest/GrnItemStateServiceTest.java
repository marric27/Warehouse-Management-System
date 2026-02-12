package com.relatech.warehouse_management_system.goodsIn.serviceUnitTest;

import com.relatech.warehouse_management_system.common.util.State;
import com.relatech.warehouse_management_system.goodsIn.GrnItemStateService;
import com.relatech.warehouse_management_system.goodsIn.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnDto;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnItemService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnService;
import com.relatech.warehouse_management_system.goodsIn.exception.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrnItemStateServiceTest {

    @Mock
    private GrnService grnService;

    @Mock
    private GrnItemService grnItemService;

    @InjectMocks
    private GrnItemStateService stateService;

    @Test
    void validateItemQuantities_expectedZero_throws() {
        GrnItemDto item = new GrnItemDto();
        item.setExpectedQty(0);

        assertThrows(InvalidQuantityException.class, () ->
                stateService.validateItemQuantities(item));
    }

    @Test
    void validateItemQuantities_quantityMismatch_throws() {
        GrnItemDto item = new GrnItemDto();
        item.setExpectedQty(10);
        item.setReceivedQty(5);
        item.setCompliantQty(3);
        item.setNotCompliantQty(1); // mismatch

        assertThrows(QuantityMismatchException.class, () ->
                stateService.validateItemQuantities(item));
    }

    @Test
    void validateItemQuantities_overReceived_throws() {
        GrnItemDto item = new GrnItemDto();
        item.setExpectedQty(10);
        item.setReceivedQty(12);
        item.setCompliantQty(6);
        item.setNotCompliantQty(6);

        assertThrows(OverReceivedQuantityException.class, () ->
                stateService.validateItemQuantities(item));
    }

    @Test
    void validateItemQuantities_receivedZero_setsPutaway() throws Exception {
        GrnItemDto item = new GrnItemDto();
        item.setExpectedQty(10);
        item.setReceivedQty(0);
        item.setCompliantQty(0);
        item.setNotCompliantQty(0);

        stateService.validateItemQuantities(item);

        assertEquals(State.PUTAWAY, item.getState());
    }

    @Test
    void validateItemQuantities_valid_noException() {
        GrnItemDto item = new GrnItemDto();
        item.setExpectedQty(10);
        item.setReceivedQty(10);
        item.setCompliantQty(6);
        item.setNotCompliantQty(4);

        assertDoesNotThrow(() -> stateService.validateItemQuantities(item));
    }

    @Test
    void evaluateAndProgressItemState_openToChecked() throws Exception {
        GrnItemDto item = new GrnItemDto();
        item.setId(1L);
        item.setExpectedQty(10);
        item.setState(State.OPEN);

        CheckingInfoDto c1 = new CheckingInfoDto();
        c1.setQuantity(10);

        item.setCheckingInfoList(List.of(c1));

        when(grnItemService.updateGrnItem(eq(1L), any())).thenReturn(item);

        stateService.evaluateAndProgressItemState(item);

        assertEquals(State.CHECKED, item.getState());
    }

    @Test
    void evaluateAndProgressItemState_checkedToPutaway() throws Exception {
        GrnItemDto item = new GrnItemDto();
        item.setId(1L);
        item.setGrnId(5L);
        item.setExpectedQty(10);
        item.setState(State.CHECKED);

        CheckingInfoDto c1 = new CheckingInfoDto();
        c1.setQuantity(5);
        c1.setState(State.PUTAWAY);

        CheckingInfoDto c2 = new CheckingInfoDto();
        c2.setQuantity(5);
        c2.setState(State.PUTAWAY);

        item.setCheckingInfoList(List.of(c1, c2));

        // mock update
        when(grnItemService.updateGrnItem(eq(1L), any())).thenReturn(item);

        // mock GRN
        GrnDto grn = new GrnDto();
        grn.setItems(List.of(item));
        when(grnService.getGRNById(5L)).thenReturn(grn);

        stateService.evaluateAndProgressItemState(item);

        assertEquals(State.PUTAWAY, item.getState());
        verify(grnService).updateGRN(eq(5L), any());
    }


    @Test
    void evaluateAndProgressItemState_noProgress_whenConditionsNotMet() throws Exception {
        GrnItemDto item = new GrnItemDto();
        item.setId(1L);
        item.setExpectedQty(10);
        item.setReceivedQty(10);
        item.setState(State.OPEN);
        item.setCheckingInfoList(List.of()); // no assigned qty

        stateService.evaluateAndProgressItemState(item);

        assertEquals(State.OPEN, item.getState());
        verify(grnItemService, never()).updateGrnItem(anyLong(), any());
    }

    @Test
    void evaluateAndProgressGrnState_allPutaway_closesGrn() throws Exception {
        GrnItemDto i1 = new GrnItemDto();
        i1.setState(State.PUTAWAY);

        GrnItemDto i2 = new GrnItemDto();
        i2.setState(State.PUTAWAY);

        GrnDto grn = new GrnDto();
        grn.setId(5L);
        grn.setItems(List.of(i1, i2));

        when(grnService.getGRNById(5L)).thenReturn(grn);

        stateService.evaluateAndProgressGrnState(5L);

        verify(grnService).updateGRN(eq(5L), any());
    }

    @Test
    void evaluateAndProgressGrnState_notAllPutaway_doesNotCloseGrn() throws Exception {
        GrnItemDto i1 = new GrnItemDto();
        i1.setState(State.PUTAWAY);

        GrnItemDto i2 = new GrnItemDto();
        i2.setState(State.CHECKED);

        GrnDto grn = new GrnDto();
        grn.setId(5L);
        grn.setItems(List.of(i1, i2));

        when(grnService.getGRNById(5L)).thenReturn(grn);

        stateService.evaluateAndProgressGrnState(5L);

        verify(grnService, never()).updateGRN(anyLong(), any());
    }

    @Test
    void checkGrnIfClosed_true() throws Exception {
        GrnDto grn = new GrnDto();
        grn.setState(State.CLOSED);

        when(grnService.getGRNById(10L)).thenReturn(grn);

        assertTrue(stateService.checkGrnIfClosed(10L));
    }


    @Test
    void checkGrnIfClosed_false() throws Exception {
        GrnDto grn = new GrnDto();
        grn.setState(State.OPEN);

        when(grnService.getGRNById(10L)).thenReturn(grn);

        assertFalse(stateService.checkGrnIfClosed(10L));
    }

    @Test
    void checkGrnItemIfCheckedOrPutaway_checked() throws Exception {
        GrnItemDto item = new GrnItemDto();
        item.setCode("item1");
        item.setState(State.CHECKED);

        when(grnItemService.getGrnItemByCode(item.getCode())).thenReturn(item);

        assertTrue(stateService.checkGrnItemIfCheckedOrPutaway(item.getCode()));
    }

    @Test
    void checkGrnItemIfCheckedOrPutaway_open_false() throws Exception {
        GrnItemDto item = new GrnItemDto();
        item.setCode("item1");
        item.setState(State.OPEN);

        when(grnItemService.getGrnItemByCode(item.getCode())).thenReturn(item);

        assertFalse(stateService.checkGrnItemIfCheckedOrPutaway(item.getCode()));
    }
}

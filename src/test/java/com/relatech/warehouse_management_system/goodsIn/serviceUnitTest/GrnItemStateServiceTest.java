package com.relatech.warehouse_management_system.goodsIn.serviceUnitTest;

import com.relatech.warehouse_management_system.common.util.State;
import com.relatech.warehouse_management_system.goodsIn.GrnItemStateService;
import com.relatech.warehouse_management_system.goodsIn.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnDto;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnItemService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnService;
import com.relatech.warehouse_management_system.goodsIn.exception.*;
import com.relatech.warehouse_management_system.goodsIn.states.GrnItemStateHandler;
import com.relatech.warehouse_management_system.goodsIn.states.GrnItemStateHandlerResolver;

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

    @Mock
    private GrnItemStateHandlerResolver resolver;

    @Mock
    private GrnItemStateHandler openHandler;

    @Mock
    private GrnItemStateHandler checkedHandler;

    @Mock
    private GrnItemStateHandler putawayHandler;

    @InjectMocks
    private GrnItemStateService stateService;

    private void mockDefaultHandlers() {
        when(resolver.resolve(State.OPEN)).thenReturn(openHandler);
        when(resolver.resolve(State.CHECKED)).thenReturn(checkedHandler);
        when(resolver.resolve(State.PUTAWAY)).thenReturn(putawayHandler);
        when(resolver.resolve(null)).thenReturn(openHandler);

        lenient().when(openHandler.onQuantitiesValidated(any())).thenAnswer(invocation -> {
            GrnItemDto item = invocation.getArgument(0);
            return item.getReceivedQty() == 0 ? State.PUTAWAY : State.OPEN;
        });
        lenient().when(checkedHandler.onQuantitiesValidated(any())).thenAnswer(invocation -> {
            GrnItemDto item = invocation.getArgument(0);
            return item.getReceivedQty() == 0 ? State.PUTAWAY : State.CHECKED;
        });
        lenient().when(putawayHandler.onQuantitiesValidated(any())).thenReturn(State.PUTAWAY);
    }

    @Test
    void validateItemQuantities_expectedZero_throws() {
        mockDefaultHandlers();
        GrnItemDto item = new GrnItemDto();
        item.setExpectedQty(0);

        assertThrows(InvalidQuantityException.class, () ->
                stateService.validateItemQuantities(item));
    }

    @Test
    void validateItemQuantities_quantityMismatch_throws() {
        mockDefaultHandlers();
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
        mockDefaultHandlers();
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
        mockDefaultHandlers();
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
        mockDefaultHandlers();
        GrnItemDto item = new GrnItemDto();
        item.setExpectedQty(10);
        item.setReceivedQty(10);
        item.setCompliantQty(6);
        item.setNotCompliantQty(4);

        assertDoesNotThrow(() -> stateService.validateItemQuantities(item));
    }

    @Test
    void evaluateAndProgressItemState_openToChecked() throws Exception {
        mockDefaultHandlers();
        GrnItemDto item = new GrnItemDto();
        item.setId(1L);
        item.setExpectedQty(10);
        item.setState(State.OPEN);

        CheckingInfoDto c1 = new CheckingInfoDto();
        c1.setQuantity(10);

        item.setCheckingInfoList(List.of(c1));

        when(grnItemService.updateGrnItem(eq(1L), any())).thenReturn(item);
        when(openHandler.onCheckingInfoAdded(item)).thenReturn(State.CHECKED);
        when(openHandler.canTransitionTo(State.CHECKED, item)).thenReturn(true);
        when(checkedHandler.onPutawayAssigned(item)).thenReturn(State.CHECKED);

        stateService.evaluateAndProgressItemState(item);

        assertEquals(State.CHECKED, item.getState());
    }

    @Test
    void evaluateAndProgressItemState_checkedToPutaway() throws Exception {
        mockDefaultHandlers();
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
        when(checkedHandler.onCheckingInfoAdded(item)).thenReturn(State.PUTAWAY);
        when(checkedHandler.canTransitionTo(State.PUTAWAY, item)).thenReturn(true);
        when(putawayHandler.onPutawayAssigned(item)).thenReturn(State.PUTAWAY);

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
        mockDefaultHandlers();
        GrnItemDto item = new GrnItemDto();
        item.setId(1L);
        item.setExpectedQty(10);
        item.setReceivedQty(10);
        item.setState(State.OPEN);
        item.setCheckingInfoList(List.of()); // no assigned qty

        when(openHandler.onCheckingInfoAdded(item)).thenReturn(State.OPEN);
        when(openHandler.onPutawayAssigned(item)).thenReturn(State.OPEN);

        stateService.evaluateAndProgressItemState(item);

        assertEquals(State.OPEN, item.getState());
        verify(grnItemService, never()).updateGrnItem(anyLong(), any());
    }

    @Test
    void evaluateAndProgressGrnState_allPutaway_closesGrn() throws Exception {
        mockDefaultHandlers();
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
        mockDefaultHandlers();
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
        mockDefaultHandlers();
        GrnDto grn = new GrnDto();
        grn.setState(State.CLOSED);

        when(grnService.getGRNById(10L)).thenReturn(grn);

        assertTrue(stateService.checkGrnIfClosed(10L));
    }


    @Test
    void checkGrnIfClosed_false() throws Exception {
        mockDefaultHandlers();
        GrnDto grn = new GrnDto();
        grn.setState(State.OPEN);

        when(grnService.getGRNById(10L)).thenReturn(grn);

        assertFalse(stateService.checkGrnIfClosed(10L));
    }

    @Test
    void canAssignCheckingInfo_checked_false() {
        GrnItemDto item = new GrnItemDto();
        item.setState(State.CHECKED);
        mockDefaultHandlers();
        when(checkedHandler.canAssignCheckingInfo(item)).thenReturn(false);

        assertFalse(stateService.canAssignCheckingInfo(item));
    }

    @Test
    void canAssignCheckingInfo_open_true() {
        GrnItemDto item = new GrnItemDto();
        item.setState(State.OPEN);
        mockDefaultHandlers();
        when(openHandler.canAssignCheckingInfo(item)).thenReturn(true);

        assertTrue(stateService.canAssignCheckingInfo(item));
    }
}

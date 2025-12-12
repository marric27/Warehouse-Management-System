package com.relatech.warehouse_management_system.goodsIn.serviceUnitTest;

import com.relatech.warehouse_management_system.common.util.State;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnDto;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnItemService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnService;
import com.relatech.warehouse_management_system.goodsIn.exception.*;
import com.relatech.warehouse_management_system.goodsIn.GrnItemStateService;

import com.relatech.warehouse_management_system.goodsIn.receiving.service.ReceivingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReceivingServiceTest - Extended CRUD and Business Logic")
class ReceivingServiceTest {

    @Mock private GrnService grnService;
    @Mock private GrnItemService grnItemService;
    @Mock private GrnItemStateService stateService;

    @InjectMocks
    private ReceivingService receivingService;

    @Test
    void createGRN_success() {
        GrnDto dto = new GrnDto();
        dto.setReceivingDate(null);

        GrnDto saved = new GrnDto();
        saved.setState(State.OPEN);

        when(grnService.createGRN(any())).thenReturn(saved);

        GrnDto result = receivingService.createGRN(dto);

        assertEquals(State.OPEN, result.getState());
        assertNotNull(dto.getReceivingDate());
    }

    @Test
    void getGRN_success() throws Exception {
        GrnDto dto = new GrnDto();
        dto.setId(1L);

        when(grnService.getGRNById(1L)).thenReturn(dto);

        GrnDto result = receivingService.getGRN(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void listGrn_returnsList() {
        when(grnService.getAllGRNs()).thenReturn(List.of(new GrnDto()));

        List<GrnDto> result = receivingService.listGrn();

        assertEquals(1, result.size());
    }

    @Test
    void listGrnPaged_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<GrnDto> page = new PageImpl<>(List.of(new GrnDto()));

        when(grnService.getAllGRNsPaged(pageable)).thenReturn(page);

        Page<GrnDto> result = receivingService.listGrnPaged(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void listGrnItems_returnsList() {
        when(grnItemService.getAllGrnItems()).thenReturn(List.of(new GrnItemDto()));

        List<GrnItemDto> result = receivingService.listGrnItems();

        assertEquals(1, result.size());
    }

    @Test
    void listGrnItemsPaged_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<GrnItemDto> page = new PageImpl<>(List.of(new GrnItemDto()));

        when(grnItemService.getAllGrnItemsPaged(pageable)).thenReturn(page);

        Page<GrnItemDto> result = receivingService.listGrnItemsPaged(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void createItem_throwsGrnNotFound() throws Exception {
        when(grnService.getGRNById(10L)).thenReturn(null);

        assertThrows(GrnNotFoundException.class, () -> {
            receivingService.createItem(10L, new GrnItemDto());
        });
    }

    @Test
    void createItem_throwsGrnClosed() throws Exception {
        when(grnService.getGRNById(10L)).thenReturn(new GrnDto());
        when(stateService.checkGrnIfClosed(10L)).thenReturn(true);

        assertThrows(CannotAssignItemToGrnClosedException.class, () -> {
            receivingService.createItem(10L, new GrnItemDto());
        });
    }

    @Test
    void createItem_success() throws Exception {
        GrnItemDto item = new GrnItemDto();
        GrnItemDto savedItem = new GrnItemDto();
        savedItem.setId(1L);

        when(grnService.getGRNById(5L)).thenReturn(new GrnDto());
        when(stateService.checkGrnIfClosed(5L)).thenReturn(false);
        doNothing().when(stateService).validateItemQuantities(any());
        when(grnItemService.createGrnItem(any())).thenReturn(savedItem);
        doNothing().when(stateService).evaluateAndProgressItemState(savedItem);

        GrnItemDto result = receivingService.createItem(5L, item);

        assertEquals(1L, result.getId());
        assertEquals(5L, item.getGrnId());
    }

    @Test
    void updateItem_success() throws Exception {
        GrnItemDto existing = new GrnItemDto();
        existing.setExpectedQty(10);
        existing.setReceivedQty(5);
        existing.setCompliantQty(5);
        existing.setNotCompliantQty(0);

        GrnItemDto update = new GrnItemDto();
        update.setExpectedQty(20);
        update.setReceivedQty(10);
        update.setCompliantQty(9);
        update.setNotCompliantQty(1);

        when(grnItemService.getGrnItemById(1L)).thenReturn(existing);
        when(grnItemService.updateGrnItem(anyLong(), any())).thenReturn(existing);

        GrnItemDto result = receivingService.updateItem(1L, update);

        assertEquals(20, result.getExpectedQty());
        assertEquals(10, result.getReceivedQty());
        verify(stateService).validateItemQuantities(existing);
        verify(stateService).evaluateAndProgressItemState(existing);
    }

    @Test
    void createItem_invalidQuantity_throws() throws Exception {
        when(grnService.getGRNById(1L)).thenReturn(new GrnDto());
        when(stateService.checkGrnIfClosed(1L)).thenReturn(false);

        GrnItemDto item = new GrnItemDto();

        // simuliamo eccezione QuantityMismatch
        doThrow(new QuantityMismatchException("mismatch"))
                .when(stateService).validateItemQuantities(item);

        assertThrows(QuantityMismatchException.class,
                () -> receivingService.createItem(1L, item));
    }

    @Test
    void createItem_expectedQtyZero_throwsInvalidQuantity() throws Exception {
        when(grnService.getGRNById(1L)).thenReturn(new GrnDto());
        when(stateService.checkGrnIfClosed(1L)).thenReturn(false);

        GrnItemDto item = new GrnItemDto();
        item.setExpectedQty(0);
        item.setReceivedQty(0);
        item.setCompliantQty(0);
        item.setNotCompliantQty(0);

        doThrow(new InvalidQuantityException("Expected qty must be > 0"))
                .when(stateService).validateItemQuantities(item);

        assertThrows(InvalidQuantityException.class,
                () -> receivingService.createItem(1L, item));
    }

    // -----------------------------------------------------
    // Edge case: receivedQty > expectedQty
    // -----------------------------------------------------
    @Test
    void createItem_receivedGreaterThanExpected_throwsOverReceived() throws Exception {
        when(grnService.getGRNById(1L)).thenReturn(new GrnDto());
        when(stateService.checkGrnIfClosed(1L)).thenReturn(false);

        GrnItemDto item = new GrnItemDto();
        item.setExpectedQty(10);
        item.setReceivedQty(15);
        item.setCompliantQty(15);
        item.setNotCompliantQty(0);

        doThrow(new OverReceivedQuantityException("Over-received: expected=10 received=15"))
                .when(stateService).validateItemQuantities(item);

        assertThrows(OverReceivedQuantityException.class,
                () -> receivingService.createItem(1L, item));
    }

    // -----------------------------------------------------
    // Null fields: item senza receivedQty/compliantQty
    // -----------------------------------------------------
    @Test
    void createItem_nullFields_throwsException() throws Exception {
        when(grnService.getGRNById(1L)).thenReturn(new GrnDto());
        when(stateService.checkGrnIfClosed(1L)).thenReturn(false);

        GrnItemDto item = new GrnItemDto();
        // tutte quantità null → simulate validateItemQuantities che lancia NullPointerException
        doThrow(new NullPointerException("Quantities cannot be null"))
                .when(stateService).validateItemQuantities(item);

        assertThrows(NullPointerException.class,
                () -> receivingService.createItem(1L, item));
    }

    // -----------------------------------------------------
    // Malformed object: liste null o valori negativi
    // -----------------------------------------------------
    @Test
    void createItem_malformedItem_throwsException() throws Exception {
        when(grnService.getGRNById(1L)).thenReturn(new GrnDto());
        when(stateService.checkGrnIfClosed(1L)).thenReturn(false);

        GrnItemDto item = new GrnItemDto();
        item.setExpectedQty(-5);
        item.setReceivedQty(-1);
        item.setCompliantQty(-1);
        item.setNotCompliantQty(-1);
        item.setCheckingInfoList(null); // lista null

        doThrow(new InvalidQuantityException("Quantities cannot be negative"))
                .when(stateService).validateItemQuantities(item);

        assertThrows(InvalidQuantityException.class,
                () -> receivingService.createItem(1L, item));
    }
}

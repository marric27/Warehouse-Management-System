package com.relatech.warehouse_management_system.goodsIn.serviceUnitTest;

import com.relatech.warehouse_management_system.goodsIn.GrnItemStateService;
import com.relatech.warehouse_management_system.goodsIn.checkGoodsIn.service.CheckGoodsInService;
import com.relatech.warehouse_management_system.goodsIn.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDTO;
import com.relatech.warehouse_management_system.goodsIn.entity.service.CheckingInfoService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnItemService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.StockUnitService;
import com.relatech.warehouse_management_system.goodsIn.exception.CannotAssignCIToGrnItemInClosedOrPutawayStateException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckGoodsInServiceTest {

    @Mock
    private StockUnitService stockUnitService;

    @Mock
    private CheckingInfoService checkingInfoService;

    @Mock
    private GrnItemService grnItemService;

    @Mock
    private GrnItemStateService stateService;

    @InjectMocks
    private CheckGoodsInService service;

    @Test
    void createCheckingInfoAndStockUnit_itemInCheckedOrPutaway_throws() throws Exception {

        when(stateService.checkGrnItemIfCheckedOrPutaway(5L)).thenReturn(true);

        assertThrows(
                CannotAssignCIToGrnItemInClosedOrPutawayStateException.class,
                () -> service.createCheckingInfoAndStockUnit(5L, new CheckingInfoDto(), new StockUnitDTO())
        );
    }

    @Test
    void createCheckingInfoAndStockUnit_success() throws Exception {

        Long grnItemId = 5L;

        // ------ Mock controlli iniziali ------
        when(stateService.checkGrnItemIfCheckedOrPutaway(grnItemId)).thenReturn(false);

        // ------ MOCK STOCK UNIT CREATION ------
        StockUnitDTO su = new StockUnitDTO();
        StockUnitDTO savedSu = new StockUnitDTO();
        savedSu.setId(100L);

        when(stockUnitService.createStockUnit(su)).thenReturn(savedSu);

        // ------ MOCK CHECKING INFO CREATION ------
        CheckingInfoDto ci = new CheckingInfoDto();
        CheckingInfoDto savedCi = new CheckingInfoDto();
        savedCi.setId(200L);

        when(checkingInfoService.create(any())).thenReturn(savedCi);

        // ------ MOCK ITEM ------
        GrnItemDto item = new GrnItemDto();
        item.setId(grnItemId);

        when(grnItemService.getGrnItemById(grnItemId)).thenReturn(item);

        doNothing().when(grnItemService).addCheckingInfo(grnItemId, 200L);
        doNothing().when(stateService).evaluateAndProgressItemState(item);

        // ------ CALL SERVICE ------
        GrnItemDto result = service.createCheckingInfoAndStockUnit(grnItemId, ci, su);

        // ------ VERIFY STOCK UNIT ------
        verify(stockUnitService).createStockUnit(su);

        // ------ VERIFY CHECKING INFO ------
        assertEquals(100L, ci.getStockUnitId());
        assertEquals(grnItemId, ci.getGrnItemId());
        verify(checkingInfoService).create(ci);

        // ------ VERIFY ADD CHECKING INFO ------
        verify(grnItemService).addCheckingInfo(grnItemId, 200L);

        // ------ VERIFY STATE PROGRESSION ------
        verify(stateService).evaluateAndProgressItemState(item);

        assertEquals(item, result);
    }

    @Test
    void listCheckinginfo_success() {
        when(checkingInfoService.getAll()).thenReturn(List.of(new CheckingInfoDto()));

        List<CheckingInfoDto> result = service.listCheckinginfo();

        assertEquals(1, result.size());
    }

    @Test
    void listCIPaged_success() {
        Pageable pageable = mock(Pageable.class);
        Page<CheckingInfoDto> page = new PageImpl<>(List.of(new CheckingInfoDto()));

        when(checkingInfoService.getAllPaged(pageable)).thenReturn(page);

        Page<CheckingInfoDto> result = service.listCIPaged(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void listStockUnit_success() {
        when(stockUnitService.getAllStockUnits()).thenReturn(List.of(new StockUnitDTO()));

        List<StockUnitDTO> result = service.listStockUnit();

        assertEquals(1, result.size());
    }

    @Test
    void listStockUnitPaged_success() {
        Pageable pageable = mock(Pageable.class);
        Page<StockUnitDTO> page = new PageImpl<>(List.of(new StockUnitDTO()));

        when(stockUnitService.getAllStockUnitsPaged(pageable)).thenReturn(page);

        Page<StockUnitDTO> result = service.listStockUnitPaged(pageable);

        assertEquals(1, result.getTotalElements());
    }
}

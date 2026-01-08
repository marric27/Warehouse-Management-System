package com.relatech.warehouse_management_system.goodsIn.serviceUnitTest;

import com.relatech.warehouse_management_system.common.util.State;
import com.relatech.warehouse_management_system.goodsIn.GrnItemStateService;
import com.relatech.warehouse_management_system.goodsIn.checkGoodsIn.service.CheckGoodsInService;
import com.relatech.warehouse_management_system.goodsIn.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDto;
import com.relatech.warehouse_management_system.goodsIn.entity.service.CheckingInfoService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnItemService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.StockUnitService;
import com.relatech.warehouse_management_system.goodsIn.exception.CannotAssignCIToGrnItemInClosedOrPutawayStateException;

import com.relatech.warehouse_management_system.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
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

    @Mock
    private ProductService productService;

    @InjectMocks
    private CheckGoodsInService service;

    @Test
    void createCheckingInfoAndStockUnit_itemInCheckedOrPutaway_throws() throws Exception {

        when(stateService.checkGrnItemIfCheckedOrPutaway(5L)).thenReturn(true);

        assertThrows(
                CannotAssignCIToGrnItemInClosedOrPutawayStateException.class,
                () -> service.createCheckingInfoAndStockUnit(5L, new StockUnitDto())
        );
    }

    @Test
    void createCheckingInfoAndStockUnit_success() throws Exception {
        // given
        Long grnItemId = 1L;

        StockUnitDto inputSu = new StockUnitDto();
        inputSu.setProductCode("PROD-001");
        inputSu.setQuantity(10);
        inputSu.setBatchNumber("BATCH-1");
        inputSu.setExpirationDate(LocalDate.now().plusDays(30));

        StockUnitDto createdSu = new StockUnitDto();
        createdSu.setId(100L);

        CheckingInfoDto savedCi = new CheckingInfoDto();
        savedCi.setId(200L);

        GrnItemDto grnItem = new GrnItemDto();
        grnItem.setId(grnItemId);

        when(stateService.checkGrnItemIfCheckedOrPutaway(grnItemId))
                .thenReturn(false);

        when(stockUnitService.createStockUnit(inputSu))
                .thenReturn(createdSu);

        when(checkingInfoService.create(any(CheckingInfoDto.class)))
                .thenReturn(savedCi);

        when(grnItemService.getGrnItemById(grnItemId))
                .thenReturn(grnItem);

        // when
        GrnItemDto result =
                service.createCheckingInfoAndStockUnit(grnItemId, inputSu);

        // then
        assertNotNull(result);
        assertEquals(grnItemId, result.getId());

        verify(productService).validateProductExists("PROD-001");
        verify(stockUnitService).createStockUnit(inputSu);

        verify(checkingInfoService).create(argThat(ci ->
                ci.getStockUnitId().equals(100L) &&
                        ci.getGrnItemId().equals(grnItemId) &&
                        ci.getState() == State.OPEN &&
                        ci.getQuantity().equals(10) &&
                        ci.getBatchNumber().equals("BATCH-1")
        ));

        verify(grnItemService).addCheckingInfo(grnItemId, 200L);
        verify(stateService).evaluateAndProgressItemState(grnItem);
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
        when(stockUnitService.getAllStockUnits()).thenReturn(List.of(new StockUnitDto()));

        List<StockUnitDto> result = service.listStockUnit();

        assertEquals(1, result.size());
    }

    @Test
    void listStockUnitPaged_success() {
        Pageable pageable = mock(Pageable.class);
        Page<StockUnitDto> page = new PageImpl<>(List.of(new StockUnitDto()));

        when(stockUnitService.getAllStockUnitsPaged(pageable)).thenReturn(page);

        Page<StockUnitDto> result = service.listStockUnitPaged(pageable);

        assertEquals(1, result.getTotalElements());
    }
}

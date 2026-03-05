package com.relatech.warehouse_management_system.goodsIn.serviceUnitTest;

import com.relatech.warehouse_management_system.common.util.Category;
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

import com.relatech.warehouse_management_system.product.dto.ProductDto;
import com.relatech.warehouse_management_system.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
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

        when(stateService.checkGrnItemIfCheckedOrPutaway("5L")).thenReturn(true);

        assertThrows(
                CannotAssignCIToGrnItemInClosedOrPutawayStateException.class,
                () -> service.createCheckingInfoAndStockUnit("5L", new StockUnitDto())
        );
    }

    @Test
    void createCheckingInfoAndStockUnit_success() throws Exception {
        // --- GIVEN ---
        String grnItemCode = "ITEM-001";
        String productCode = "PROD-123";
        Long grnItemId = 1L;
        Long stockUnitId = 100L;
        Long checkingInfoId = 200L;

        // 1. Setup Input StockUnitDto
        StockUnitDto inputSu = new StockUnitDto();
        inputSu.setQuantity(10);
        inputSu.setBatchNumber("BATCH-XYZ");
        inputSu.setExpirationDate(LocalDate.now().plusDays(30));

        // 2. Setup GrnItemDto (con tutti i campi necessari per superare i controlli)
        GrnItemDto grnItem = new GrnItemDto();
        grnItem.setId(grnItemId);
        grnItem.setCode(grnItemCode);
        grnItem.setProductCode(productCode);
        grnItem.setReceivedQty(15);
        grnItem.setCheckingInfoList(new ArrayList<>());

        // 3. Setup ProductDto
        ProductDto mockProduct = new ProductDto();
        mockProduct.setCategory(Category.STANDARD);

        // 4. Setup Mock dei risultati dei salvataggi
        StockUnitDto createdSu = new StockUnitDto();
        createdSu.setId(stockUnitId);

        CheckingInfoDto savedCi = new CheckingInfoDto();
        savedCi.setId(checkingInfoId);

        // --- STUBBING (Mockito setup) ---
        when(grnItemService.getGrnItemByCode(grnItemCode)).thenReturn(grnItem);
        when(stateService.checkGrnItemIfCheckedOrPutaway(grnItemCode)).thenReturn(false);
        when(productService.getProductByCode(productCode)).thenReturn(mockProduct);

        when(stockUnitService.createStockUnit(any(StockUnitDto.class))).thenReturn(createdSu);
        when(checkingInfoService.create(any(CheckingInfoDto.class))).thenReturn(savedCi);

        // --- WHEN ---
        GrnItemDto result = service.createCheckingInfoAndStockUnit(grnItemCode, inputSu);

        // --- THEN ---
        assertNotNull(result);

        // Verifichiamo che i flussi logici siano stati rispettati
        verify(productService).getProductByCode(productCode);
        verify(stockUnitService).createStockUnit(argThat(su -> su.getCategory() == Category.STANDARD));

        // Verifica che la CheckingInfo sia stata creata con i dati corretti
        verify(checkingInfoService).create(argThat(ci ->
                ci.getGrnItemId().equals(grnItemId) &&
                        ci.getStockUnitId().equals(stockUnitId) &&
                        ci.getQuantity() == 10
        ));

        verify(grnItemService).addCheckingInfo(grnItemCode, checkingInfoId);
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

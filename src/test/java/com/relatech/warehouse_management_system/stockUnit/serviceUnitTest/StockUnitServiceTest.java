package com.relatech.warehouse_management_system.stockUnit.serviceUnitTest;

import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.product.repository.ProductRepository;
import com.relatech.warehouse_management_system.slot.dto.SlotDTO;
import com.relatech.warehouse_management_system.slot.entity.Slot;
import com.relatech.warehouse_management_system.slot.mapper.SlotMapper;
import com.relatech.warehouse_management_system.slot.repository.SlotRepository;
import com.relatech.warehouse_management_system.slot.service.SlotServiceImpl;
import com.relatech.warehouse_management_system.stockUnit.entity.StockUnit;
import com.relatech.warehouse_management_system.stockUnit.repository.StockUnitRepository;
import com.relatech.warehouse_management_system.stockUnit.service.StockUnitServiceImpl;
import com.relatech.warehouse_management_system.util.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StockUnitServiceTest {

    @Mock
    private SlotRepository slotRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockUnitRepository stockUnitRepository;

    @InjectMocks
    private SlotServiceImpl slotService;

    @InjectMocks
    private StockUnitServiceImpl stockUnitService;

    private Slot slot;
    private StockUnit stockUnit;

    @BeforeEach
    void setUp() {
        stockUnit = StockUnit.builder()
                .batchNumber("LOT20250118")
                .expirationDate(LocalDate.of(2026, 5, 30))
                .productCode("PRD-APPLE-001")
                .uniqueCode("SU-000123456")
                .quantity(50)
                .category(Category.REFRIGERATED)
                .build();

        slot = new Slot();
        slot.setCode("SLOT001");
        slot.setAllowedCategory(Category.REFRIGERATED);
        slot.setCapacity(100);
    }

    @Test
    public void testAssignStockUnitToSlot_success() throws ResourceNotFoundException {
        Long slotId = slot.getId();
        Long stockUnitId = stockUnit.getId();

        when(slotRepository.findById(slotId)).thenReturn(Optional.of(slot));
        when(stockUnitRepository.findById(stockUnitId)).thenReturn(Optional.of(stockUnit));

        stockUnitService.assignStockUnitToSlot(stockUnitId, slotId);

        assertNotNull(slot.getStockUnits());
        assertEquals(1, slot.getStockUnits().size());
        assertEquals(stockUnit, slot.getStockUnits().getFirst());

        verify(slotRepository).findById(slotId);
        verify(stockUnitRepository).findById(stockUnitId);
    }



}

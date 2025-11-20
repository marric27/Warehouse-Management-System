package com.relatech.warehouse_management_system.stockUnit.serviceUnitTest;

import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.product.repository.ProductRepository;
import com.relatech.warehouse_management_system.slot.service.SlotServiceImpl;
import com.relatech.warehouse_management_system.stockUnit.dto.StockUnitDTO;
import com.relatech.warehouse_management_system.stockUnit.entity.StockUnit;
import com.relatech.warehouse_management_system.stockUnit.mapper.StockUnitMapper;
import com.relatech.warehouse_management_system.stockUnit.repository.StockUnitRepository;
import com.relatech.warehouse_management_system.stockUnit.service.StockUnitServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StockUnitServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockUnitRepository stockUnitRepository;

    @InjectMocks
    private StockUnitServiceImpl stockUnitService;

    private StockUnit stockUnit;
    private StockUnitDTO stockUnitDTO;
    private Product product;

    @BeforeEach
    void setUp() {
        stockUnit = StockUnit.builder()
                .id(1L)
                .batchNumber("BN001")
                .expirationDate(LocalDate.now().plusDays(30))
                .productCode("P001")
                .uniqueCode("UNIQUE-001")
                .quantity(10)
                .build();

        stockUnitDTO = StockUnitMapper.toDTO(stockUnit);

        product = new Product();
        product.setId(1L);
        product.setCode("P001");
        product.setName("Test Product");
    }

    @Test
    @DisplayName("Should create a new StockUnit")
    void testCreateStockUnit() {
        when(stockUnitRepository.save(any(StockUnit.class))).thenReturn(stockUnit);

        StockUnitDTO result = stockUnitService.createStockUnit(stockUnitDTO);

        assertThat(result).isNotNull();
        assertThat(result.getUniqueCode()).isEqualTo("UNIQUE-001");
        verify(stockUnitRepository, times(1)).save(any(StockUnit.class));
    }

    @Test
    @DisplayName("Should get StockUnit by ID")
    void testGetStockUnitById() throws ResourceNotFoundException {
        when(stockUnitRepository.findById(1L)).thenReturn(Optional.of(stockUnit));

        StockUnitDTO result = stockUnitService.getStockUnitById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getBatchNumber()).isEqualTo("BN001");
        verify(stockUnitRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException if StockUnit not found")
    void testGetStockUnitById_NotFound() {
        when(stockUnitRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> stockUnitService.getStockUnitById(1L));
        verify(stockUnitRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should assign Product to StockUnit")
    void testAssignProductToStockUnit() throws ResourceNotFoundException {
        when(stockUnitRepository.findById(1L)).thenReturn(Optional.of(stockUnit));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(stockUnitRepository.save(any(StockUnit.class))).thenReturn(stockUnit);

        StockUnitDTO result = stockUnitService.assignProductToStockUnit(1L, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getProductCode()).isEqualTo("P001");
        verify(stockUnitRepository, times(1)).save(any(StockUnit.class));
    }

    @Test
    @DisplayName("Should remove Product from StockUnit")
    void testRemoveProductFromStockUnit() throws ResourceNotFoundException {
        stockUnit.setProduct(product);
        when(stockUnitRepository.findById(1L)).thenReturn(Optional.of(stockUnit));
        when(stockUnitRepository.save(any(StockUnit.class))).thenReturn(stockUnit);

        StockUnitDTO result = stockUnitService.removeProductFromStockUnit(1L);

        assertThat(result.getProductCode()).isEqualTo(stockUnit.getProductCode());
        verify(stockUnitRepository, times(1)).save(any(StockUnit.class));
    }
}
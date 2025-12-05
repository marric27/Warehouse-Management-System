package com.relatech.warehouse_management_system.stockUnit.serviceUnitTest;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.common.util.Category;
import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.product.repository.ProductRepository;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDTO;
import com.relatech.warehouse_management_system.goodsIn.entity.StockUnit;
import com.relatech.warehouse_management_system.goodsIn.entity.mapper.StockUnitMapper;
import com.relatech.warehouse_management_system.goodsIn.entity.repository.StockUnitRepository;
import com.relatech.warehouse_management_system.goodsIn.entity.service.StockUnitServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    @Mock
    private StockUnitMapper stockUnitMapper;

    private StockUnit stockUnit;
    private StockUnitDTO stockUnitDTO;
    private Product product;

    @BeforeEach
    void setUp() {

        product = new Product(1L, "CODE", "NAME", Category.STANDARD);

        stockUnit = StockUnit.builder()
                .id(1L)
                .batchNumber("B123")
                .uniqueCode("U123")
                .quantity(100)
                .category(Category.STANDARD)
                .expirationDate(LocalDate.now().plusDays(10))
                .build();

        stockUnitDTO = StockUnitDTO.builder()
                .id(1L)
                .batchNumber("B123")
                .uniqueCode("U123")
                .quantity(100)
                .category(Category.STANDARD)
                .expirationDate(LocalDate.now().plusDays(10))
                .build();
    }
    @Test
    @DisplayName("Should create a new StockUnit and return DTO")
    void createStockUnit_ShouldReturnDTO() {
        when(stockUnitMapper.toEntity(stockUnitDTO)).thenReturn(stockUnit);
        when(stockUnitRepository.save(stockUnit)).thenReturn(stockUnit);
        when(stockUnitMapper.toDTO(stockUnit)).thenReturn(stockUnitDTO);

        StockUnitDTO result = stockUnitService.createStockUnit(stockUnitDTO);

        assertNotNull(result);
        assertEquals(stockUnitDTO.getId(), result.getId());
        verify(stockUnitRepository, times(1)).save(stockUnit);
    }

    @Test
    @DisplayName("Should return StockUnit DTO by ID")
    void getStockUnitById_ShouldReturnDTO() throws ResourceNotFoundException {
        when(stockUnitRepository.findById(1L)).thenReturn(Optional.of(stockUnit));
        when(stockUnitMapper.toDTO(stockUnit)).thenReturn(stockUnitDTO);

        StockUnitDTO result = stockUnitService.getStockUnitById(1L);

        assertNotNull(result);
        assertEquals(stockUnitDTO.getId(), result.getId());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when StockUnit not found")
    void getStockUnitById_ShouldThrowException() {
        when(stockUnitRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> stockUnitService.getStockUnitById(1L));
    }

    @Test
    @DisplayName("Should return list of all StockUnit DTOs")
    void getAllStockUnits_ShouldReturnList() {
        when(stockUnitRepository.findAll()).thenReturn(List.of(stockUnit));
        when(stockUnitMapper.toDTO(stockUnit)).thenReturn(stockUnitDTO);

        List<StockUnitDTO> result = stockUnitService.getAllStockUnits();

        assertEquals(1, result.size());
        assertEquals(stockUnitDTO.getId(), result.getFirst().getId());
    }

    @Test
    @DisplayName("Should return a paged list of StockUnit DTOs")
    void getAllStockUnitsPaged_ShouldReturnPage() {
        Page<StockUnit> page = new PageImpl<>(List.of(stockUnit));
        when(stockUnitRepository.findAll(Pageable.unpaged())).thenReturn(page);
        when(stockUnitMapper.toDTO(stockUnit)).thenReturn(stockUnitDTO);

        Page<StockUnitDTO> result = stockUnitService.getAllStockUnitsPaged(Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        assertEquals(stockUnitDTO.getId(), result.getContent().getFirst().getId());
    }

    @Test
    @DisplayName("Should update StockUnit and return updated DTO")
    void updateStockUnit_ShouldReturnUpdatedDTO() throws ResourceNotFoundException {
        StockUnitDTO updatedDTO = StockUnitDTO.builder()
                .batchNumber("B456")
                .uniqueCode("U456")
                .quantity(200)
                .category(Category.STANDARD)
                .expirationDate(LocalDate.now().plusDays(20))
                .build();

        when(stockUnitRepository.findById(1L)).thenReturn(Optional.of(stockUnit));
        when(stockUnitRepository.save(stockUnit)).thenReturn(stockUnit);
        when(stockUnitMapper.toDTO(stockUnit)).thenReturn(updatedDTO);

        StockUnitDTO result = stockUnitService.updateStockUnit(1L, updatedDTO);

        assertEquals(updatedDTO.getBatchNumber(), result.getBatchNumber());
        assertEquals(updatedDTO.getQuantity(), result.getQuantity());
    }

    @Test
    @DisplayName("Should delete StockUnit by ID")
    void deleteStockUnit_ShouldCallDelete() throws ResourceNotFoundException {
        when(stockUnitRepository.findById(1L)).thenReturn(Optional.of(stockUnit));

        stockUnitService.deleteStockUnit(1L);

        verify(stockUnitRepository, times(1)).delete(stockUnit);
    }

    @Test
    @DisplayName("Should assign Product to StockUnit and return DTO")
    void assignProductToStockUnit_ShouldReturnDTO() throws ResourceNotFoundException {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(stockUnitRepository.findById(1L)).thenReturn(Optional.of(stockUnit));
        when(stockUnitRepository.save(stockUnit)).thenReturn(stockUnit);
        when(stockUnitMapper.toDTO(stockUnit)).thenReturn(stockUnitDTO);

        StockUnitDTO result = stockUnitService.assignProductToStockUnit(1L, 1L);

        assertNotNull(result);
        verify(stockUnitRepository).save(stockUnit);
    }

    @Test
    @DisplayName("Should remove Product from StockUnit and return DTO")
    void removeProductFromStockUnit_ShouldReturnDTO() throws ResourceNotFoundException {
        stockUnit.setProduct(product);

        when(stockUnitRepository.findById(1L)).thenReturn(Optional.of(stockUnit));
        when(stockUnitRepository.save(stockUnit)).thenReturn(stockUnit);
        when(stockUnitMapper.toDTO(stockUnit)).thenReturn(stockUnitDTO);

        StockUnitDTO result = stockUnitService.removeProductFromStockUnit(1L);

        assertNotNull(result);
        assertNull(stockUnit.getProduct());
        verify(stockUnitRepository).save(stockUnit);
    }
}
package com.relatech.warehouse_management_system.slot.serviceUnitTest;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDto;
import com.relatech.warehouse_management_system.goodsIn.entity.mapper.StockUnitMapper;
import com.relatech.warehouse_management_system.goodsIn.exception.UpdateEntityException;
import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.goodsIn.entity.StockUnit;
import com.relatech.warehouse_management_system.goodsIn.entity.repository.StockUnitRepository;
import com.relatech.warehouse_management_system.common.util.Category;
import com.relatech.warehouse_management_system.product.repository.ProductRepository;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDto;
import com.relatech.warehouse_management_system.warehouse.entity.Slot;
import com.relatech.warehouse_management_system.warehouse.entity.SlotMapper;
import com.relatech.warehouse_management_system.warehouse.entity.SlotRepository;
import com.relatech.warehouse_management_system.warehouse.service.SlotServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlotServiceTest {

    @Mock
    private SlotRepository slotRepository;

    @Mock
    private StockUnitRepository stockUnitRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SlotMapper slotMapper;

    @Mock
    private StockUnitMapper stockUnitMapper;

    @InjectMocks
    private SlotServiceImpl slotService;

    private Slot slot;
    private SlotDto slotDTO;
    private StockUnit stockUnit;
    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product(1L, "Test Product", null, Category.STANDARD);

        stockUnit = StockUnit.builder().id(1L).build();
        StockUnitDto stockUnitDTO = StockUnitDto.builder().id(1L).build();

        slot = new Slot();
        slot.setId(1L);
        slot.setCode("SLOT-01");
        slot.setCapacity(10);
        slot.setAllowedCategory(Category.STANDARD);
        slot.setStockUnits(new ArrayList<>());

        slotDTO = new SlotDto();
        slotDTO.setId(1L);
        slotDTO.setCode("SLOT-01");
        slotDTO.setCapacity(10);
        slotDTO.setCategory(Category.STANDARD);
        slotDTO.setStockUnits(new ArrayList<>());
    }

    @Test
    @DisplayName("Should return all slots as DTOs")
    void getAllSlots_ShouldReturnList() {
        when(slotRepository.findAll()).thenReturn(List.of(slot));
        when(slotMapper.toDto(slot)).thenReturn(slotDTO);

        List<SlotDto> result = slotService.getAllSlots();

        assertEquals(1, result.size());
        verify(slotRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return slot DTO by ID")
    void getSlotById_ShouldReturnDTO() throws ResourceNotFoundException {
        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));
        when(slotMapper.toDto(slot)).thenReturn(slotDTO);

        SlotDto result = slotService.getSlotById(1L);

        assertNotNull(result);
        assertEquals(slotDTO.getId(), result.getId());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when slot not found")
    void getSlotById_ShouldThrowException() {
        when(slotRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> slotService.getSlotById(1L));
    }

    @Test
    @DisplayName("Should create a new slot and return DTO")
    void createSlot_ShouldReturnDTO() {
        when(slotMapper.toEntity(slotDTO)).thenReturn(slot);
        when(slotRepository.save(slot)).thenReturn(slot);
        when(slotMapper.toDto(slot)).thenReturn(slotDTO);

        SlotDto result = slotService.createSlot(slotDTO);

        assertNotNull(result);
        assertEquals(slotDTO.getId(), result.getId());
        verify(slotRepository, times(1)).save(slot);
    }

    @Test
    @DisplayName("Should update slot and return updated DTO")
    void updateSlot_ShouldReturnUpdatedDTO() throws ResourceNotFoundException, UpdateEntityException {
        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));
        when(slotRepository.save(slot)).thenReturn(slot);
        when(slotMapper.toDto(slot)).thenReturn(slotDTO);

        SlotDto result = slotService.updateSlot(1L, slotDTO);

        assertEquals(slotDTO.getCode(), result.getCode());
        assertEquals(slotDTO.getCapacity(), result.getCapacity());
    }

    @Test
    @DisplayName("Should throw UpdateEntityException when category mismatch")
    void updateSlot_ShouldThrowUpdateEntityException() {
        slot.setProd(product);
        slotDTO.setCategory(Category.FLAMMABLE);

        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));

        assertThrows(UpdateEntityException.class, () -> slotService.updateSlot(1L, slotDTO));
    }

    @Test
    @DisplayName("Should delete slot successfully")
    void deleteSlot_ShouldDelete() throws ResourceNotFoundException {
        slot.setStockUnits(new ArrayList<>());
        slot.setProd(null);
        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));

        slotService.deleteSlot(1L);

        verify(slotRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when slot contains product")
    void deleteSlot_ShouldThrowException_ProductExists() {
        slot.setProd(product);
        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));

        assertThrows(IllegalStateException.class, () -> slotService.deleteSlot(1L));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when slot contains stock units")
    void deleteSlot_ShouldThrowException_StockUnitsExist() {
        slot.getStockUnits().add(stockUnit);
        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));

        assertThrows(IllegalStateException.class, () -> slotService.deleteSlot(1L));
    }

    @Test
    void updateSlot_ok_whenNoProductPresent() throws Exception {
        Long id = 1L;

        SlotDto dto = new SlotDto();
        dto.setCapacity(20);
        dto.setCategory(Category.STANDARD);

        Slot existing = new Slot();
        existing.setId(id);
        existing.setCapacity(10);
        existing.setProd(null);  // Nessun prodotto presente

        when(slotRepository.findById(id)).thenReturn(Optional.of(existing));
        when(slotRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(slotMapper.toDto(any())).thenReturn(dto);

        SlotDto result = slotService.updateSlot(id, dto);

        assertEquals(20, result.getCapacity());
        assertEquals(Category.STANDARD, result.getCategory());
    }

    // ------------------------------
    // 2. UPDATE OK — Categoria coerente col prodotto
    // ------------------------------
    @Test
    void updateSlot_ok_whenCategoryMatchesProduct() throws Exception {
        Long id = 1L;

        SlotDto dto = new SlotDto();
        dto.setCategory(Category.STANDARD);

        Product prod = new Product();
        prod.setCategory(Category.STANDARD);

        Slot existing = new Slot();
        existing.setId(id);
        existing.setProd(prod);

        when(slotRepository.findById(id)).thenReturn(Optional.of(existing));
        when(slotRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(slotMapper.toDto(any())).thenReturn(dto);

        SlotDto result = slotService.updateSlot(id, dto);

        assertEquals(Category.STANDARD, result.getCategory());
    }

    // ------------------------------
    // 3. UPDATE FAIL — Categoria non coerente con prodotto
    // ------------------------------
    @Test
    void updateSlot_fails_whenCategoryDifferentFromProduct() {
        Long id = 1L;

        SlotDto dto = new SlotDto();
        dto.setCategory(Category.STANDARD);

        Product prod = new Product();
        prod.setCategory(Category.FLAMMABLE);

        Slot existing = new Slot();
        existing.setId(id);
        existing.setProd(prod);

        when(slotRepository.findById(id)).thenReturn(Optional.of(existing));

        assertThrows(UpdateEntityException.class,
                () -> slotService.updateSlot(id, dto));
    }

    // ------------------------------
    // 4. UPDATE FAIL — Slot non trovato
    // ------------------------------
    @Test
    void updateSlot_fails_whenSlotNotFound() {
        Long id = 1L;
        SlotDto dto = new SlotDto();

        when(slotRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> slotService.updateSlot(id, dto));
    }

    // ------------------------------
    // 5. STOCK UNITS — Aggiunta senza duplicati
    // ------------------------------
    @Test
    void updateSlot_addsStockUnits_withoutDuplicates() throws Exception {

        Long id = 1L;

        // DTO con 1 stockUnit esistente
        StockUnitDto suDto = new StockUnitDto();
        suDto.setId(10L);

        SlotDto dto = new SlotDto();
        dto.setStockUnits(List.of(suDto));

        // Slot esistente con lista vuota
        Slot slot = new Slot();
        slot.setId(id);
        slot.setStockUnits(new ArrayList<>());

        StockUnit su = new StockUnit();
        su.setId(10L);

        when(slotRepository.findById(id)).thenReturn(Optional.of(slot));
        when(stockUnitRepository.findById(10L)).thenReturn(Optional.of(su));
        when(slotRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(slotMapper.toDto(any())).thenReturn(dto);

        SlotDto result = slotService.updateSlot(id, dto);

        assertEquals(1, slot.getStockUnits().size());
        assertEquals(10L, slot.getStockUnits().getFirst().getId());
        assertSame(slot, slot.getStockUnits().getFirst().getSlot());

        // Ripeto update → NON deve raddoppiare
        slotService.updateSlot(id, dto);

        assertEquals(1, slot.getStockUnits().size());
    }

    // ------------------------------
    // 6. STOCK UNIT non trovata -> errore
    // ------------------------------
    @Test
    void updateSlot_fails_whenStockUnitNotFound() {

        Long id = 1L;

        StockUnitDto suDto = new StockUnitDto();
        suDto.setId(99L);

        SlotDto dto = new SlotDto();
        dto.setStockUnits(List.of(suDto));

        Slot slot = new Slot();
        slot.setId(id);
        slot.setStockUnits(new ArrayList<>());

        when(slotRepository.findById(id)).thenReturn(Optional.of(slot));
        when(stockUnitRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> slotService.updateSlot(id, dto));
    }
}
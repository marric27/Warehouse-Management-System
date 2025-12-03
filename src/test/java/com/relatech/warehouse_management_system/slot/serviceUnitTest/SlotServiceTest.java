package com.relatech.warehouse_management_system.slot.serviceUnitTest;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.goodsIn.entity.StockUnit;
import com.relatech.warehouse_management_system.goodsIn.entity.repository.StockUnitRepository;
import com.relatech.warehouse_management_system.common.util.Category;
import com.relatech.warehouse_management_system.goodsIn.dto.SlotDTO;
import com.relatech.warehouse_management_system.goodsIn.entity.Slot;
import com.relatech.warehouse_management_system.goodsIn.entity.mapper.SlotMapper;
import com.relatech.warehouse_management_system.goodsIn.entity.repository.SlotRepository;
import com.relatech.warehouse_management_system.goodsIn.entity.service.SlotServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlotServiceTest {

    @Mock
    private SlotRepository slotRepository;

    @Mock
    private StockUnitRepository stockUnitRepository;

    @InjectMocks
    private SlotServiceImpl slotService;

    private Slot slot;
    private SlotDTO slotDTO;

    @BeforeEach
    void setUp() {
        Product product = new Product();
        product.setCode("P001");
        product.setName("Paracetamolo");
        product.setCategory(Category.STANDARD);

        slot = new Slot();
        slot.setCode("SLOT001");
        slot.setAllowedCategory(Category.STANDARD);
        slot.setCapacity(100);
        slot.setProd(product);

        slotDTO = SlotMapper.toDto(slot);
    }

    @Test
    @DisplayName("Given slots exist, when getAllSlots, then return list of SlotDTO")
    void givenSlotsExist_whenGetAllSlots_thenReturnList() {
        when(slotRepository.findAll()).thenReturn(List.of(slot));

        List<SlotDTO> result = slotService.getAllSlots();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCode()).isEqualTo("SLOT001");
        verify(slotRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Given slot exists, when getSlotById, then return SlotDTO")
    void givenSlotExists_whenGetSlotById_thenReturnSlotDTO() throws ResourceNotFoundException {
        when(slotRepository.findById(slot.getId())).thenReturn(Optional.of(slot));

        SlotDTO result = slotService.getSlotById(slot.getId());

        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("SLOT001");
        verify(slotRepository, times(1)).findById(slot.getId());
    }

    @Test
    @DisplayName("Given slot does not exist, when getSlotById, then throw ResourceNotFoundException")
    void givenSlotDoesNotExist_whenGetSlotById_thenThrowException() {
        when(slotRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> slotService.getSlotById(99L));

        verify(slotRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Given valid SlotDTO, when createSlot, then save and return SlotDTO")
    void givenValidSlotDTO_whenCreateSlot_thenReturnSavedSlotDTO() {
        when(slotRepository.save(any(Slot.class))).thenReturn(slot);

        SlotDTO result = slotService.createSlot(slotDTO);

        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("SLOT001");
        verify(slotRepository, times(1)).save(any(Slot.class));
    }

    @Test
    @DisplayName("Given existing slot, when updateSlot with same category, then update fields and return SlotDTO")
    void givenExistingSlot_whenUpdateSlot_thenReturnUpdatedDTO() throws Exception {
        SlotDTO updatedDTO = new SlotDTO(1L, "SLOT002", Category.STANDARD, 200, null, null);
        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));
        when(slotRepository.save(any(Slot.class))).thenReturn(SlotMapper.toEntity(updatedDTO));

        SlotDTO result = slotService.updateSlot(1L, updatedDTO);

        assertThat(result.getCode()).isEqualTo("SLOT002");
        verify(slotRepository, times(1)).save(any(Slot.class));
    }

    @Test
    @DisplayName("Given slot contains product, when updateSlot with different category, then throw Exception")
    void givenSlotContainsProduct_whenUpdateSlotWithDifferentCategory_thenThrowException() {
        SlotDTO updatedDTO = new SlotDTO(1L, "SLOT002", Category.FLAMMABLE, 200, null, null);
        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));

        assertThrows(Exception.class, () -> slotService.updateSlot(1L, updatedDTO));

        verify(slotRepository, never()).save(any());
    }

    @Test
    @DisplayName("Given slot exists and is empty, when deleteSlot, then deleteById is called")
    void givenEmptySlot_whenDeleteSlot_thenDeleteByIdCalled() throws ResourceNotFoundException {
        slot.setProd(null);
        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));

        slotService.deleteSlot(1L);

        verify(slotRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Given slot contains product, when deleteSlot, then throw IllegalStateException")
    void givenSlotContainsProduct_whenDeleteSlot_thenThrowIllegalStateException() {
        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));

        assertThrows(IllegalStateException.class, () -> slotService.deleteSlot(1L));

        verify(slotRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Given slot does not exist, when deleteSlot, then throw ResourceNotFoundException")
    void givenSlotDoesNotExist_whenDeleteSlot_thenThrowResourceNotFoundException() {
        when(slotRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> slotService.deleteSlot(99L));

        verify(slotRepository, never()).deleteById(anyLong());
    }

    @Test
    void testAssignStockUnitToSlot_Success() throws ResourceNotFoundException {
        Long slotId = 1L;
        Long stockUnitId = 1L;

        StockUnit stockUnit = new StockUnit();
        stockUnit.setId(stockUnitId);
        stockUnit.setCategory(Category.STANDARD);

        when(slotRepository.findById(slotId)).thenReturn(Optional.of(slot));
        when(stockUnitRepository.findById(stockUnitId)).thenReturn(Optional.of(stockUnit));
        when(slotRepository.save(any(Slot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SlotDTO result = slotService.assignStockUnitToSlot(slotId, stockUnitId);

        assertNotNull(result);
        assertTrue(result.getStockUnits().stream().anyMatch(su -> su.getId().equals(stockUnitId)));

        verify(slotRepository).findById(slotId);
        verify(stockUnitRepository).findById(stockUnitId);
        verify(slotRepository).save(slot);
    }

    @Test
    void testAssignStockUnitToSlot_SlotNotFound() {
        Long slotId = 1L;
        Long stockUnitId = 1L;

        when(slotRepository.findById(slotId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> slotService.assignStockUnitToSlot(slotId, stockUnitId));

        assertEquals("Slot not found with id: " + slotId, exception.getMessage());
    }

    @Test
    void testAssignStockUnitToSlot_StockUnitNotFound() {
        Long slotId = 1L;
        Long stockUnitId = 1L;

        Slot slot = new Slot();
        slot.setId(slotId);

        when(slotRepository.findById(slotId)).thenReturn(Optional.of(slot));
        when(stockUnitRepository.findById(stockUnitId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> slotService.assignStockUnitToSlot(slotId, stockUnitId));

        assertEquals("Stock Unit not found with id: " + stockUnitId, exception.getMessage());
    }

    @Test
    void testAssignStockUnitToSlot_CategoryMismatch() {
        Long slotId = 1L;
        Long stockUnitId = 1L;

        StockUnit stockUnit = new StockUnit();
        stockUnit.setId(stockUnitId);
        stockUnit.setCategory(Category.FLAMMABLE);

        when(slotRepository.findById(slotId)).thenReturn(Optional.of(slot));
        when(stockUnitRepository.findById(stockUnitId)).thenReturn(Optional.of(stockUnit));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> slotService.assignStockUnitToSlot(slotId, stockUnitId));

        assertEquals("StockUnit category not allowed in this Slot", exception.getMessage());
    }

    @Test
    void testRemoveStockUnitFromSlot_Success() throws ResourceNotFoundException {
        Long slotId = 1L;
        Long stockUnitId = 1L;

        StockUnit stockUnit = new StockUnit();
        stockUnit.setId(stockUnitId);

        List<StockUnit> stockUnits = new ArrayList<>();
        stockUnits.add(stockUnit);

        Slot slot = new Slot();
        slot.setId(slotId);
        slot.setStockUnits(stockUnits);

        when(slotRepository.findById(slotId)).thenReturn(Optional.of(slot));
        when(slotRepository.save(any(Slot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SlotDTO result = slotService.removeStockUnitFromSlot(slotId, stockUnitId);

        assertNotNull(result);
        assertTrue(result.getStockUnits().isEmpty(), "Lo stock unit dovrebbe essere rimosso");
        assertNull(stockUnit.getSlot(), "Lo slot dello stock unit dovrebbe essere null");

        verify(slotRepository).findById(slotId);
        verify(slotRepository).save(slot);
    }

    @Test
    void testRemoveStockUnitFromSlot_SlotNotFound() {
        Long slotId = 1L;
        Long stockUnitId = 1L;

        when(slotRepository.findById(slotId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> slotService.removeStockUnitFromSlot(slotId, stockUnitId));

        assertEquals("Slot not found with id: " + slotId, exception.getMessage());
    }

    @Test
    void testRemoveStockUnitFromSlot_StockUnitNotFound() {
        Long slotId = 1L;
        Long stockUnitId = 1L;

        Slot slot = new Slot();
        slot.setId(slotId);
        slot.setStockUnits(new ArrayList<>()); // empty

        when(slotRepository.findById(slotId)).thenReturn(Optional.of(slot));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> slotService.removeStockUnitFromSlot(slotId, stockUnitId));

        assertEquals("StockUnit not found with id: " + stockUnitId, exception.getMessage());
    }

    @Test
    @DisplayName("Given StockUnit already assigned to another Slot, when assign, then throw IllegalArgumentException")
    void givenStockUnitAlreadyAssignedToAnotherSlot_whenAssign_thenThrowException() {
        Long slotId = 1L;
        Long stockUnitId = 1L;

        // Target slot
        Slot targetSlot = new Slot();
        targetSlot.setId(slotId);
        targetSlot.setAllowedCategory(Category.STANDARD);

        // other slot with stock unit already assigned
        Slot otherSlot = new Slot();
        otherSlot.setId(99L);
        otherSlot.setAllowedCategory(Category.STANDARD);

        StockUnit stockUnit = new StockUnit();
        stockUnit.setId(stockUnitId);
        stockUnit.setCategory(Category.STANDARD);
        stockUnit.setSlot(otherSlot); // already assigned to another slot

        when(slotRepository.findById(slotId)).thenReturn(Optional.of(targetSlot));
        when(stockUnitRepository.findById(stockUnitId)).thenReturn(Optional.of(stockUnit));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> slotService.assignStockUnitToSlot(slotId, stockUnitId)
        );

        assertEquals("StockUnit already assigned to another Slot", ex.getMessage());

        verify(slotRepository).findById(slotId);
        verify(stockUnitRepository).findById(stockUnitId);
        verify(slotRepository, never()).save(any());
    }


}
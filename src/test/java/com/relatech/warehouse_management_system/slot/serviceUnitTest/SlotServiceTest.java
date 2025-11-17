package com.relatech.warehouse_management_system.slot.serviceUnitTest;

import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.product.repository.ProductRepository;
import com.relatech.warehouse_management_system.util.Category;import com.relatech.warehouse_management_system.slot.dto.SlotDTO;
import com.relatech.warehouse_management_system.slot.entity.Slot;
import com.relatech.warehouse_management_system.slot.mapper.SlotMapper;
import com.relatech.warehouse_management_system.slot.repository.SlotRepository;
import com.relatech.warehouse_management_system.slot.service.SlotServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlotServiceTest {

    @Mock
    private SlotRepository slotRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private SlotServiceImpl slotService;

    private Slot slot;
    private SlotDTO slotDTO;
    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setCode("P001");
        product.setName("Paracetamolo");
        product.setCategory(Category.STANDARD);
        product.setNationalCode("IT001");

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
        SlotDTO updatedDTO = new SlotDTO(1L, "SLOT002", Category.STANDARD, 200, null);
        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));
        when(slotRepository.save(any(Slot.class))).thenReturn(SlotMapper.toEntity(updatedDTO));

        SlotDTO result = slotService.updateSlot(1L, updatedDTO);

        assertThat(result.getCode()).isEqualTo("SLOT002");
        verify(slotRepository, times(1)).save(any(Slot.class));
    }

    @Test
    @DisplayName("Given slot contains product, when updateSlot with different category, then throw Exception")
    void givenSlotContainsProduct_whenUpdateSlotWithDifferentCategory_thenThrowException() {
        SlotDTO updatedDTO = new SlotDTO(1L, "SLOT002", Category.FLAMMABLE, 200, null);
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
}

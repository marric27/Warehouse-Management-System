package com.relatech.warehouse_management_system.logistic.serviceUnitTest;


import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.logistic.service.LogisticServiceImpl;
import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.product.repository.ProductRepository;
import com.relatech.warehouse_management_system.slot.dto.SlotDTO;
import com.relatech.warehouse_management_system.slot.entity.Slot;
import com.relatech.warehouse_management_system.slot.repository.SlotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogisticServiceTest {

    @Mock
    SlotRepository slotRepository;

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    LogisticServiceImpl logisticService;

    @Test
    void assignProductToSlot_ShouldAssignProduct() throws Exception {
        Slot slot = new Slot();
        slot.setId(1L);
        slot.setProd(null);

        Product product = new Product();
        product.setId(10L);

        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(slotRepository.save(any(Slot.class))).thenReturn(slot);

        SlotDTO result = logisticService.assignProductToSlot(1L, 10L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(slotRepository).save(slot);
    }

    @Test
    void assignProductToSlot_ShouldThrow_WhenSlotNotFound() {
        when(slotRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> logisticService.assignProductToSlot(1L, 10L));
    }

    @Test
    void assignProductToSlot_ShouldThrow_WhenProductNotFound() {
        Slot slot = new Slot();
        slot.setId(1L);

        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));
        when(productRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> logisticService.assignProductToSlot(1L, 10L));
    }

    @Test
    void removeProductFromSlot_ShouldRemoveProduct() throws Exception {
        Slot slot = new Slot();
        slot.setId(1L);
        slot.setProd(new Product());

        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));
        when(slotRepository.save(any(Slot.class))).thenReturn(slot);

        SlotDTO result = logisticService.removeProductFromSlot(1L);

        assertNotNull(result);
        assertNull(slot.getProd());
        verify(slotRepository).save(slot);
    }

    @Test
    void removeProductFromSlot_ShouldThrow_WhenSlotNotFound() {
        when(slotRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> logisticService.removeProductFromSlot(1L));
    }

    @Test
    void canSlotContainProduct_ShouldReturnTrue() throws Exception {
        Slot slot = mock(Slot.class);
        Product product = new Product();

        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(slot.canContain(product)).thenReturn(true);

        boolean result = logisticService.canSlotContainProduct(1L, 10L);

        assertTrue(result);
    }

    @Test
    void canSlotContainProduct_ShouldThrow_WhenSlotNotFound() {
        when(slotRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> logisticService.canSlotContainProduct(1L, 10L));
    }

    @Test
    void canSlotContainProduct_ShouldThrow_WhenProductNotFound() {
        Slot slot = new Slot();

        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));
        when(productRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> logisticService.canSlotContainProduct(1L, 10L));
    }
}
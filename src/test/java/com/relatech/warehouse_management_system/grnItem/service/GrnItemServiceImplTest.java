package com.relatech.warehouse_management_system.grnItem.service;

import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.entity.GrnItem;
import com.relatech.warehouse_management_system.goodsIn.entity.mapper.GrnItemMapper;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnItemServiceImpl;
import com.relatech.warehouse_management_system.goodsIn.entity.repository.GrnItemRepository;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnItemNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("GrnItemServiceImpl - Extended CRUD and Business Logic")
class GrnItemServiceImplTest {

    @Mock
    private GrnItemRepository grnItemRepository;

    @Mock
    private GrnItemMapper grnItemMapper;

    @InjectMocks
    private GrnItemServiceImpl grnItemService;

    private GrnItemDto createValidDto() {
        return GrnItemDto.builder()
                .id(1L)
                .code("ITEM-001")
                .productCode("P001")
                .expectedQty(10)
                .build();
    }

    private GrnItem createValidEntity() {
        return GrnItem.builder()
                .id(1L)
                .code("ITEM-001")
                .productCode("P001")
                .expectedQty(10)
                .build();
    }

    @Test
    void givenValidGrnItemDto_whenCreateGrnItem_thenReturnsSavedDto() {
        GrnItemDto grnItemDto = createValidDto();
        GrnItem grnItem = createValidEntity();

        when(grnItemMapper.toEntity(grnItemDto)).thenReturn(grnItem);
        when(grnItemMapper.toDto(grnItem)).thenReturn(grnItemDto);

        GrnItemDto result = grnItemService.createGrnItem(grnItemDto);

        assertNotNull(result);
        assertEquals("ITEM-001", result.getCode());
        verify(grnItemMapper).toEntity(grnItemDto);
        verify(grnItemMapper).toDto(grnItem);
    }

    @Test
    void givenExistingGrnItemId_whenGetGrnItemById_thenReturnsDto() throws GrnItemNotFoundException {
        GrnItem grnItem = createValidEntity();
        GrnItemDto grnItemDto = createValidDto();

        when(grnItemRepository.findById(1L)).thenReturn(Optional.of(grnItem));
        when(grnItemMapper.toDto(grnItem)).thenReturn(grnItemDto);

        GrnItemDto result = grnItemService.getGrnItemById(1L);

        assertNotNull(result);
        assertEquals("ITEM-001", result.getCode());
    }

    @Test
    void givenNonExistingGrnItemId_whenGetGrnItemById_thenThrowsNotFoundException() {
        when(grnItemRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(GrnItemNotFoundException.class,
                () -> grnItemService.getGrnItemById(2L));
    }

    @Test
    void givenValidUpdateDto_whenUpdateGrnItem_thenUpdatesFieldsAndReturnsDto() throws GrnItemNotFoundException {
        GrnItemDto updateDto = GrnItemDto.builder()
                .productCode("P002")
                .expectedQty(20)
                .build();
        GrnItem grnItem = createValidEntity();
        GrnItemDto grnItemDto = createValidDto();

        when(grnItemRepository.findById(1L)).thenReturn(Optional.of(grnItem));
        when(grnItemRepository.save(grnItem)).thenReturn(grnItem);
        when(grnItemMapper.toDto(grnItem)).thenReturn(grnItemDto);

        GrnItemDto result = grnItemService.updateGrnItem(1L, updateDto);

        assertNotNull(result);
        assertEquals("P002", grnItem.getProductCode());
        assertEquals(20, grnItem.getExpectedQty());
        verify(grnItemRepository).save(grnItem);
    }

    @Test
    void givenNonExistingId_whenUpdateGrnItem_thenThrowsNotFoundException() {
        GrnItemDto updateDto = GrnItemDto.builder().productCode("P002").build();
        when(grnItemRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(GrnItemNotFoundException.class,
                () -> grnItemService.updateGrnItem(2L, updateDto));
    }

    @Test
    void givenExistingGrnItemId_whenDeleteGrnItem_thenRepositoryDeleteCalled() throws  GrnItemNotFoundException {
        GrnItem grnItem = createValidEntity();
        when(grnItemRepository.findById(1L)).thenReturn(Optional.of(grnItem));

        grnItemService.deleteGrnItem(1L);

        verify(grnItemRepository).deleteById(1L);
    }

    @Test
    void givenNonExistingGrnItemId_whenDeleteGrnItem_thenThrowsNotFoundException() {
        when(grnItemRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(GrnItemNotFoundException.class,
                () -> grnItemService.deleteGrnItem(2L));
    }
}

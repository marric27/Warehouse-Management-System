package com.relatech.warehouse_management_system.grnItem.service;

import com.relatech.warehouse_management_system.checkingInfo.entity.CheckingInfo;
import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.grnItem.dto.GrnItemDto;
import com.relatech.warehouse_management_system.grnItem.entity.GrnItem;
import com.relatech.warehouse_management_system.grnItem.mapper.GrnItemMapper;
import com.relatech.warehouse_management_system.grnItem.repository.GrnItemRepository;
import com.relatech.warehouse_management_system.util.State;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GrnItemServiceImpl - Extended CRUD and Business Logic")
class GrnItemServiceImplTest {
    @Mock
    private GrnItemRepository grnItemRepository;

    @InjectMocks
    private GrnItemServiceImpl grnItemService;

    private GrnItemDto createValidDto() {
        GrnItemDto dto = new GrnItemDto();
        dto.setProductCode("P001");
        dto.setExpectedQty(100);
        dto.setCompliantQty(80);
        dto.setNotCompliantQty(20);
        dto.setReceivedQty(100);
        dto.setState(State.OPEN);
        dto.setCheckingInfoList(null);
        return dto;
    }

    @Test
    @DisplayName("givenValidGrnItemDto_whenCreateGrnItem_thenReturnsSavedDto")
    void givenValidGrnItemDto_whenCreateGrnItem_thenReturnsSavedDto() {
        GrnItemDto dto = createValidDto();
        GrnItem entity = GrnItemMapper.toEntity(dto);

        when(grnItemRepository.save(any(GrnItem.class)))
                .thenReturn(entity);

        GrnItemDto result = grnItemService.createGrnItem(dto);

        assertThat(result).isNotNull();
        assertThat(result.getReceivedQty()).isEqualTo(100);
    }

    @Test
    @DisplayName("givenInvalidReceivedQty_whenCreateGrnItem_thenThrowsIllegalArgumentException")
    void givenInvalidReceivedQty_whenCreateGrnItem_thenThrowsIllegalArgumentException() {
        GrnItemDto dto = createValidDto();
        dto.setReceivedQty(50);

        assertThatThrownBy(() -> grnItemService.createGrnItem(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("receivedQty deve essere uguale");
    }

    @Test
    @DisplayName("givenCheckingInfoAllPutaway_whenUpdateGrnItem_thenStateBecomesPutaway")
    void givenCheckingInfoAllPutaway_whenUpdateGrnItem_thenStateBecomesPutaway() throws Exception {
        GrnItem existing = GrnItemMapper.toEntity(createValidDto());
        existing.setId(1L);

        CheckingInfo ci1 = new CheckingInfo();
        ci1.setState(State.PUTAWAY);

        CheckingInfo ci2 = new CheckingInfo();
        ci2.setState(State.PUTAWAY);

        existing.setCheckingInfoList(List.of(ci1, ci2));

        GrnItemDto updateDto = createValidDto();
        updateDto.setCheckingInfoList(List.of(ci1, ci2));

        when(grnItemRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        when(grnItemRepository.save(any(GrnItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        GrnItemDto result = grnItemService.updateGrnItem(1L, updateDto);

        assertThat(result.getState()).isEqualTo(State.PUTAWAY);
    }

    @Test
    @DisplayName("givenGrnItemDoesNotExist_whenGetById_thenThrowsResourceNotFoundException")
    void givenGrnItemDoesNotExist_whenGetById_thenThrowsResourceNotFoundException() {
        when(grnItemRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> grnItemService.getGrnItemById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("givenGrnItemExists_whenDeleteGrnItem_thenRepositoryDeleteIsCalled")
    void givenGrnItemExists_whenDeleteGrnItem_thenRepositoryDeleteIsCalled() throws Exception {
        GrnItem existing = GrnItemMapper.toEntity(createValidDto());
        existing.setId(1L);

        when(grnItemRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        grnItemService.deleteGrnItem(1L);

        verify(grnItemRepository).deleteById(1L);
    }
}
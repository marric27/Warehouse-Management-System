package com.relatech.warehouse_management_system.outbound.entity.service;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.outbound.dto.PickListItemDto;
import com.relatech.warehouse_management_system.outbound.entity.PickListItem;
import com.relatech.warehouse_management_system.outbound.entity.mapper.PickListMapper;
import com.relatech.warehouse_management_system.outbound.entity.repository.PickListItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PickListItemService {
    private final PickListItemRepository pickListItemRepository;


    @Transactional(rollbackFor = ResourceNotFoundException.class)
    public PickListItemDto update(String pickListItemCode, PickListItemDto pickListItemDto) throws ResourceNotFoundException {
        PickListItem existing = pickListItemRepository.findByCode(pickListItemCode)
                .orElseThrow(() -> new ResourceNotFoundException("PickListItem", pickListItemCode));

        existing.setPickedQty(pickListItemDto.getPickedQty());
        existing.setState(pickListItemDto.getState());
        existing.setErrorReason(pickListItemDto.getErrorReason());

        PickListItem saved = pickListItemRepository.save(existing);
        return PickListMapper.toItemDto(saved);
    }

}

package com.relatech.warehouse_management_system.outbound.entity.service;

import com.relatech.warehouse_management_system.outbound.dto.PickListDto;
import com.relatech.warehouse_management_system.outbound.entity.PickList;
import com.relatech.warehouse_management_system.outbound.entity.mapper.PickListMapper;
import com.relatech.warehouse_management_system.outbound.entity.repository.PickListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PickListService {
    private final PickListRepository pickListRepository;

    public PickListDto create(PickListDto pickListDto) {
        PickList pickList = PickListMapper.toEntity(pickListDto);
        return PickListMapper.toDto(pickListRepository.save(pickList));
    }



}

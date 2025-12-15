package com.relatech.warehouse_management_system.outbound.entity.service;

import com.relatech.warehouse_management_system.outbound.dto.PickListDto;
import com.relatech.warehouse_management_system.outbound.entity.PickList;
import com.relatech.warehouse_management_system.outbound.entity.mapper.PickListMapper;
import com.relatech.warehouse_management_system.outbound.entity.repository.PickListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PickListService {
    private final PickListRepository pickListRepository;

    public PickListDto create(PickListDto pickListDto) {
        PickList pickList = PickListMapper.toEntity(pickListDto);
        return PickListMapper.toDto(pickListRepository.save(pickList));
    }

    public List<PickListDto> getAll() {
        return pickListRepository.findAll()
                .stream().map(PickListMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Page<PickListDto> getAllPickListPaged(Pageable pageable) {
        Page<PickList> pickListPage = pickListRepository.findAll(pageable);
        return pickListPage.map(PickListMapper::toDto);
    }


}

package com.relatech.warehouse_management_system.outbound.entity.service;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.common.util.PickListItemState;
import com.relatech.warehouse_management_system.outbound.dto.PickListDto;
import com.relatech.warehouse_management_system.outbound.dto.PickListItemDto;
import com.relatech.warehouse_management_system.outbound.entity.PickList;
import com.relatech.warehouse_management_system.outbound.entity.PickListItem;
import com.relatech.warehouse_management_system.outbound.entity.mapper.PickListMapper;
import com.relatech.warehouse_management_system.outbound.entity.repository.PickListItemRepository;
import com.relatech.warehouse_management_system.outbound.entity.repository.PickListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PickListService {
    private final PickListRepository pickListRepository;
    private final PickListItemRepository pickListItemRepository;

    @Transactional
    public PickListDto create(PickListDto pickListDto) {
        PickList pickList = PickListMapper.toEntity(pickListDto);
        return PickListMapper.toDto(pickListRepository.save(pickList));
    }

    @Transactional(readOnly = true)
    public List<PickListDto> getAll() {
        return pickListRepository.findAll()
                .stream().map(PickListMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Page<PickListDto> getAllPickListPaged(Pageable pageable) {
        Page<PickList> pickListPage = pickListRepository.findAll(pageable);
        return pickListPage.map(PickListMapper::toDto);
    }

    @Transactional(readOnly = true)
    public PickListDto getPickListById(Long id) throws ResourceNotFoundException {
        return pickListRepository.findById(id)
                .map(PickListMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("PickList", id));
    }

    @Transactional(readOnly = true)
    public List<PickListItemDto> findItemsByStateOrdered(List<Long> plIds, PickListItemState state, Pageable pageable) {
        List<PickListItem> result = pickListItemRepository.findOpenItemsOrdered(plIds, state, pageable);
        return result.stream().map(PickListMapper::toItemDto).toList();
    }

    @Transactional(readOnly = true)
    public PickListDto getPickListByCode(String pickListCode) throws ResourceNotFoundException {
        return pickListRepository.findByCode(pickListCode)
                .map(PickListMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("PickList", pickListCode));
    }

    public List<PickListDto> createBulk(ArrayList<PickListDto> pickListDtos) {
        List<PickList> entities = pickListDtos.stream()
                .map(PickListMapper::toEntity)
                .toList();

        List<PickList> saved = pickListRepository.saveAll(entities);
        log.info("Picklist generated successfully (bulk)");
        return saved.stream()
                .map(PickListMapper::toDto)
                .toList();
    }
}

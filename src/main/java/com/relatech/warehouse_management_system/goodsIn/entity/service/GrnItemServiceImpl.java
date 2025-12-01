package com.relatech.warehouse_management_system.goodsIn.entity.service;

import com.relatech.warehouse_management_system.goodsIn.checkingInfo.repository.CheckingInfoRepository;
import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.entity.GrnItem;
import com.relatech.warehouse_management_system.goodsIn.entity.mapper.GrnItemMapper;
import com.relatech.warehouse_management_system.goodsIn.entity.repository.GrnItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GrnItemServiceImpl implements GrnItemService {

    private final GrnItemRepository grnItemRepository;
    private final CheckingInfoRepository checkingInfoRepository;
    private final GrnItemMapper grnItemMapper;

    @Override
    public List<GrnItemDto> getAllGrnItems() {
        return grnItemRepository.findAll().stream()
                .map(grnItemMapper::toDto)
                .toList();
    }

    @Override
    public GrnItemDto getGrnItemById(Long id) throws ResourceNotFoundException {
        return grnItemRepository.findById(id)
                .map(grnItemMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("GrnItem", id));
    }

    @Override
    public GrnItemDto getGrnItemByCode(String code) throws ResourceNotFoundException {
        return grnItemRepository.findByCode(code)
                .map(grnItemMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("GrnItem", code));
    }

    @Override
    @Transactional
    public GrnItemDto updateGrnItem(Long id, GrnItemDto grnItemDto) throws ResourceNotFoundException {
        GrnItem existingGrnItem = grnItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GrnItem", id));


        if (grnItemDto.getProductCode() != null)
            existingGrnItem.setProductCode(grnItemDto.getProductCode());


        existingGrnItem.setExpectedQty(grnItemDto.getExpectedQty());
        existingGrnItem.setReceivedQty(grnItemDto.getReceivedQty());
        existingGrnItem.setCompliantQty(grnItemDto.getCompliantQty());
        existingGrnItem.setNotCompliantQty(grnItemDto.getNotCompliantQty());


        if (grnItemDto.getState() != null)
            existingGrnItem.setState(grnItemDto.getState());
        if (grnItemDto.getCheckingInfoList() != null)
            existingGrnItem.setCheckingInfoList(grnItemDto.getCheckingInfoList());

        GrnItem updatedItem = grnItemRepository.save(existingGrnItem);
        return grnItemMapper.toDto(updatedItem);
    }


    @Override
    @Transactional
    public void deleteGrnItem(Long id) throws ResourceNotFoundException {
        grnItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GrnItem", id));
        grnItemRepository.deleteById(id);
    }
}

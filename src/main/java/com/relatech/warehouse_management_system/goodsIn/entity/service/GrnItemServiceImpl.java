package com.relatech.warehouse_management_system.goodsIn.entity.service;

import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.entity.GrnItem;
import com.relatech.warehouse_management_system.goodsIn.entity.mapper.GrnItemMapper;
import com.relatech.warehouse_management_system.goodsIn.entity.repository.GrnItemRepository;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnExceptions;
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
    private final GrnItemMapper grnItemMapper;

    @Override
    public GrnItemDto createGrnItem(GrnItemDto dto) throws GrnExceptions.DuplicateGrnCodeException {
        log.debug("Creating new GRN item with ID: {}", dto.getId());
        GrnItem grnItem = grnItemMapper.toEntity(dto);
        log.info("GRN item created successfully with ID: {}", grnItem.getId());
        return grnItemMapper.toDto(grnItem);
    }

    @Override
    public List<GrnItemDto> getAllGrnItems() {
        return grnItemRepository.findAll().stream()
                .map(grnItemMapper::toDto)
                .toList();
    }

    @Override
    public GrnItemDto getGrnItemById(Long id) throws GrnExceptions.GrnItemNotFoundException {
        log.debug("Fetching GRN item with ID: {}", id);
        GrnItem grnItem = grnItemRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("GRN item not found with ID: {}", id);
                    return new GrnExceptions.GrnItemNotFoundException(id);
                });
        return grnItemMapper.toDto(grnItem);
    }

    @Override
    public GrnItemDto getGrnItemByCode(String code) throws GrnExceptions.GrnItemNotFoundException {
        log.debug("Fetching GRN item with code: {}", code);
        GrnItem grnItem = grnItemRepository.findByCode(code)
                .orElseThrow(() -> {
                    log.warn("GRN not found with code: {}", code);
                    return new GrnExceptions.GrnItemNotFoundException(code);
                });
        return grnItemMapper.toDto(grnItem);
    }

    @Override
    @Transactional
    public GrnItemDto updateGrnItem(Long id, GrnItemDto grnItemDto) throws GrnExceptions.GrnItemNotFoundException {
        GrnItem existingGrnItem = grnItemRepository.findById(id)
                .orElseThrow(() -> new GrnExceptions.GrnItemNotFoundException(id));

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
    public void deleteGrnItem(Long id) throws GrnExceptions.GrnItemNotFoundException {
        grnItemRepository.findById(id)
                .orElseThrow(() -> new GrnExceptions.GrnItemNotFoundException(id));
        grnItemRepository.deleteById(id);
    }

}

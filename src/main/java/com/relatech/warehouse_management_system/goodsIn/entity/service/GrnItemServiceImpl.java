package com.relatech.warehouse_management_system.goodsIn.entity.service;

import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.entity.CheckingInfo;
import com.relatech.warehouse_management_system.goodsIn.entity.GRN;
import com.relatech.warehouse_management_system.goodsIn.entity.GrnItem;
import com.relatech.warehouse_management_system.goodsIn.entity.mapper.GrnItemMapper;
import com.relatech.warehouse_management_system.goodsIn.entity.repository.CheckingInfoRepository;
import com.relatech.warehouse_management_system.goodsIn.entity.repository.GrnItemRepository;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnItemNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrnItemServiceImpl implements GrnItemService {

    private final GrnItemRepository grnItemRepository;
    private final CheckingInfoRepository checkingInfoRepository;
    private final GrnItemMapper grnItemMapper;


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public GrnItemDto createGrnItem(GrnItemDto dto) {
        log.debug("Creating new GRN item with ID: {}", dto.getId());
        GrnItem grnItem = grnItemMapper.toEntity(dto);
        grnItemRepository.save(grnItem);
        log.info("GRN item created successfully with ID: {}", grnItem.getId());
        return grnItemMapper.toDto(grnItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GrnItemDto> getAllGrnItems() {
        return grnItemRepository.findAll().stream()
                .map(grnItemMapper::toDto)
                .toList();
    }

    @Override
    public Page<GrnItemDto> getAllGrnItemsPaged(Pageable pageable) {
        log.debug("Fetching paginated GrnItems: page {}, size {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<GrnItem> grnItemPage = grnItemRepository.findAll(pageable);
        return grnItemPage.map(grnItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public GrnItemDto getGrnItemById(Long id) throws GrnItemNotFoundException {
        log.debug("Fetching GRN item with ID: {}", id);
        GrnItem grnItem = grnItemRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("GRN item not found with ID: {}", id);
                    return new GrnItemNotFoundException(id);
                });
        return grnItemMapper.toDto(grnItem);
    }

    @Override
    @Transactional(readOnly = true)
    public GrnItemDto getGrnItemByCode(String code) throws GrnItemNotFoundException {
        log.debug("Fetching GRN item with code: {}", code);
        GrnItem grnItem = grnItemRepository.findByCode(code)
                .orElseThrow(() -> {
                    log.warn("GRN not found with code: {}", code);
                    return new GrnItemNotFoundException(code);
                });
        return grnItemMapper.toDto(grnItem);
    }

    @Override
    @Transactional(rollbackFor = {GrnItemNotFoundException.class, Exception.class})
    public GrnItemDto updateGrnItem(Long id, GrnItemDto grnItemDto) throws GrnItemNotFoundException {
        GrnItem existingGrnItem = grnItemRepository.findById(id)
                .orElseThrow(() -> new GrnItemNotFoundException(id));

        if (grnItemDto.getProductCode() != null)
            existingGrnItem.setProductCode(grnItemDto.getProductCode());

        existingGrnItem.setExpectedQty(grnItemDto.getExpectedQty());
        existingGrnItem.setReceivedQty(grnItemDto.getReceivedQty());
        existingGrnItem.setCompliantQty(grnItemDto.getCompliantQty());
        existingGrnItem.setNotCompliantQty(grnItemDto.getNotCompliantQty());


        if (grnItemDto.getState() != null)
            existingGrnItem.setState(grnItemDto.getState());

        GrnItem updatedItem = grnItemRepository.save(existingGrnItem);
        return grnItemMapper.toDto(updatedItem);
    }

    @Override
    @Transactional(rollbackFor = {GrnItemNotFoundException.class}, propagation = Propagation.REQUIRES_NEW)
    public void deleteGrnItem(Long id) throws GrnItemNotFoundException {
        grnItemRepository.findById(id)
                .orElseThrow(() -> new GrnItemNotFoundException(id));
        grnItemRepository.deleteById(id);
    }

    @Transactional(rollbackFor = {GrnItemNotFoundException.class})
    @Override
    public void addCheckingInfo(Long grnItemId, Long checkingInfoId) throws Exception {
        GrnItem item = grnItemRepository.findById(grnItemId)
                .orElseThrow(() -> new GrnItemNotFoundException(grnItemId));

        CheckingInfo info = checkingInfoRepository.findById(checkingInfoId)
                .orElseThrow(() -> new Exception("CheckingInfo not found"));

        item.getCheckingInfoList().add(info);
    }

}

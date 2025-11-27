package com.relatech.warehouse_management_system.goodsIn.entity.service;

import com.relatech.warehouse_management_system.checkingInfo.entity.CheckingInfo;
import com.relatech.warehouse_management_system.checkingInfo.repository.CheckingInfoRepository;
import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.goodsIn.entity.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.entity.GrnItem;
import com.relatech.warehouse_management_system.goodsIn.entity.mapper.GrnItemMapper;
import com.relatech.warehouse_management_system.goodsIn.repository.GrnItemRepository;
import com.relatech.warehouse_management_system.util.State;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
//@Transactional(readOnly = true) //TODO
public class GrnItemServiceImpl implements GrnItemService {

    @Autowired
    private GrnItemRepository grnItemRepository;

    @Autowired
    private CheckingInfoRepository checkingInfoRepository;

    @Override
    public GrnItemDto createGrnItem(GrnItemDto grnItemDto) {
        GrnItem grnItem = GrnItemMapper.toEntity(grnItemDto);

        validateAndCalculateReceivedQty(grnItem);

        GrnItem savedItem = grnItemRepository.save(grnItem);
        return GrnItemMapper.toDto(savedItem);
    }

    @Override
    public List<GrnItemDto> getAllGrnItems() {
        return grnItemRepository.findAll()
                .stream()
                .map(GrnItemMapper::toDto).toList();
    }

    @Override
    public GrnItemDto getGrnItemById(Long id) throws ResourceNotFoundException {
        return grnItemRepository.findById(id)
                .map(GrnItemMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("GrnItem", id));
    }

    @Override
    public GrnItemDto updateGrnItem(Long id, GrnItemDto grnItemDto) throws ResourceNotFoundException {
        GrnItem existingGrnItem = grnItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GrnItem", id));
        //TODO scegliere quali campi aggiornare
        existingGrnItem.setProductCode(grnItemDto.getProductCode());
        existingGrnItem.setExpectedQty(grnItemDto.getExpectedQty());
        existingGrnItem.setReceivedQty(grnItemDto.getReceivedQty());
        existingGrnItem.setCompliantQty(grnItemDto.getCompliantQty());
        existingGrnItem.setNotCompliantQty(grnItemDto.getNotCompliantQty());
        existingGrnItem.setState(grnItemDto.getState());
        existingGrnItem.setCheckingInfoList(grnItemDto.getCheckingInfoList());

        validateAndCalculateReceivedQty(existingGrnItem);

        GrnItem updatedItem = grnItemRepository.save(existingGrnItem);
        return GrnItemMapper.toDto(updatedItem);
    }

    @Override
    public void deleteGrnItem(Long id) throws ResourceNotFoundException {
        GrnItem grnItem = grnItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GrnItem", id));
        grnItemRepository.deleteById(id);
    }

    @Override
    @Transactional
    public GrnItemDto addCheckinginfoToGrnitem(Long grnitemId, List<Long> ciIds) throws ResourceNotFoundException {
        GrnItem grnItem = grnItemRepository.findById(grnitemId)
                .orElseThrow(() -> new ResourceNotFoundException("GrnItem", grnitemId));

        List<CheckingInfo> checkingInfoList = checkingInfoRepository.findAllById(ciIds);
        grnItem.addCInfos(checkingInfoList);
        log.info("Assigned checkinginfo {} to grnitem {}", ciIds.getFirst(), grnitemId);

        checkIfExpectedQtyIsSatisfied(grnItem);

        return GrnItemMapper.toDto(grnItemRepository.save(grnItem));
    }

    @Override
    @Transactional
    public GrnItem updateGrnItemState(Long grnItemId, State newState) throws ResourceNotFoundException {
        GrnItem grnItem = grnItemRepository.findById(grnItemId)
                .orElseThrow(() -> new ResourceNotFoundException("GrnItem", grnItemId));

        grnItem.setState(newState);
        log.info("Updated GrnItem {} to state {}", grnItem.getId(), newState);
        return grnItemRepository.save(grnItem);
    }

    private void checkIfExpectedQtyIsSatisfied(GrnItem grnItem) throws ResourceNotFoundException {
        int totalAssigned = grnItem.getCheckingInfoList()
                .stream()
                .mapToInt(CheckingInfo::getQuantity)
                .sum();

        if (totalAssigned == grnItem.getExpectedQty() && grnItem.getState() == State.OPEN) {
            updateGrnItemState(grnItem.getId(), State.CHECKED);
        }
    }

    private void validateAndCalculateReceivedQty(GrnItem grnItem) {
        int calculated = grnItem.getCompliantQty() + grnItem.getNotCompliantQty();
        if (grnItem.getReceivedQty() != calculated) {
            throw new IllegalArgumentException(
                    "receivedQty deve essere uguale a compliantQty + notCompliantQty"
            );
        }
    }
}
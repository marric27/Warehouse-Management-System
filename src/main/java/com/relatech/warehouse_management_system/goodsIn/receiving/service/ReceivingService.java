package com.relatech.warehouse_management_system.goodsIn.receiving.service;

import com.relatech.warehouse_management_system.common.util.State;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnDTO;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnItemService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnService;
import com.relatech.warehouse_management_system.goodsIn.exception.*;
import com.relatech.warehouse_management_system.goodsIn.GrnItemStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReceivingService {

    private final GrnService grnService;
    private final GrnItemService grnItemService;
    private final GrnItemStateService stateService;

    // CREATE GRN
    @Transactional(rollbackFor = {Exception.class})
    public GrnDTO createGRN(GrnDTO dto) {
        dto.setState(State.OPEN);
        if (dto.getReceivingDate() == null)
            dto.setReceivingDate(LocalDate.now());
        return grnService.createGRN(dto);
    }

    // CREATE ITEM AND ASSIGN TO GRN BY ID
    @Transactional(rollbackFor = {Exception.class})
    public GrnItemDto createItem(Long grnId, GrnItemDto item) throws GrnNotFoundException, CannotAssignItemToGrnClosedException, InvalidQuantityException, QuantityMismatchException, OverReceivedQuantityException, GrnItemNotFoundException {
        if(grnService.getGRNById(grnId) == null) throw new GrnNotFoundException(grnId);
        if(stateService.checkGrnIfClosed(grnId))
            throw new CannotAssignItemToGrnClosedException(grnId);
        stateService.validateItemQuantities(item);

        item.setGrnId(grnId);
        GrnItemDto saved = grnItemService.createGrnItem(item);

        stateService.evaluateAndProgressItemState(saved);
        return saved;
    }

    // UPDATE ITEM
    @Transactional(rollbackFor = {Exception.class})
    public GrnItemDto updateItem(Long itemId, GrnItemDto dto) throws Exception {

        GrnItemDto item = grnItemService.getGrnItemById(itemId);

        if (dto.getExpectedQty() > 0) item.setExpectedQty(dto.getExpectedQty());
        if (dto.getReceivedQty() >= 0) item.setReceivedQty(dto.getReceivedQty());
        if (dto.getCompliantQty() >= 0) item.setCompliantQty(dto.getCompliantQty());
        if (dto.getNotCompliantQty() >= 0) item.setNotCompliantQty(dto.getNotCompliantQty());

        stateService.validateItemQuantities(item);

        GrnItemDto saved = grnItemService.updateGrnItem(itemId, item);

        stateService.evaluateAndProgressItemState(saved);
        return saved;
    }

    @Transactional(readOnly = true)
    public GrnDTO getGRN(Long id) throws GrnNotFoundException {
        return grnService.getGRNById(id);
    }

    @Transactional(readOnly = true)
    public List<GrnDTO> listGrn() {
        return grnService.getAllGRNs();
    }

    public Page<GrnDTO> listGrnPaged(Pageable pageable) {
        return grnService.getAllGRNsPaged(pageable);
    }

    @Transactional(readOnly = true)
    public List<GrnItemDto> listGrnItems() {
        return grnItemService.getAllGrnItems();
    }

    public Page<GrnItemDto> listGrnItemsPaged(Pageable pageable) {
        return grnItemService.getAllGrnItemsPaged(pageable);
    }
}

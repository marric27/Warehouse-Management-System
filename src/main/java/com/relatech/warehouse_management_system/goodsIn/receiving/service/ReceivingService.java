package com.relatech.warehouse_management_system.goodsIn.receiving.service;

import com.relatech.warehouse_management_system.common.util.State;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnDTO;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnItemService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnService;
import com.relatech.warehouse_management_system.goodsIn.exception.CannotAssignItemToGrnClosedException;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnExceptions;
import com.relatech.warehouse_management_system.goodsIn.GrnItemStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ReceivingService {

    private final GrnService grnService;
    private final GrnItemService grnItemService;
    private final GrnItemStateService stateService;

    // CREATE GRN
    public GrnDTO createGRN(GrnDTO dto) throws GrnExceptions.DuplicateGrnCodeException {
        dto.setState(State.OPEN);
        if (dto.getReceivingDate() == null)
            dto.setReceivingDate(LocalDate.now());
        return grnService.createGRN(dto);
    }

    // TODO tolgo e uso direttamente quelli del grnserviceimpl?
    public GrnDTO getGRN(Long id) throws GrnExceptions.GrnNotFoundException {
        return grnService.getGRNById(id);
    }

    public List<GrnDTO> list() {
        return grnService.getAllGRNs();
    }

    // CREATE ITEM AND ASSIGN TO GRN BY ID
    public GrnItemDto createItem(Long grnId, GrnItemDto item) throws Exception {
        // se grn è closed non posso assegnare item -> return exception
        if(stateService.checkGrnIfClosed(grnId)) throw new CannotAssignItemToGrnClosedException(grnId);
        stateService.validateItemQuantities(item);

        item.setGrnId(grnId);
        GrnItemDto saved = grnItemService.createGrnItem(item);

        stateService.evaluateAndProgressItemState(saved);
        return saved;
    }

    // UPDATE ITEM
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
}

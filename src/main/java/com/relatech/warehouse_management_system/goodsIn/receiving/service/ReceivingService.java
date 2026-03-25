package com.relatech.warehouse_management_system.goodsIn.receiving.service;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.common.util.State;
import com.relatech.warehouse_management_system.goodsIn.GrnItemStateService;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnDto;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnItemService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnService;
import com.relatech.warehouse_management_system.goodsIn.events.GrnItemQuantitiesValidatedEvent;
import com.relatech.warehouse_management_system.goodsIn.exception.*;
import com.relatech.warehouse_management_system.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ProductService productService;
    private final ApplicationEventPublisher eventPublisher;

    // CREATE GRN
    @Transactional(rollbackFor = { Exception.class })
    public GrnDto createGRN(GrnDto dto) {
        dto.setState(State.OPEN);
        if (dto.getReceivingDate() == null)
            dto.setReceivingDate(LocalDate.now());
        return grnService.createGRN(dto);
    }

    // CREATE ITEM AND ASSIGN TO GRN BY ID
    @Transactional(rollbackFor = { Exception.class })
    public GrnItemDto createItem(String grnCode, GrnItemDto item) throws GrnNotFoundException,
            CannotAssignItemToGrnClosedException, InvalidQuantityException, QuantityMismatchException,
            OverReceivedQuantityException, ResourceNotFoundException {

        GrnDto grn = grnService.getGRNByCode(grnCode);
        if (grn.getState() == State.CLOSED)
            throw new CannotAssignItemToGrnClosedException(grnCode);

        productService.validateProductExists(item.getProductCode());

        stateService.validateItemQuantities(item);

        item.setGrnId(grn.getId());
        GrnItemDto saved = grnItemService.createGrnItem(item);

        eventPublisher.publishEvent(new GrnItemQuantitiesValidatedEvent(
                saved.getId(),
                null,
                saved.getState(),
                saved.getGrnId()
        ));
        return saved;
    }

    // UPDATE ITEM
    @Transactional(rollbackFor = { Exception.class })
    public GrnItemDto updateItem(Long itemId, GrnItemDto dto) throws Exception {

        GrnItemDto item = grnItemService.getGrnItemById(itemId);

        if (dto.getExpectedQty() > 0)
            item.setExpectedQty(dto.getExpectedQty());
        if (dto.getReceivedQty() >= 0)
            item.setReceivedQty(dto.getReceivedQty());
        if (dto.getCompliantQty() >= 0)
            item.setCompliantQty(dto.getCompliantQty());
        if (dto.getNotCompliantQty() >= 0)
            item.setNotCompliantQty(dto.getNotCompliantQty());

        State oldState = item.getState();
        stateService.validateItemQuantities(item);

        GrnItemDto saved = grnItemService.updateGrnItem(itemId, item);

        eventPublisher.publishEvent(new GrnItemQuantitiesValidatedEvent(
                saved.getId(),
                oldState,
                saved.getState(),
                saved.getGrnId()
        ));
        return saved;
    }

    @Transactional(readOnly = true)
    public GrnDto getGRN(Long id) throws GrnNotFoundException {
        return grnService.getGRNById(id);
    }

    @Transactional(readOnly = true)
    public List<GrnDto> listGrn() {
        return grnService.getAllGRNs();
    }

    public Page<GrnDto> listGrnPaged(Pageable pageable) {
        return grnService.getAllGRNsPaged(pageable);
    }

    @Transactional(readOnly = true)
    public List<GrnItemDto> listGrnItems() {
        return grnItemService.getAllGrnItems();
    }

    public Page<GrnItemDto> listGrnItemsPaged(Pageable pageable) {
        return grnItemService.getAllGrnItemsPaged(pageable);
    }

    public GrnItemDto getGrnItemById(Long itemId) throws GrnItemNotFoundException {
        return grnItemService.getGrnItemById(itemId);
    }

    public GrnDto getGRNByCode(String code) throws GrnNotFoundException {
        return grnService.getGRNByCode(code);
    }

    public GrnItemDto getGrnItemByCode(String code) throws GrnItemNotFoundException {
        return grnItemService.getGrnItemByCode(code);
    }
}

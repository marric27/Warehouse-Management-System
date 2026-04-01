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
import com.relatech.warehouse_management_system.goodsIn.states.GrnItemStateHandler;
import com.relatech.warehouse_management_system.goodsIn.states.GrnItemStateHandlerResolver;
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
    private final ProductService productService;
    private final ApplicationEventPublisher eventPublisher;
    private final GrnItemStateHandlerResolver resolver;

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

        validateItemQuantities(item);

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

    // VALIDAZIONE QUANTITÀ
    public void validateItemQuantities(GrnItemDto item) throws InvalidQuantityException, QuantityMismatchException, OverReceivedQuantityException {

        int expected = item.getExpectedQty();
        int compliant = item.getCompliantQty();
        int notCompliant = item.getNotCompliantQty();
        int received = item.getReceivedQty();

        if (expected <= 0)
            throw new InvalidQuantityException("Expected qty must be > 0");

        if (received != compliant + notCompliant)
            throw new QuantityMismatchException("Received != compliant + notCompliant");

        if (received > expected)
            throw new OverReceivedQuantityException("Over-received: expected=" + expected + " received=" + received);

        GrnItemStateHandler currentHandler = resolver.resolve(item.getState());
        item.setState(currentHandler.onQuantitiesValidated(item));
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

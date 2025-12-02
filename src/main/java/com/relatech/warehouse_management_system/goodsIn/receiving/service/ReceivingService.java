package com.relatech.warehouse_management_system.goodsIn.receiving.service;

/*
 * CENTRAL ORCHESTRATOR for Goods-In process: coordinates GRN headers, items,
 * and quality checks (CheckingInfo) in one transactional boundary.
 *
 * Handles full lifecycle: creation, validation, state transitions,
 * item assignment, and auto-progression rules.
 */
import com.relatech.warehouse_management_system.goodsIn.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.goodsIn.entity.CheckingInfo;
import com.relatech.warehouse_management_system.goodsIn.entity.service.CheckingInfoService;
import com.relatech.warehouse_management_system.goodsIn.entity.mapper.GrnItemMapper;
import com.relatech.warehouse_management_system.goodsIn.entity.mapper.GrnMapper;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnDTO;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.entity.GRN;
import com.relatech.warehouse_management_system.goodsIn.entity.GrnItem;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnItemService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnService;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnExceptions;
import com.relatech.warehouse_management_system.common.util.State;
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
    private final CheckingInfoService checkingInfoService;
    private final GrnMapper grnMapper;
    private final GrnItemMapper grnItemMapper;

    // Basic create/read/update/delete for GRN headers with business rules

    /**
     * Creates new GRN header with duplicate code check + business defaults
     * (OPEN state, today date). Maps DTO→Entity→DTO.
     */
    public GrnDTO createGRN(GrnDTO grnDTO) throws GrnExceptions.GrnNotFoundException, GrnExceptions.DuplicateGrnCodeException {
        log.info("Creating new GRN with code: {}", grnDTO.getCode());

        GRN grn = grnMapper.toEntity(grnDTO);
        if (grn.getState() == null) grn.setState(State.OPEN);
        if (grn.getReceivingDate() == null) grn.setReceivingDate(LocalDate.now());

        GrnDTO dto = grnService.createGRN(grnDTO);
        log.info("GRN {} created successfully", dto.getId());
        return dto;
    }

    /** Reads single GRN by ID → Entity→DTO */
    public GrnDTO readGRN(Long id) throws GrnExceptions.GrnNotFoundException {
        return grnService.getGRNById(id);
    }

    /** Lists ALL GRNs (non-paginated) → Entity→DTO */
    public List<GrnDTO> listGRNs() {
        return grnService.getAllGRNs();
    }

    /** Text search across GRN fields → Entity→DTO */
    public List<GrnDTO> searchGRNs(String term) {
        return grnService.searchGrns(term);
    }

    /** Partial update: only supplier/date if provided */
    public GrnDTO updateGRN(Long id, GrnDTO grnDTO) throws GrnExceptions.GrnNotFoundException {
        GrnDTO toUpdate = grnService.getGRNById(id);

        if (grnDTO.getSupplier() != null) {
            toUpdate.setSupplier(grnDTO.getSupplier());
        }
        if (grnDTO.getReceivingDate() != null) {
            toUpdate.setReceivingDate(grnDTO.getReceivingDate());
        }
        return grnService.updateGRN(id, toUpdate);
    }

    /** Delete GRN only if it has NO items attached */
    public void deleteGRN(Long id) throws GrnExceptions.GrnNotFoundException, GrnExceptions.GrnWithItemsException {
        GrnDTO grn = grnService.getGRNById(id);

        if (grn.getItems() != null && !grn.getItems().isEmpty()) {
            throw new GrnExceptions.GrnWithItemsException(id.toString());
        }
        grnService.deleteById(id);
    }


    // GRN ITEMS - ORCHESTRATED (parent GRN coordination)
    // Creates/updates items while maintaining GRN aggregate consistency


    /**
     * Creates item → validates quantities → saves item → adds to parent GRN
     * → saves GRN aggregate for relationship consistency
     */
    public GrnItemDto createGRNItem(Long grnId, GrnItemDto dto)
            throws GrnExceptions.GrnNotFoundException, GrnExceptions.InvalidQuantityException,
            GrnExceptions.QuantityMismatchException, GrnExceptions.OverReceivedQuantityException, GrnExceptions.DuplicateGrnCodeException {

        GrnDTO grnDTO = grnService.getGRNById(grnId);
        GRN grn = grnMapper.toEntity(grnDTO);

        GrnItem item = grnItemMapper.toEntity(dto);
        item.setGrn(grn);

        validateGRNItemLogic(item);
        grnItemService.createGrnItem(dto);

        grn.addItem(item);
        grnService.updateGRN(grnId, grnMapper.toDto(grn));

        return grnItemMapper.toDto(item);
    }

    public GrnDTO createItemForGrn(Long grnId, GrnItemDto dto) throws GrnExceptions.GrnItemNotFoundException, GrnExceptions.InvalidQuantityException, GrnExceptions.QuantityMismatchException, GrnExceptions.OverReceivedQuantityException {
        validateGRNItemLogic(grnItemMapper.toEntity(dto));
        return grnService.addItemToGrn(grnId, dto);
    }

    /**
     * Partial update of item fields → re-validate quantities → save
     * (product code, qtys, state)
     */
    public GrnItemDto updateGRNItem(Long grnItemId, GrnItemDto dto)
            throws GrnExceptions.GrnItemNotFoundException, GrnExceptions.InvalidQuantityException,
            GrnExceptions.QuantityMismatchException, GrnExceptions.OverReceivedQuantityException {

        GrnItemDto toUpdate = grnItemService.getGrnItemById(grnItemId);

        if (dto.getProductCode() != null) toUpdate.setProductCode(dto.getProductCode());
        if (dto.getExpectedQty() > 0) toUpdate.setExpectedQty(dto.getExpectedQty());
        if (dto.getReceivedQty() >= 0) toUpdate.setReceivedQty(dto.getReceivedQty());
        if (dto.getCompliantQty() >= 0) toUpdate.setCompliantQty(dto.getCompliantQty());
        if (dto.getNotCompliantQty() >= 0) toUpdate.setNotCompliantQty(dto.getNotCompliantQty());
        if (dto.getState() != null) toUpdate.setState(dto.getState());

        validateGRNItemLogic(grnItemMapper.toEntity(toUpdate));

        return grnItemService.updateGrnItem(grnItemId, toUpdate);
    }

    /** Lists all items belonging to specific GRN */
    public List<GrnItemDto> listGRNItems(Long grnId) throws GrnExceptions.GrnNotFoundException {
        GrnDTO grn = grnService.getGRNById(grnId);

        List<GrnItemDto> items = grn.getItems();
        return items != null ? items : List.of();
    }

    public GrnItemDto readGRNItem(Long id) throws GrnExceptions.GrnItemNotFoundException {
        return grnItemService.getGrnItemById(id);
    }

    public void deleteGRNItem(Long id) throws GrnExceptions.GrnItemNotFoundException {
        grnItemService.deleteGrnItem(id);
    }


    // CHECKING INFO ASSIGNMENT
    // Links quality checks to items + auto-state progression


    /**
     * Assigns CheckingInfo entries to item → checks if sum(qty) >= expected
     * → auto CHECKED if complete
     */
    public GrnItemDto assignCheckingInfoToItem(Long itemId, List<Long> checkingInfoIds)
            throws GrnExceptions.GrnItemNotFoundException, GrnExceptions.QuantityMismatchException {
        GrnItemDto item = grnItemService.getGrnItemById(itemId);

        List<CheckingInfoDto> checkingInfos = checkingInfoService.getAllById(checkingInfoIds);
        item.setCheckingInfoList(checkingInfos);

        GrnItemDto updated = grnItemService.updateGrnItem(itemId, item);
        checkAssignedQuantity(grnItemMapper.toEntity(updated));

        return updated;
    }

    // Validates and applies allowed state transitions
    // OPEN → CHECKED/PUTAWAY | CHECKED → PUTAWAY/CLOSED | PUTAWAY → CLOSED

    public GrnDTO changeGRNState(Long id, State newState)
            throws GrnExceptions.GrnNotFoundException, GrnExceptions.InvalidStateTransitionException {
        GrnDTO grn = grnService.getGRNById(id);

        State currentState = grn.getState() != null ? grn.getState() : State.OPEN;
        validateStateTransition(currentState, newState);

        grn.setState(newState);
        return grnService.updateGRN(id, grn);
    }

    public GrnItemDto changeItemState(Long id, State newState)
            throws GrnExceptions.GrnItemNotFoundException, GrnExceptions.InvalidStateTransitionException {
        GrnItemDto item = grnItemService.getGrnItemById(id);

        State currentState = item.getState() != null ? item.getState() : State.OPEN;
        validateStateTransition(currentState, newState);

        item.setState(newState);
        return grnItemService.updateGrnItem(id, item);
    }


    // BUSINESS VALIDATIONS (Reusable private methods)
    // Centralized rules for quantities and state transitions

    /**
     * Quantity business rules:
     * 1. expected > 0
     * 2. received = compliant + notCompliant
     * 3. received <= expected (no over-receipt)
     */
    private void validateGRNItemLogic(GrnItem item)
            throws GrnExceptions.InvalidQuantityException, GrnExceptions.QuantityMismatchException,
            GrnExceptions.OverReceivedQuantityException {
        int compliant = item.getCompliantQty();
        int notCompliant = item.getNotCompliantQty();
        int received = item.getReceivedQty();
        int expected = item.getExpectedQty();

        if (expected <= 0) {
            throw new GrnExceptions.InvalidQuantityException("Expected quantity must be > 0");
        }

        if (received != (compliant + notCompliant)) {
            throw new GrnExceptions.QuantityMismatchException(
                    String.format("Qty mismatch: received=%d vs compliant=%d+notCompliant=%d",
                            received, compliant, notCompliant));
        }

        if (received > expected) {
            throw new GrnExceptions.OverReceivedQuantityException(
                    String.format("OVER-RECEIVED: expected=%d, received=%d", expected, received));
        }
    }

    /** Validates finite state machine transitions */
    private void validateStateTransition(State current, State next) throws GrnExceptions.InvalidStateTransitionException {
        if (current == null || next == null) {
            throw new GrnExceptions.InvalidStateTransitionException(current, next);
        }

        if (current == State.CLOSED) {
            throw new GrnExceptions.InvalidStateTransitionException(current, next);
        }

        if (!isAllowedTransition(current, next)) {
            throw new GrnExceptions.InvalidStateTransitionException(current, next);
        }
    }

    /** State machine rules: defines allowed transitions */
    private boolean isAllowedTransition(State from, State to) {
        return switch (from) {
            case OPEN -> to == State.CHECKED || to == State.PUTAWAY;
            case CHECKED -> to == State.PUTAWAY || to == State.CLOSED;
            case PUTAWAY -> to == State.CLOSED;
            default -> false;
        };
    }

    /**
     * AUTO-PROGRESSION: if assigned CheckingInfo qty >= expected
     * and state=OPEN → auto CHECKED
     */
    private void checkAssignedQuantity(GrnItem item) throws GrnExceptions.GrnItemNotFoundException {
        int expected = item.getExpectedQty();
        List<CheckingInfo> checkingInfos = item.getCheckingInfoList();
        int assigned = (checkingInfos == null || checkingInfos.isEmpty()) ? 0 :
                checkingInfos.stream().mapToInt(CheckingInfo::getQuantity).sum();

        State currentState = item.getState() != null ? item.getState() : State.OPEN;
        if (assigned >= expected && currentState == State.OPEN) {
            log.info("Item {} complete → auto CHECKED", item.getId());
            item.setState(State.CHECKED);
            grnItemService.updateGrnItem(item.getId(), grnItemMapper.toDto(item));
        }
    }
}

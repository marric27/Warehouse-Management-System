package com.relatech.warehouse_management_system.goodsIn.receiving.service;

/*
 * CENTRAL ORCHESTRATOR for Goods-In process: coordinates GRN headers, items,
 * and quality checks (CheckingInfo) in one transactional boundary.
 *
 * Handles full lifecycle: creation, validation, state transitions,
 * item assignment, and auto-progression rules.
 */
import com.relatech.warehouse_management_system.goodsIn.checkingInfo.entity.CheckingInfo;
import com.relatech.warehouse_management_system.goodsIn.checkingInfo.repository.CheckingInfoRepository;
import com.relatech.warehouse_management_system.goodsIn.entity.mapper.GrnItemMapper;
import com.relatech.warehouse_management_system.goodsIn.entity.mapper.GrnMapper;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnDTO;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.entity.GRN;
import com.relatech.warehouse_management_system.goodsIn.entity.GrnItem;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnExceptions;
import com.relatech.warehouse_management_system.goodsIn.entity.repository.GrnItemRepository;
import com.relatech.warehouse_management_system.goodsIn.entity.repository.GrnRepository;
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

    private final GrnRepository grnRepository;
    private final GrnItemRepository grnItemRepository;
    private final CheckingInfoRepository checkingInfoRepository;
    private final GrnMapper grnMapper;
    private final GrnItemMapper grnItemMapper;

    // Basic create/read/update/delete for GRN headers with business rules

    /**
     * Creates new GRN header with duplicate code check + business defaults
     * (OPEN state, today date). Maps DTO→Entity→DTO.
     */
    public GrnDTO createGRN(GrnDTO grnDTO) throws GrnExceptions.DuplicateGrnCodeException {
        log.info("Creating new GRN with code: {}", grnDTO.getCode());

        if (grnRepository.findByCode(grnDTO.getCode()).isPresent()) {
            throw new GrnExceptions.DuplicateGrnCodeException(grnDTO.getCode());
        }

        GRN grn = grnMapper.toEntity(grnDTO);
        if (grn.getState() == null) grn.setState(State.OPEN);
        if (grn.getReceivingDate() == null) grn.setReceivingDate(LocalDate.now());

        grn = grnRepository.save(grn);
        log.info("GRN {} created successfully", grn.getId());
        return grnMapper.toDto(grn);
    }

    /** Reads single GRN by ID → Entity→DTO */
    public GrnDTO readGRN(Long id) throws GrnExceptions.GrnNotFoundException {
        GRN grn = grnRepository.findById(id)
                .orElseThrow(() -> new GrnExceptions.GrnNotFoundException(id));
        return grnMapper.toDto(grn);
    }

    /** Lists ALL GRNs (non-paginated) → Entity→DTO */
    public List<GrnDTO> listGRNs() {
        return grnRepository.findAll().stream()
                .map(grnMapper::toDto)
                .toList();
    }

    /** Text search across GRN fields → Entity→DTO */
    public List<GrnDTO> searchGRNs(String term) {
        return grnRepository.searchByTerm(term).stream()
                .map(grnMapper::toDto)
                .toList();
    }

    /** Partial update: only supplier/date if provided */
    public GrnDTO updateGRN(Long id, GrnDTO grnDTO) throws GrnExceptions.GrnNotFoundException {
        GRN grn = grnRepository.findById(id)
                .orElseThrow(() -> new GrnExceptions.GrnNotFoundException(id));

        if (grnDTO.getSupplier() != null) {
            grn.setSupplier(grnDTO.getSupplier());
        }
        if (grnDTO.getReceivingDate() != null) {
            grn.setReceivingDate(grnDTO.getReceivingDate());
        }

        grn = grnRepository.save(grn);
        return grnMapper.toDto(grn);
    }

    /** Delete GRN only if it has NO items attached */
    public void deleteGRN(Long id) throws GrnExceptions.GrnNotFoundException, GrnExceptions.GrnWithItemsException {
        GRN grn = grnRepository.findById(id)
                .orElseThrow(() -> new GrnExceptions.GrnNotFoundException(id));

        if (grn.getItems() != null && !grn.getItems().isEmpty()) {
            throw new GrnExceptions.GrnWithItemsException(id.toString());
        }

        grnRepository.delete(grn);
    }


    // GRN ITEMS - ORCHESTRATED (parent GRN coordination)
    // Creates/updates items while maintaining GRN aggregate consistency


    /**
     * Creates item → validates quantities → saves item → adds to parent GRN
     * → saves GRN aggregate for relationship consistency
     */
    public GrnItemDto createGRNItem(Long grnId, GrnItemDto dto)
            throws GrnExceptions.GrnNotFoundException, GrnExceptions.InvalidQuantityException,
            GrnExceptions.QuantityMismatchException, GrnExceptions.OverReceivedQuantityException {
        GRN grn = grnRepository.findById(grnId)
                .orElseThrow(() -> new GrnExceptions.GrnNotFoundException(grnId));

        GrnItem item = grnItemMapper.toEntity(dto);
        item.setGrn(grn);

        validateGRNItemLogic(item);
        item = grnItemRepository.save(item);

        grn.addItem(item);
        grnRepository.save(grn);

        return grnItemMapper.toDto(item);
    }

    /**
     * Partial update of item fields → re-validate quantities → save
     * (product code, qtys, state)
     */
    public GrnItemDto updateGRNItem(Long grnItemId, GrnItemDto dto)
            throws GrnExceptions.GrnItemNotFoundException, GrnExceptions.InvalidQuantityException,
            GrnExceptions.QuantityMismatchException, GrnExceptions.OverReceivedQuantityException {
        GrnItem item = grnItemRepository.findById(grnItemId)
                .orElseThrow(() -> new GrnExceptions.GrnItemNotFoundException(grnItemId));

        if (dto.getProductCode() != null) item.setProductCode(dto.getProductCode());
        if (dto.getExpectedQty() > 0) item.setExpectedQty(dto.getExpectedQty());
        if (dto.getReceivedQty() >= 0) item.setReceivedQty(dto.getReceivedQty());
        if (dto.getCompliantQty() >= 0) item.setCompliantQty(dto.getCompliantQty());
        if (dto.getNotCompliantQty() >= 0) item.setNotCompliantQty(dto.getNotCompliantQty());
        if (dto.getState() != null) item.setState(dto.getState());

        validateGRNItemLogic(item);
        item = grnItemRepository.save(item);

        return grnItemMapper.toDto(item);
    }

    /** Lists all items belonging to specific GRN */
    public List<GrnItemDto> listGRNItems(Long grnId) throws GrnExceptions.GrnNotFoundException {
        GRN grn = grnRepository.findById(grnId)
                .orElseThrow(() -> new GrnExceptions.GrnNotFoundException(grnId));

        List<GrnItem> items = grn.getItems();
        return items != null ? grnItemMapper.toDto(items) : List.of();
    }

    public GrnItemDto readGRNItem(Long id) throws GrnExceptions.GrnItemNotFoundException {
        GrnItem item = grnItemRepository.findById(id)
                .orElseThrow(() -> new GrnExceptions.GrnItemNotFoundException(id));
        return grnItemMapper.toDto(item);
    }

    public void deleteGRNItem(Long id) throws GrnExceptions.GrnItemNotFoundException {
        grnItemRepository.findById(id)
                .orElseThrow(() -> new GrnExceptions.GrnItemNotFoundException(id));
        grnItemRepository.deleteById(id);
    }


    // CHECKING INFO ASSIGNMENT
    // Links quality checks to items + auto-state progression


    /**
     * Assigns CheckingInfo entries to item → checks if sum(qty) >= expected
     * → auto CHECKED if complete
     */
    public GrnItemDto assignCheckingInfoToItem(Long itemId, List<Long> checkingInfoIds)
            throws GrnExceptions.GrnItemNotFoundException, GrnExceptions.QuantityMismatchException {
        GrnItem item = grnItemRepository.findById(itemId)
                .orElseThrow(() -> new GrnExceptions.GrnItemNotFoundException(itemId));

        List<CheckingInfo> checkingInfos = checkingInfoRepository.findAllById(checkingInfoIds);
        item.setCheckingInfoList(checkingInfos);

        GrnItem updated = grnItemRepository.save(item);
        checkAssignedQuantity(updated);

        return grnItemMapper.toDto(updated);
    }


    // Validates and applies allowed state transitions
    // OPEN → CHECKED/PUTAWAY | CHECKED → PUTAWAY/CLOSED | PUTAWAY → CLOSED


    public GrnDTO changeGRNState(Long id, State newState)
            throws GrnExceptions.GrnNotFoundException, GrnExceptions.InvalidStateTransitionException {
        GRN grn = grnRepository.findById(id)
                .orElseThrow(() -> new GrnExceptions.GrnNotFoundException(id));

        State currentState = grn.getState() != null ? grn.getState() : State.OPEN;
        validateStateTransition(currentState, newState);

        grn.setState(newState);
        return grnMapper.toDto(grnRepository.save(grn));
    }

    public GrnItemDto changeItemState(Long id, State newState)
            throws GrnExceptions.GrnItemNotFoundException, GrnExceptions.InvalidStateTransitionException {
        GrnItem item = grnItemRepository.findById(id)
                .orElseThrow(() -> new GrnExceptions.GrnItemNotFoundException(id));

        State currentState = item.getState() != null ? item.getState() : State.OPEN;
        validateStateTransition(currentState, newState);

        item.setState(newState);
        return grnItemMapper.toDto(grnItemRepository.save(item));
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
    private void checkAssignedQuantity(GrnItem item) {
        int expected = item.getExpectedQty();
        List<CheckingInfo> checkingInfos = item.getCheckingInfoList();
        int assigned = (checkingInfos == null || checkingInfos.isEmpty()) ? 0 :
                checkingInfos.stream().mapToInt(CheckingInfo::getQuantity).sum();

        State currentState = item.getState() != null ? item.getState() : State.OPEN;
        if (assigned >= expected && currentState == State.OPEN) {
            log.info("Item {} complete → auto CHECKED", item.getId());
            item.setState(State.CHECKED);
            grnItemRepository.save(item);
        }
    }
}

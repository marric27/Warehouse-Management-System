package com.relatech.warehouse_management_system.goodsIn;

import com.relatech.warehouse_management_system.common.util.State;
import com.relatech.warehouse_management_system.goodsIn.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnDTO;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnItemService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnService;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnExceptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GrnItemStateService {

    private final GrnService grnService;
    private final GrnItemService grnItemService;

    // VALIDAZIONE QUANTITÀ
    public void validateItemQuantities(GrnItemDto item)
            throws GrnExceptions.InvalidQuantityException,
            GrnExceptions.QuantityMismatchException,
            GrnExceptions.OverReceivedQuantityException {

        int expected = item.getExpectedQty();
        int compliant = item.getCompliantQty();
        int notCompliant = item.getNotCompliantQty();
        int received = item.getReceivedQty();

        if (expected <= 0)
            throw new GrnExceptions.InvalidQuantityException("Expected qty must be > 0");

        if (received != compliant + notCompliant)
            throw new GrnExceptions.QuantityMismatchException(
                    "Received != compliant + notCompliant");

        if (received > expected)
            throw new GrnExceptions.OverReceivedQuantityException(
                    "Over-received: expected=" + expected + " received=" + received);
    }


    // PROGRESSIONE AUTOMATICA DI STATO
    @Transactional
    public void evaluateAndProgressItemState(GrnItemDto item)
            throws GrnExceptions.GrnItemNotFoundException,
            GrnExceptions.GrnNotFoundException {

        List<CheckingInfoDto> checks = item.getCheckingInfoList();
        int expected = item.getExpectedQty();
        int assigned = checks == null ? 0 : checks.stream().mapToInt(CheckingInfoDto::getQuantity).sum();

        State current = item.getState() == null ? State.OPEN : item.getState();

        // OPEN → CHECKED
        if (assigned >= expected && current == State.OPEN) {
            log.info("AUTO: Item {} → CHECKED (qty complete)", item.getId());
            item.setState(State.CHECKED);
            grnItemService.updateGrnItem(item.getId(), item);
            current = State.CHECKED;
        }

        // CHECKED → PUTAWAY
        if (current == State.CHECKED
                && checks != null
                && !checks.isEmpty()
                && checks.stream().allMatch(c -> c.getState() == State.PUTAWAY)) {

            log.info("AUTO: Item {} → PUTAWAY (all checks PUTAWAY)", item.getId());
            item.setState(State.PUTAWAY);
            grnItemService.updateGrnItem(item.getId(), item);

            evaluateAndProgressGrnState(item.getGrnId());
        }
    }


    // SE TUTTI GLI ITEMS SONO PUTAWAY → CHIUDERE GRN
    @Transactional
    public void evaluateAndProgressGrnState(Long grnId)
            throws GrnExceptions.GrnNotFoundException {

        GrnDTO grn = grnService.getGRNById(grnId);

        boolean allPutaway =
                grn.getItems().stream().allMatch(i -> i.getState() == State.PUTAWAY);

        if (allPutaway) {
            grn.setState(State.CLOSED);
            grnService.updateGRN(grnId, grn);
            log.info("AUTO: GRN {} → CLOSED (all items PUTAWAY)", grnId);
        }
    }
}

package com.relatech.warehouse_management_system.goodsIn;

import com.relatech.warehouse_management_system.common.util.State;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnDto;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnItemService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnService;
import com.relatech.warehouse_management_system.goodsIn.exception.*;
import com.relatech.warehouse_management_system.goodsIn.states.GrnItemStateHandler;
import com.relatech.warehouse_management_system.goodsIn.states.GrnItemStateHandlerResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GrnItemStateService {

    private final GrnService grnService;
    private final GrnItemService grnItemService;
    private final GrnItemStateHandlerResolver resolver;

    public boolean canAssignCheckingInfo(GrnItemDto item) {
        GrnItemStateHandler currentHandler = resolver.resolve(item.getState());
        return currentHandler.canAssignCheckingInfo(item);
    }

    // PROGRESSIONE AUTOMATICA DI STATO
    @Transactional(rollbackFor = {GrnItemNotFoundException.class, GrnNotFoundException.class})
    public void evaluateAndProgressItemState(GrnItemDto item) throws GrnItemNotFoundException, GrnNotFoundException {

        State currentState = item.getState();
        GrnItemStateHandler currentHandler = resolver.resolve(currentState);

        State nextState = currentHandler.onCheckingInfoAdded(item);
        if (nextState != currentState && currentHandler.canTransitionTo(nextState, item)) {
            item.setState(nextState);
            grnItemService.updateGrnItem(item.getId(), item);
            currentState = nextState;
        }

        currentHandler = resolver.resolve(currentState);
        nextState = currentHandler.onPutawayAssigned(item);
        if (nextState != currentState && currentHandler.canTransitionTo(nextState, item)) {
            item.setState(nextState);
            grnItemService.updateGrnItem(item.getId(), item);
        }

        if (item.getState() == State.PUTAWAY) {
            log.info("AUTO: Item {} -> PUTAWAY (all checks PUTAWAY)", item.getId());
            evaluateAndProgressGrnState(item.getGrnId());
        }
    }

    // SE TUTTI GLI ITEMS SONO PUTAWAY → CHIUDERE GRN
    @Transactional(rollbackFor = GrnNotFoundException.class)
    public void evaluateAndProgressGrnState(Long grnId) throws GrnNotFoundException {

        GrnDto grn = grnService.getGRNById(grnId);

        boolean allPutaway = grn.getItems().stream().allMatch(i -> i.getState() == State.PUTAWAY);

        if (allPutaway) {
            grn.setState(State.CLOSED);
            grnService.updateGRN(grnId, grn);
            log.info("AUTO: GRN {} -> CLOSED (all items PUTAWAY)", grnId);
        }
    }

    public boolean checkGrnIfClosed(Long grnID) throws GrnNotFoundException {
        return grnService.getGRNById(grnID).getState() == State.CLOSED;
    }
}

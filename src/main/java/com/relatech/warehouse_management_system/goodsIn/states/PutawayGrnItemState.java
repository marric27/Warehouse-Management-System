package com.relatech.warehouse_management_system.goodsIn.states;

import com.relatech.warehouse_management_system.common.util.State;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import org.springframework.stereotype.Component;

@Component
public class PutawayGrnItemState implements GrnItemStateHandler {

    @Override
    public State getState() {
        return State.PUTAWAY;
    }

    @Override
    public boolean canTransitionTo(State targetState, GrnItemDto item) {
        return targetState == State.PUTAWAY;
    }

    @Override
    public State onCheckingInfoAdded(GrnItemDto item) {
        return State.PUTAWAY;
    }

    @Override
    public State onPutawayAssigned(GrnItemDto item) {
        return State.PUTAWAY;
    }

    @Override
    public State onQuantitiesValidated(GrnItemDto item) {
        return State.PUTAWAY;
    }

    @Override
    public boolean canAssignCheckingInfo(GrnItemDto item) {
        return false;
    }
}

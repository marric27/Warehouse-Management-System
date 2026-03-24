package com.relatech.warehouse_management_system.goodsIn.states;

import com.relatech.warehouse_management_system.common.util.State;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import org.springframework.stereotype.Component;

@Component
public class CheckedGrnItemState implements GrnItemStateHandler {
    @Override
    public State getState() {
        return State.CHECKED;
    }

    @Override
    public boolean canTransitionTo(State targetState, GrnItemDto item) {
        return targetState == State.PUTAWAY
                && item.getCheckingInfoList() != null
                && !item.getCheckingInfoList().isEmpty()
                && item.getCheckingInfoList().stream().allMatch(checkingInfoDto -> checkingInfoDto.getState() == State.PUTAWAY);
    }

    @Override
    public State onCheckingInfoAdded(GrnItemDto item) {
        return canTransitionTo(State.PUTAWAY, item) ? State.PUTAWAY : State.CHECKED;
    }

    @Override
    public State onPutawayAssigned(GrnItemDto item) {
        return onCheckingInfoAdded(item);
    }
}

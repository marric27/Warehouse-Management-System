package com.relatech.warehouse_management_system.goodsIn.states;

import com.relatech.warehouse_management_system.common.util.State;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import org.springframework.stereotype.Component;

@Component
public class OpenGrnItemState implements GrnItemStateHandler {
    @Override
    public State getState() {
        return State.OPEN;
    }
    @Override
    public boolean canTransitionTo(State targetState, GrnItemDto item) {
        int assigned = 0;
        if (item.getCheckingInfoList() != null) {
            assigned = item.getCheckingInfoList().stream()
                    .mapToInt(ci -> ci.getQuantity())
                    .sum();
        }

        return targetState == State.CHECKED
                && assigned >= item.getReceivedQty()
                && item.getReceivedQty() > 0;
    }

    @Override
    public State onCheckingInfoAdded(GrnItemDto item) {
        return canTransitionTo(State.CHECKED, item) ? State.CHECKED : State.OPEN;
    }

    @Override
    public State onPutawayAssigned(GrnItemDto item) {
        return onCheckingInfoAdded(item);
    }
}

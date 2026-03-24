package com.relatech.warehouse_management_system.goodsIn.states;

import com.relatech.warehouse_management_system.common.util.State;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;

public interface GrnItemStateHandler {
    State getState();
    boolean canTransitionTo(State targetState, GrnItemDto item);
    State onCheckingInfoAdded(GrnItemDto item);
    State onPutawayAssigned(GrnItemDto item);
    State onQuantitiesValidated(GrnItemDto item);
    boolean canAssignCheckingInfo(GrnItemDto item);
}

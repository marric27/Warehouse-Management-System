package com.relatech.warehouse_management_system.goodsIn.events;

import com.relatech.warehouse_management_system.common.util.State;

public record GrnItemCheckingInfoAddedEvent(Long grnItemId, State oldState, State newState, Long grnId) {
}
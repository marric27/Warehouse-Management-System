package com.relatech.warehouse_management_system.goodsIn.events;

import com.relatech.warehouse_management_system.common.util.State;
import com.relatech.warehouse_management_system.goodsIn.GrnItemStateService;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnItemService;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnItemNotFoundException;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class GrnStateTransitionListener {

    private final GrnItemService grnItemService;
    private final GrnItemStateService grnItemStateService;

    @EventListener
    public void onGrnItemCheckingInfoAdded(GrnItemCheckingInfoAddedEvent event)
            throws GrnItemNotFoundException, GrnNotFoundException {
        handleProgression(event.grnItemId(), event.oldState(), event.newState(), event.grnId(), "checking-info-added");
    }

    @EventListener
    public void onGrnItemPutawayAssigned(GrnItemPutawayAssignedEvent event)
            throws GrnItemNotFoundException, GrnNotFoundException {
        handleProgression(event.grnItemId(), event.oldState(), event.newState(), event.grnId(), "putaway-assigned");
    }

    @EventListener
    public void onGrnItemQuantitiesValidated(GrnItemQuantitiesValidatedEvent event)
            throws GrnItemNotFoundException, GrnNotFoundException {
        handleProgression(event.grnItemId(), event.oldState(), event.newState(), event.grnId(), "quantities-validated");
    }

    private void handleProgression(Long grnItemId, State oldState, State newState, Long grnId, String trigger)
            throws GrnItemNotFoundException, GrnNotFoundException {
        log.info("event=grn_item_state_progression trigger={} grnItemId={} grnId={} oldState={} newState={}",
                trigger, grnItemId, grnId, oldState, newState);

        GrnItemDto item = grnItemService.getGrnItemById(grnItemId);
        grnItemStateService.evaluateAndProgressItemState(item);
    }
}
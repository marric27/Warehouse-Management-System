package com.relatech.warehouse_management_system.event;

import com.relatech.warehouse_management_system.checkingInfo.entity.CheckingInfo;
import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.grnItem.entity.GrnItem;
import com.relatech.warehouse_management_system.grnItem.repository.GrnItemRepository;
import com.relatech.warehouse_management_system.util.State;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckingInfoEventListener {

    private final GrnItemRepository grnItemRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCheckingInfoUpdate(CheckingInfoUpdatedEvent event) throws ResourceNotFoundException {
        log.info("Handling event CheckingInfoUpdatedEvent");
        CheckingInfo ci = event.checkingInfo();
        GrnItem grnItem = ci.getGrnItem();

        if (grnItem == null) return;

        grnItem = grnItemRepository.findByIdWithCheckingInfos(grnItem.getId())
                .orElseThrow(() -> new ResourceNotFoundException("GRN Item", 1L));

        boolean allPutaway = grnItem.getCheckingInfoList()
                .stream()
                .allMatch(info -> info.getState() == State.PUTAWAY);

        int totalAssigned = grnItem.getCheckingInfoList()
                .stream()
                .filter(info -> info.getStockUnit() != null)
                .mapToInt(CheckingInfo::getQuantity)
                .sum();

        if (allPutaway && grnItem.getState() != State.PUTAWAY) {
            grnItem.setState(State.PUTAWAY);
            log.info("GrnItem {} set to PUTAWAY", grnItem.getId());
        } else if (totalAssigned >= grnItem.getCompliantQty() && grnItem.getState() != State.CHECKED) {
            grnItem.setState(State.CHECKED);
            log.info("GrnItem {} set to CHECKED", grnItem.getId());
        } else {
            return;
        }

        grnItemRepository.save(grnItem);
        eventPublisher.publishEvent(new GrnItemStateUpdatedEvent(grnItem));
        log.info("Publish new event GrnItemStateUpdatedEvent");
    }
}
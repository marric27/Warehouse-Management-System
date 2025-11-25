package com.relatech.warehouse_management_system.event;

import com.relatech.warehouse_management_system.GRN.entity.GRN;
import com.relatech.warehouse_management_system.GRN.repository.GrnRepository;
import com.relatech.warehouse_management_system.grnItem.entity.GrnItem;
import com.relatech.warehouse_management_system.util.State;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class GrnItemEventListener {

    @Autowired
    private GrnRepository grnRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGrnItemStateUpdate(GrnItemStateUpdatedEvent event) {
        log.info("Handling event GrnItemStateUpdatedEvent");
        GrnItem grnItem = event.grnItem();
        GRN grn = grnItem.getGrn();

        grn = grnRepository.findByIdWithItems(grn.getId())
                .orElseThrow(() -> new RuntimeException("GRN not found"));

        boolean allPutaway = grn.getItems().stream()
                .allMatch(item -> item.getState() == State.PUTAWAY);

        if (allPutaway && grn.getState() != State.CLOSED) {
            grn.setState(State.CLOSED);
            grnRepository.saveAndFlush(grn);
            log.info("GRN {} set to CLOSED because all GRN items are PUTAWAY", grn.getId());
        }
    }
}

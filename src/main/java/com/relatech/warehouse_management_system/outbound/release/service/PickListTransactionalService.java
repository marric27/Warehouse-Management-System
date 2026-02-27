package com.relatech.warehouse_management_system.outbound.release.service;

import com.relatech.warehouse_management_system.common.util.OrderState;
import com.relatech.warehouse_management_system.outbound.dto.PickListDto;
import com.relatech.warehouse_management_system.outbound.entity.service.PickListService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PickListTransactionalService {

    private final EntityManager entityManager;
    private final PickListService pickListService;

    @Transactional
    public List<PickListDto> doTransactionalUpdate(
            List<Long> orderIds,
            Collection<PickListDto> pickLists) {

        int updated = entityManager.createQuery("""
            UPDATE Order o
               SET o.state = :newState
             WHERE o.id IN :ids
               AND o.state = :oldState
        """)
                .setParameter("newState", OrderState.PICKING)
                .setParameter("oldState", OrderState.OPEN)
                .setParameter("ids", orderIds)
                .executeUpdate();

        if (updated == 0)
            throw new IllegalStateException("Orders already processed by another request");

        return pickListService.createBulk(new ArrayList<>(pickLists));
    }
}
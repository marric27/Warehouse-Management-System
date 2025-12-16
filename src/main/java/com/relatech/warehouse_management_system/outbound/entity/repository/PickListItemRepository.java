package com.relatech.warehouse_management_system.outbound.entity.repository;

import com.relatech.warehouse_management_system.common.util.PickListItemState;
import com.relatech.warehouse_management_system.outbound.entity.PickListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PickListItemRepository extends JpaRepository<PickListItem, Long> {

    @Query("""
                SELECT pli
                FROM PickListItem pli
                WHERE pli.pickList.id IN :plIds
                  AND pli.state = :state
                ORDER BY pli.pickingSequence ASC, pli.slotCode ASC
            """)
    List<PickListItem> findOpenItemsOrdered(@Param("plIds") List<Long> plIds, @Param("state") PickListItemState state, Pageable pageable);
}

package com.relatech.warehouse_management_system.picking.service;

import com.relatech.warehouse_management_system.common.util.PickListItemState;
import com.relatech.warehouse_management_system.outbound.dto.PickListItemDto;
import com.relatech.warehouse_management_system.outbound.entity.service.PickListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class PickingService {

    private final PickListService pickListService;

    public PickListItemDto getNextPickListItem(List<Long> plIds) {
        if (plIds == null || plIds.isEmpty()) {
            return null;
        }
        Pageable limitOne = PageRequest.of(0, 1); // get first result
        List<PickListItemDto> result = pickListService.findOpenItemsOrdered(plIds, PickListItemState.OPEN, limitOne);

        if (result.isEmpty()) {
            log.info("Nessun PickListItem OPEN trovato");
            return null;
        }

        return result.getFirst();
    }


}

package com.relatech.warehouse_management_system.goodsIn.entity.service;

import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnItemNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GrnItemService {

    GrnItemDto createGrnItem(GrnItemDto dto);

    List<GrnItemDto> getAllGrnItems();
    Page<GrnItemDto> getAllGrnItemsPaged(Pageable pageable);
    GrnItemDto getGrnItemById(Long id) throws GrnItemNotFoundException;
    GrnItemDto getGrnItemByCode(String code) throws GrnItemNotFoundException;
    GrnItemDto updateGrnItem(Long id, GrnItemDto grnItemDto) throws GrnItemNotFoundException;
    void deleteGrnItem(Long id) throws GrnItemNotFoundException;
    void addCheckingInfo(Long grnItemId, Long checkingInfoId) throws Exception;
}

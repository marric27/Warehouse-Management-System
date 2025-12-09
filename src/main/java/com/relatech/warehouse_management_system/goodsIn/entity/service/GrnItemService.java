package com.relatech.warehouse_management_system.goodsIn.entity.service;

import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnItemNotFoundException;

import java.util.List;

public interface GrnItemService {

    GrnItemDto createGrnItem(GrnItemDto dto);

    List<GrnItemDto> getAllGrnItems();
    GrnItemDto getGrnItemById(Long id) throws GrnItemNotFoundException;
    GrnItemDto getGrnItemByCode(String code) throws GrnItemNotFoundException;
    GrnItemDto updateGrnItem(Long id, GrnItemDto grnItemDto) throws GrnItemNotFoundException;
    void deleteGrnItem(Long id) throws GrnItemNotFoundException;
    void addCheckingInfo(Long grnItemId, Long checkingInfoId) throws Exception;
}

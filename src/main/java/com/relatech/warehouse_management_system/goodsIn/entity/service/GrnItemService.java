package com.relatech.warehouse_management_system.goodsIn.entity.service;

import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnExceptions;

import java.util.List;

public interface GrnItemService {

    GrnItemDto createGrnItem(GrnItemDto dto) throws GrnExceptions.DuplicateGrnCodeException;

    List<GrnItemDto> getAllGrnItems();
    GrnItemDto getGrnItemById(Long id) throws GrnExceptions.GrnItemNotFoundException;
    GrnItemDto getGrnItemByCode(String code) throws GrnExceptions.GrnItemNotFoundException;
    GrnItemDto updateGrnItem(Long id, GrnItemDto grnItemDto) throws GrnExceptions.GrnItemNotFoundException;
    void deleteGrnItem(Long id) throws GrnExceptions.GrnItemNotFoundException;
    // Opzionale: List<GrnItemDto> findByGrnId(Long grnId);
}

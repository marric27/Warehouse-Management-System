package com.relatech.warehouse_management_system.goodsIn.entity.service;

import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;

import java.util.List;

public interface GrnItemService {


        List<GrnItemDto> getAllGrnItems();
        GrnItemDto getGrnItemById(Long id) throws ResourceNotFoundException;
        GrnItemDto getGrnItemByCode(String code) throws ResourceNotFoundException;
        GrnItemDto updateGrnItem(Long id, GrnItemDto grnItemDto) throws ResourceNotFoundException;
        void deleteGrnItem(Long id) throws ResourceNotFoundException;
        // Opzionale: List<GrnItemDto> findByGrnId(Long grnId);
    }



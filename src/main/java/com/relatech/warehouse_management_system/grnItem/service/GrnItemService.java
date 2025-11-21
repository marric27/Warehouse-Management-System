package com.relatech.warehouse_management_system.grnItem.service;

import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.grnItem.dto.GrnItemDto;

import java.util.List;

public interface GrnItemService {

    GrnItemDto createGrnItem(GrnItemDto grnItemDto);
    List<GrnItemDto> getAllGrnItems();
    GrnItemDto getGrnItemById(Long id) throws ResourceNotFoundException;
    GrnItemDto updateGrnItem(Long id, GrnItemDto grnItemDto) throws ResourceNotFoundException;
    void deleteGrnItem(Long id) throws ResourceNotFoundException;
}

package com.relatech.warehouse_management_system.stockUnit.service;

import java.util.List;

import com.relatech.warehouse_management_system.exception.DuplicateResourceException;
import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.stockUnit.dto.StockUnitDTO;

public interface StockUnitService {
    StockUnitDTO createStockUnit(StockUnitDTO dto) throws DuplicateResourceException;
    StockUnitDTO getStockUnitById(Long id) throws ResourceNotFoundException;
    List<StockUnitDTO> getAllStockUnits();
    StockUnitDTO updateStockUnit(Long id,StockUnitDTO dto) throws ResourceNotFoundException;
    void deleteStockUnit(Long id) throws ResourceNotFoundException;
    StockUnitDTO assignProductToStockUnit(Long stockUnitId, Long slotId) throws ResourceNotFoundException;
    StockUnitDTO removeProductFromStockUnit(Long stockUnitId) throws ResourceNotFoundException;
}

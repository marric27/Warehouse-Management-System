package com.relatech.warehouse_management_system.goodsIn.entity.service;

import java.util.List;

import com.relatech.warehouse_management_system.common.exception.DuplicateResourceException;
import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StockUnitService {
    StockUnitDTO createStockUnit(StockUnitDTO dto) throws DuplicateResourceException;
    StockUnitDTO getStockUnitById(Long id) throws ResourceNotFoundException;
    List<StockUnitDTO> getAllStockUnits();
    Page<StockUnitDTO> getAllStockUnitsPaged(Pageable pageable);
    StockUnitDTO updateStockUnit(Long id,StockUnitDTO dto) throws ResourceNotFoundException;
    void deleteStockUnit(Long id) throws ResourceNotFoundException;
    StockUnitDTO assignProductToStockUnit(Long stockUnitId, Long slotId) throws ResourceNotFoundException;
    StockUnitDTO removeProductFromStockUnit(Long stockUnitId) throws ResourceNotFoundException;
}

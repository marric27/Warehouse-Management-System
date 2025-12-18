package com.relatech.warehouse_management_system.goodsIn.entity.service;

import com.relatech.warehouse_management_system.common.exception.DuplicateResourceException;
import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StockUnitService {
    StockUnitDto createStockUnit(StockUnitDto dto) throws DuplicateResourceException;
    StockUnitDto getStockUnitById(Long id) throws ResourceNotFoundException;
    List<StockUnitDto> getAllStockUnits();
    Page<StockUnitDto> getAllStockUnitsPaged(Pageable pageable);
    StockUnitDto updateStockUnit(Long id, StockUnitDto dto) throws ResourceNotFoundException;
    void deleteStockUnit(Long id) throws ResourceNotFoundException;
    StockUnitDto assignProductToStockUnit(Long stockUnitId, Long slotId) throws ResourceNotFoundException;
    StockUnitDto removeProductFromStockUnit(Long stockUnitId) throws ResourceNotFoundException;

    StockUnitDto updateQuantity(String stockUnitCode, int i) throws ResourceNotFoundException;
}

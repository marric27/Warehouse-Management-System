package com.relatech.warehouse_management_system.stockUnit.service;

import com.relatech.warehouse_management_system.exception.DuplicateResourceException;
import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.slot.entity.Slot;
import com.relatech.warehouse_management_system.slot.repository.SlotRepository;
import com.relatech.warehouse_management_system.stockUnit.dto.StockUnitDTO;
import com.relatech.warehouse_management_system.stockUnit.entity.StockUnit;
import com.relatech.warehouse_management_system.stockUnit.mapper.StockUnitMapper;
import com.relatech.warehouse_management_system.stockUnit.repository.StockUnitRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockUnitServiceImpl implements StockUnitService{

    private final StockUnitRepository stockUnitRepository;
    private final SlotRepository slotRepository;
    private final StockUnitMapper stockUnitMapper;

    @Override
    public StockUnitDTO createStockUnit(StockUnitDTO dto) throws DuplicateResourceException, ValidationException {
        if (stockUnitRepository.findByUniqueCode(dto.getUniqueCode()).isPresent()) {
            throw new DuplicateResourceException("StockUnit", "uniqueCode", dto.getUniqueCode());
        }
        Set<Slot> slots = slotRepository.findAllById(dto.getSlotIds()).stream().collect(Collectors.toSet());

        for (Slot s : slots) {
            if (!s.getAllowedCategory().equals(dto.getProductCategory())) {
                throw new ValidationException(
                        "StockUnit category " + dto.getProductCategory() +
                                " not allowed in Slot " + s.getId() +
                                " (allowed: " + s.getAllowedCategory() + ")"
                );
            }
        }
        StockUnit stockUnit = stockUnitMapper.toEntity(dto, slots);
        StockUnit saved = stockUnitRepository.save(stockUnit);
        return stockUnitMapper.toDTO(saved);
    }

    @Override
    public StockUnitDTO getStockUnitById(Long id) throws ResourceNotFoundException {
        StockUnit stockUnit = stockUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockUnit", id));
        return stockUnitMapper.toDTO(stockUnit);
    }

    @Override
    public List<StockUnitDTO> getAllStockUnits() {
        return stockUnitRepository.findAll()
                .stream().map(stockUnitMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public StockUnitDTO updateStockUnit(Long id, StockUnitDTO dto) throws ResourceNotFoundException, ValidationException {
        StockUnit existing = stockUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockUnit", id));
        Set<Slot> slots = slotRepository.findAllById(dto.getSlotIds()).stream().collect(Collectors.toSet());
        for (Slot s : slots) {
            if (!s.getAllowedCategory().equals(dto.getProductCategory())) {
                throw new ValidationException("StockUnit category " + dto.getProductCategory() +
                        " not allowed in Slot " + s.getId() +
                        " (allowed: " + s.getAllowedCategory() + ")");
            }
        }
        existing.setBatchNumber(dto.getBatchNumber());
        existing.setExpirationDate(dto.getExpirationDate());
        existing.setProductCode(dto.getProductCode());
        existing.setUniqueCode(dto.getUniqueCode());
        existing.setQuantity(dto.getQuantity());
        existing.setProductCategory(dto.getProductCategory());
        existing.setSlots(slots);
        StockUnit saved = stockUnitRepository.save(existing);
        return stockUnitMapper.toDTO(saved);
    }

    @Override
    public void deleteStockUnit(Long id) throws ResourceNotFoundException {
        StockUnit stockUnit = stockUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockUnit", id));
        stockUnitRepository.delete(stockUnit);
    }
}

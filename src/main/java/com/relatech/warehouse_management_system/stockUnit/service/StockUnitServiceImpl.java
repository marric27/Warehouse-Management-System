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
<<<<<<< Updated upstream
import lombok.extern.log4j.Log4j;
=======
>>>>>>> Stashed changes
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.List;
<<<<<<< Updated upstream
import java.util.Set;
import java.util.stream.Collectors;
=======
>>>>>>> Stashed changes

@Service
@Slf4j
@RequiredArgsConstructor
public class StockUnitServiceImpl implements StockUnitService {

    private final StockUnitRepository stockUnitRepository;
    private final SlotRepository slotRepository;
    private final StockUnitMapper stockUnitMapper;

    @Override
    public StockUnitDTO createStockUnit(StockUnitDTO dto) throws DuplicateResourceException, ValidationException {
        if (stockUnitRepository.findByUniqueCode(dto.getUniqueCode()).isPresent()) {
            throw new DuplicateResourceException("StockUnit", "uniqueCode", dto.getUniqueCode());
        }

        Long slotId = dto.getSlotId();
        if (slotId == null) {
            throw new ValidationException("Slot ID is required");
        }
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ValidationException("Slot not found with id " + slotId));


        dto.setProductCategory(slot.getProductCategory());

        StockUnit stockUnit = stockUnitMapper.toEntity(dto, slot);
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
                .stream().map(stockUnitMapper::toDTO).toList();
    }

    @Override
    public StockUnitDTO updateStockUnit(Long id, StockUnitDTO dto) throws ResourceNotFoundException, ValidationException {
        StockUnit existing = stockUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockUnit", id));

        Long slotId = dto.getSlotId();
        if (slotId == null) {
            throw new ValidationException("Slot ID is required");
        }
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ValidationException("Slot not found with id " + slotId));


        dto.setProductCategory(slot.getProductCategory());

        existing.setBatchNumber(dto.getBatchNumber());
        existing.setExpirationDate(dto.getExpirationDate());
        existing.setUniqueCode(dto.getUniqueCode());
        existing.setQuantity(dto.getQuantity());
        existing.setProductCategory(dto.getProductCategory());
        existing.setSlot(slot);

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

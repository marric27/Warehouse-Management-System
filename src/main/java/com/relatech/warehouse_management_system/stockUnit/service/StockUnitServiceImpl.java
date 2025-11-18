package com.relatech.warehouse_management_system.stockUnit.service;

import com.relatech.warehouse_management_system.exception.DuplicateResourceException;
import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.product.repository.ProductRepository;
import com.relatech.warehouse_management_system.slot.entity.Slot;
import com.relatech.warehouse_management_system.slot.repository.SlotRepository;
import com.relatech.warehouse_management_system.stockUnit.dto.StockUnitDTO;
import com.relatech.warehouse_management_system.stockUnit.entity.StockUnit;
import com.relatech.warehouse_management_system.stockUnit.mapper.StockUnitMapper;
import com.relatech.warehouse_management_system.stockUnit.repository.StockUnitRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;



import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockUnitServiceImpl implements StockUnitService {

    private final StockUnitRepository stockUnitRepository;
    private final SlotRepository slotRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public StockUnitDTO createStockUnit(StockUnitDTO dto) throws DuplicateResourceException, ValidationException {
//        StockUnit existingStockUnit = stockUnitRepository.findByUniqueCode(dto.getUniqueCode())
//                .orElseThrow(() -> new DuplicateResourceException("StockUnit", "uniqueCode", dto.getUniqueCode()));

//        Long slotId = dto.getSlotId();
//        if (slotId == null) {
//            throw new ValidationException("Slot ID is required");
//        }
//        Slot slot = slotRepository.findById(slotId)
//                .orElseThrow(() -> new ValidationException("Slot not found with id " + slotId));


        //dto.setCategory(slot.getAllowedCategory());

        //StockUnit stockUnit = stockUnitMapper.toEntity(dto, slot);
        StockUnit saved = stockUnitRepository.save(StockUnitMapper.toEntity(dto));
        return StockUnitMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public StockUnitDTO getStockUnitById(Long id) throws ResourceNotFoundException {
        StockUnit stockUnit = stockUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockUnit", id));
        return StockUnitMapper.toDTO(stockUnit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockUnitDTO> getAllStockUnits() {
        return stockUnitRepository.findAll()
                .stream().map(StockUnitMapper::toDTO).toList();
    }

    @Override
    @Transactional
    public StockUnitDTO updateStockUnit(Long id, StockUnitDTO dto) throws ResourceNotFoundException, ValidationException {
        StockUnit existing = stockUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockUnit", id));

        Long slotId = null;//dto.getSlotId();TODO
        if (slotId == null) {
            throw new ValidationException("Slot ID is required");
        }
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ValidationException("Slot not found with id " + slotId));


        dto.setCategory(slot.getAllowedCategory());

        existing.setBatchNumber(dto.getBatchNumber());
        existing.setExpirationDate(dto.getExpirationDate());
        existing.setUniqueCode(dto.getUniqueCode());
        existing.setQuantity(dto.getQuantity());
        existing.setCategory(dto.getCategory());
        existing.setSlot(slot);

        StockUnit saved = stockUnitRepository.save(existing);
        return StockUnitMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public void deleteStockUnit(Long id) throws ResourceNotFoundException {
        StockUnit stockUnit = stockUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockUnit", id));
        stockUnitRepository.delete(stockUnit);
    }

    @Override
    @Transactional
    public StockUnitDTO assignProductToStockUnit(Long stockUnitId, Long productId) throws ResourceNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        StockUnit su = stockUnitRepository.findById(stockUnitId)
                .orElseThrow(() -> new ResourceNotFoundException("StockUnit", stockUnitId));

        su.addProduct(product);
        return StockUnitMapper.toDTO(stockUnitRepository.save(su));
    }

    @Transactional
    @Override
    public StockUnitDTO removeProductFromStockUnit(Long stockUnitId, Long productId) throws ResourceNotFoundException {
        StockUnit stockUnit = stockUnitRepository.findById(stockUnitId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock Unit", stockUnitId));
        stockUnit.setProduct(null);
        return StockUnitMapper.toDTO(stockUnitRepository.save(stockUnit));
    }


}

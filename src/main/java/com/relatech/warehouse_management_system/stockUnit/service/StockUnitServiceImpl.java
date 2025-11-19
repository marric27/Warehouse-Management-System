package com.relatech.warehouse_management_system.stockUnit.service;

import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.product.repository.ProductRepository;
import com.relatech.warehouse_management_system.slot.repository.SlotRepository;
import com.relatech.warehouse_management_system.stockUnit.dto.StockUnitDTO;
import com.relatech.warehouse_management_system.stockUnit.entity.StockUnit;
import com.relatech.warehouse_management_system.stockUnit.mapper.StockUnitMapper;
import com.relatech.warehouse_management_system.stockUnit.repository.StockUnitRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class StockUnitServiceImpl implements StockUnitService {

    private final StockUnitRepository stockUnitRepository;
    private final SlotRepository slotRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(rollbackFor = {ValidationException.class, RuntimeException.class})
    public StockUnitDTO createStockUnit(StockUnitDTO dto) throws ValidationException {
        StockUnit saved = stockUnitRepository.save(StockUnitMapper.toEntity(dto));
        return StockUnitMapper.toDTO(saved);
    }

    @Override
    public StockUnitDTO getStockUnitById(Long id) throws ResourceNotFoundException {
        StockUnit stockUnit = stockUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockUnit", id));
        return StockUnitMapper.toDTO(stockUnit);
    }

    @Override
    public List<StockUnitDTO> getAllStockUnits() {
        return stockUnitRepository.findAll()
                .stream().map(StockUnitMapper::toDTO).toList();
    }

    @Override
    public Page<StockUnitDTO> getAllStockUnitsPaged(Pageable pageable) {
        return stockUnitRepository.findAll(pageable).map(StockUnitMapper::toDTO);
    }

    @Override
    @Transactional(rollbackFor = {ValidationException.class, ResourceNotFoundException.class, RuntimeException.class})
    public StockUnitDTO updateStockUnit(Long id, StockUnitDTO dto) throws ResourceNotFoundException, ValidationException {
        StockUnit existing = stockUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockUnit", id));

        existing.setBatchNumber(dto.getBatchNumber());
        existing.setExpirationDate(dto.getExpirationDate());
        existing.setUniqueCode(dto.getUniqueCode());
        existing.setQuantity(dto.getQuantity());
        existing.setCategory(dto.getCategory());

        StockUnit saved = stockUnitRepository.save(existing);
        return StockUnitMapper.toDTO(saved);
    }

    @Override
    @Transactional(rollbackFor = {ResourceNotFoundException.class, RuntimeException.class})
    public void deleteStockUnit(Long id) throws ResourceNotFoundException {
        StockUnit stockUnit = stockUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockUnit", id));
        stockUnitRepository.delete(stockUnit);
    }

    @Override
    @Transactional(rollbackFor = {ResourceNotFoundException.class, RuntimeException.class})
    public StockUnitDTO assignProductToStockUnit(Long stockUnitId, Long productId) throws ResourceNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        StockUnit su = stockUnitRepository.findById(stockUnitId)
                .orElseThrow(() -> new ResourceNotFoundException("StockUnit", stockUnitId));

        su.addProduct(product);
        return StockUnitMapper.toDTO(stockUnitRepository.save(su));
    }

    @Override
    @Transactional(rollbackFor = {ResourceNotFoundException.class, RuntimeException.class})
    public StockUnitDTO removeProductFromStockUnit(Long stockUnitId) throws ResourceNotFoundException {
        StockUnit stockUnit = stockUnitRepository.findById(stockUnitId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock Unit", stockUnitId));
        stockUnit.setProduct(null);
        return StockUnitMapper.toDTO(stockUnitRepository.save(stockUnit));
    }
}

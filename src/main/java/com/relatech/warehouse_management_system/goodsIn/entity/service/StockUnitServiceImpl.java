package com.relatech.warehouse_management_system.goodsIn.entity.service;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDto;
import com.relatech.warehouse_management_system.goodsIn.entity.StockUnit;
import com.relatech.warehouse_management_system.goodsIn.entity.mapper.StockUnitMapper;
import com.relatech.warehouse_management_system.goodsIn.entity.repository.StockUnitRepository;
import com.relatech.warehouse_management_system.product.ProductMirrorRepository;
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
    private final ProductMirrorRepository productRepository;
    private final StockUnitMapper stockUnitMapper;

    @Override
    @Transactional(rollbackFor = {ValidationException.class, RuntimeException.class})
    public StockUnitDto createStockUnit(StockUnitDto dto) throws ValidationException {
        StockUnit saved = stockUnitRepository.save(stockUnitMapper.toEntity(dto));
        return stockUnitMapper.toDTO(saved);
    }

    @Override
    public StockUnitDto getStockUnitById(Long id) throws ResourceNotFoundException {
        StockUnit stockUnit = stockUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockUnit", id));
        return stockUnitMapper.toDTO(stockUnit);
    }

    @Override
    public List<StockUnitDto> getAllStockUnits() {
        return stockUnitRepository.findAll()
                .stream().map(stockUnitMapper::toDTO).toList();
    }

    @Override
    public Page<StockUnitDto> getAllStockUnitsPaged(Pageable pageable) {
        return stockUnitRepository.findAll(pageable).map(stockUnitMapper::toDTO);
    }

    @Override
    @Transactional(rollbackFor = {ValidationException.class, ResourceNotFoundException.class, RuntimeException.class})
    public StockUnitDto updateStockUnit(Long id, StockUnitDto dto) throws ResourceNotFoundException, ValidationException {
        StockUnit existing = stockUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockUnit", id));

        existing.setBatchNumber(dto.getBatchNumber());
        existing.setExpirationDate(dto.getExpirationDate());
        existing.setQuantity(dto.getQuantity());
        existing.setCategory(dto.getCategory());

        StockUnit saved = stockUnitRepository.save(existing);
        return stockUnitMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public StockUnitDto getStockUnitByCode(String code) throws ResourceNotFoundException {
        StockUnit stockUnit = stockUnitRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("StockUnit", code));
        return stockUnitMapper.toDTO(stockUnit);
    }


    @Override
    @Transactional(rollbackFor = {ResourceNotFoundException.class, RuntimeException.class})
    public void deleteStockUnit(Long id) throws ResourceNotFoundException {
        StockUnit stockUnit = stockUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockUnit", id));
        stockUnitRepository.delete(stockUnit);
    }

//    @Override
//    @Transactional(rollbackFor = {ResourceNotFoundException.class, RuntimeException.class})
//    public StockUnitDto assignProductToStockUnit(Long stockUnitId, Long productId) throws ResourceNotFoundException {
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
//        StockUnit su = stockUnitRepository.findById(stockUnitId)
//                .orElseThrow(() -> new ResourceNotFoundException("StockUnit", stockUnitId));
//
//        su.addProduct(product);
//        return stockUnitMapper.toDTO(stockUnitRepository.save(su));
//    }

//    @Override
//    @Transactional(rollbackFor = {ResourceNotFoundException.class, RuntimeException.class})
//    public StockUnitDto removeProductFromStockUnit(Long stockUnitId) throws ResourceNotFoundException {
//        StockUnit stockUnit = stockUnitRepository.findById(stockUnitId)
//                .orElseThrow(() -> new ResourceNotFoundException("Stock Unit", stockUnitId));
//        stockUnit.setProduct(null);
//        return stockUnitMapper.toDTO(stockUnitRepository.save(stockUnit));
//    }
}

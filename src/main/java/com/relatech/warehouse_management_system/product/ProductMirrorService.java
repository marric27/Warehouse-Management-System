package com.relatech.warehouse_management_system.product;

import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.product.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductMirrorService {

    private final ProductMirrorRepository productMirrorRepository;

    @Transactional(readOnly = true)
    public void validateProductExists(String productCode) throws ResourceNotFoundException {
        if (!productMirrorRepository.existsByCode(productCode)) {
            throw new ResourceNotFoundException("Product", productCode);
        }
    }

    public ProductDto getProductByCode(String productCode) throws ResourceNotFoundException {
        return productMirrorRepository.findByCode(productCode)
                .map(ProductMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productCode));
    }

    public List<ProductDto> getAll() {
        return productMirrorRepository.findAll().stream().map(ProductMapper::toDto).toList();
    }
}

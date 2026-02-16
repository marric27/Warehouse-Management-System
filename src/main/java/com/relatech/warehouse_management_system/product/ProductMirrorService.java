package com.relatech.warehouse_management_system.product;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

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

    public ProductMirror getProductByCode(String productCode) throws ResourceNotFoundException {
        return productMirrorRepository.findByCode(productCode)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productCode));
    }
}

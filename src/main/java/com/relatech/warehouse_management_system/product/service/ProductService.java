package com.relatech.warehouse_management_system.product.service;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnDTO;
import com.relatech.warehouse_management_system.product.dto.ProductDTO;
import com.relatech.warehouse_management_system.common.util.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    ProductDTO getProductById(Long id) throws ResourceNotFoundException;
    ProductDTO getProductByCode(String code) throws ResourceNotFoundException;
    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO updateProduct(Long id, ProductDTO productDTO) throws Exception;
    void deleteProduct(Long id) throws ResourceNotFoundException;
    List<ProductDTO> getAllProducts();
    Page<ProductDTO> getAllProductsPaged(Pageable pageable);
    List<ProductDTO> getAllProductByProductCategory(Category category);
}

package com.relatech.warehouse_management_system.product.service;

import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.product.dto.ProductDTO;
import com.relatech.warehouse_management_system.util.Category;

import java.util.List;

public interface ProductService {
    ProductDTO getProductById(Long id) throws ResourceNotFoundException;
    ProductDTO getProductByCode(String code) throws ResourceNotFoundException;
    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO updateProduct(Long id, ProductDTO productDTO) throws Exception;
    void deleteProduct(Long id) throws ResourceNotFoundException;
    List<ProductDTO> getAllProducts();
    List<ProductDTO> getAllProductByProductCategory(Category category);
}

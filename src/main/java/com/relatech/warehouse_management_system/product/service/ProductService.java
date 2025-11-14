package com.relatech.warehouse_management_system.product.service;

import com.relatech.warehouse_management_system.exception.EntityNotFoundException;
import com.relatech.warehouse_management_system.product.dto.ProductDTO;
import com.relatech.warehouse_management_system.util.ProductCategory;

import java.util.List;

public interface ProductService {
    ProductDTO getProductById(Long id) throws EntityNotFoundException;
    ProductDTO getProductByCode(String code) throws EntityNotFoundException;
    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO updateProduct(Long id, ProductDTO productDTO) throws Exception;
    void deleteProduct(Long id) throws EntityNotFoundException;
    List<ProductDTO> getAllProducts();
    List<ProductDTO> getAllProductByProductCategory(ProductCategory productCategory);
}

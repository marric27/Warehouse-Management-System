package com.relatech.warehouse_management_system.product.service;

import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.product.dto.ProductDTO;
import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.product.mapper.ProductMapper;
import com.relatech.warehouse_management_system.product.repository.ProductRepository;
import com.relatech.warehouse_management_system.util.Category;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ProductDTO getProductById(Long id) throws ResourceNotFoundException {
        return productRepository.findById(id)
                .map(ProductMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(this.getClass().getName(), id));
    }

    @Override
    public ProductDTO getProductByCode(String code) throws ResourceNotFoundException {
        return productRepository.findByCode(code)
                .map(ProductMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(this.getClass().getName(), code));
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = ProductMapper.toEntity(productDTO);
        return ProductMapper.toDto(productRepository.save(product));
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) throws Exception {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(this.getClass().getName(), id));


        existing.setCode(productDTO.getCode());
        existing.setName(productDTO.getName());
        existing.setNationalCode(productDTO.getNationalCode());

        return ProductMapper.toDto(productRepository.save(existing));
    }

    @Override
    public void deleteProduct(Long id) throws ResourceNotFoundException {
        if(!productRepository.existsById(id)) throw new ResourceNotFoundException(this.getClass().getName(), id);

        productRepository.deleteById(id);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getAllProductByProductCategory(Category category) {
        return productRepository.findByCategory(category)
                .stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }
}

package com.relatech.warehouse_management_system.product.service;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.product.dto.ProductDto;
import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.product.mapper.ProductMapper;
import com.relatech.warehouse_management_system.product.repository.ProductRepository;
import com.relatech.warehouse_management_system.common.util.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) throws ResourceNotFoundException {
        return productRepository.findById(id)
                .map(ProductMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductByCode(String code) throws ResourceNotFoundException {
        return productRepository.findByCode(code)
                .map(ProductMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Product", code));
    }

    @Override
    @Transactional
    public ProductDto createProduct(ProductDto productDTO) {
        Product product = ProductMapper.toEntity(productDTO);
        return ProductMapper.toDto(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductDto updateProduct(Long id, ProductDto productDTO) throws Exception {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        existing.setName(productDTO.getName());

        return ProductMapper.toDto(productRepository.save(existing));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) throws ResourceNotFoundException {
        if(!productRepository.existsById(id)) throw new ResourceNotFoundException("Product", id);

        productRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductMapper::toDto).toList();
    }

    @Override
    public Page<ProductDto> getAllProductsPaged(Pageable pageable) {
        log.debug("Fetching paginated GRNs: page {}, size {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(ProductMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getAllProductByProductCategory(Category category) {
        return productRepository.findByCategory(category)
                .stream()
                .map(ProductMapper::toDto).toList();
    }
}

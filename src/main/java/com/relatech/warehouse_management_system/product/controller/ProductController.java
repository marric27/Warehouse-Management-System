package com.relatech.warehouse_management_system.product.controller;


import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.product.dto.ProductDto;
import com.relatech.warehouse_management_system.product.service.ProductService;
import com.relatech.warehouse_management_system.common.util.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/products")
@Tag(name = "Product Management", description = "APIs for managing products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) throws ResourceNotFoundException {
        log.info("Received GET request for product with ID: {}", id);
        ProductDto product = productService.getProductById(id);
        log.info("Returning product: {}", product);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ProductDto> getProductByCode(@PathVariable String code) throws ResourceNotFoundException {
        log.info("Received GET request for product with code: {}", code);
        ProductDto product = productService.getProductByCode(code);
        log.info("Returning product: {}", product);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        log.info("Received GET request for all products");
        List<ProductDto> products = productService.getAllProducts();
        log.info("Returning products: {}", products);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/paged")
    @Operation(summary = "List Stock Units paginated")
    public ResponseEntity<Page<ProductDto>> listStockUnitsPaged(Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProductsPaged(pageable));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(@PathVariable Category category) {
        log.info("Received GET request for product with category: {}", category);
        List<ProductDto> products = productService.getAllProductByProductCategory(category);
        log.info("Returning products: {}", products);
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDTO) {
        log.info("Received POST request to create product : {}", productDTO);
        ProductDto created = productService.createProduct(productDTO);
        log.info("Product created with ID: {}", created);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDto productDTO) throws Exception {
        log.info("Received PUT request to update product with ID: {}", id);
        ProductDto updated = productService.updateProduct(id, productDTO);
        log.info("Product updated: {} (ID: {})", updated.getName(), id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) throws ResourceNotFoundException {
        log.info("Received DELETE request for product with ID: {}", id);
        productService.deleteProduct(id);
        log.info("Product with ID: {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

}

package com.relatech.warehouse_management_system.product.controller;


import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.product.dto.ProductDTO;
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
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) throws ResourceNotFoundException {
        log.info("Received GET request for product with ID: {}", id);
        ProductDTO product = productService.getProductById(id);
        log.info("Returning product: {}", product);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ProductDTO> getProductByCode(@PathVariable String code) throws ResourceNotFoundException {
        log.info("Received GET request for product with code: {}", code);
        ProductDTO product = productService.getProductByCode(code);
        log.info("Returning product: {}", product);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        log.info("Received GET request for all products");
        List<ProductDTO> products = productService.getAllProducts();
        log.info("Returning products: {}", products);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/paged")
    @Operation(summary = "List Stock Units paginated")
    public ResponseEntity<Page<ProductDTO>> listStockUnitsPaged(Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProductsPaged(pageable));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Category category) {
        log.info("Received GET request for product with category: {}", category);
        List<ProductDTO> products = productService.getAllProductByProductCategory(category);
        log.info("Returning products: {}", products);
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        log.info("Received POST request to create product : {}", productDTO);
        ProductDTO created = productService.createProduct(productDTO);
        log.info("Product created with ID: {}", created);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) throws Exception {
        log.info("Received PUT request to update product with ID: {}", id);
        ProductDTO updated = productService.updateProduct(id, productDTO);
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

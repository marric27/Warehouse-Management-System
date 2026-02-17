package com.relatech.warehouse_management_system.product;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.product.entity.ProductDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Product mirror Management")
public class ProductMirrorController {
    private final ProductMirrorService productMirrorService;

    @GetMapping("/all")
    @Operation(summary = "List all products", description = "Returns all warehouse Products")
    @ApiResponse(responseCode = "200", description = "Products retrieved")
    public ResponseEntity<List<ProductDto>> listProds() {
        log.info("GET /all - listing all products");
        return ResponseEntity.ok(productMirrorService.getAll());
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get Product by code")
    @ApiResponse(responseCode = "200", description = "Product found")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<ProductDto> getProduct(@PathVariable String code) throws ResourceNotFoundException {
        log.info("GET /Products/{} - fetching Product", code);
        return ResponseEntity.ok(productMirrorService.getProductByCode(code));
    }
}

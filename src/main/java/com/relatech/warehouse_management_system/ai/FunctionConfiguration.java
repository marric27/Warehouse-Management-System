package com.relatech.warehouse_management_system.ai;


import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.common.util.Category;
import com.relatech.warehouse_management_system.product.dto.ProductDto;
import com.relatech.warehouse_management_system.product.service.ProductService;
import lombok.AllArgsConstructor;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

@Slf4j
@AllArgsConstructor
@Configuration
public class FunctionConfiguration {
    private final ProductService productService;

    public record ProductCode(String code) {}
    public record ProductDetails(long id, String code, String name, Category category){}

    @Bean
    @Description("Get product details by name")
    public Function<ProductCode, ProductDetails> getProductDetails() {
        return productCode -> {
            ProductDto product = null;
            try {
                product = productService.getProductByCode(productCode.code());
                log.info("product details for product code {}", productCode);
            } catch (ResourceNotFoundException e) {
                throw new RuntimeException(e);
            }
            if (product != null) {
                return new ProductDetails(product.getId(), product.getCode(), product.getName(), product.getCategory());
            } else {
                return new ProductDetails(0, "Not Found", null, null);
            }
        };
    }
}
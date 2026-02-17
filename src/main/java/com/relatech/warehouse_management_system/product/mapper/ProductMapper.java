package com.relatech.warehouse_management_system.product.mapper;

import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.product.ProductDto;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public static ProductDto toDto(Product product) {
        if (product == null) {
            return null;
        }
        return ProductDto.builder()
                .code(product.getCode())
                .name(product.getName())
                .category(product.getCategory())
                .build();
    }

    public static Product toEntity(ProductDto dto) {
        if (dto == null) {
            return null;
        }

        return Product.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .category(dto.getCategory())
                .build();
    }
}
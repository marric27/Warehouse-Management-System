package com.relatech.warehouse_management_system.product.mapper;

import com.relatech.warehouse_management_system.product.dto.ProductDTO;
import com.relatech.warehouse_management_system.product.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public static ProductDTO toDto(Product product) {
        if (product == null) {
            return null;
        }
        return ProductDTO.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .category(product.getCategory())
                .build();
    }

    public static Product toEntity(ProductDTO dto) {
        if (dto == null) {
            return null;
        }

        Product product = new Product();
        product.setId(dto.getId());
        product.setCode(dto.getCode());
        product.setName(dto.getName());
        product.setCategory(dto.getCategory());

        return product;
    }
}
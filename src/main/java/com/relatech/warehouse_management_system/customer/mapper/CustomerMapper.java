package com.relatech.warehouse_management_system.customer.mapper;

import com.relatech.warehouse_management_system.customer.dto.CustomerDTO;
import com.relatech.warehouse_management_system.customer.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    public CustomerDTO toDTO(Customer entity) {
        if (entity == null) return null;
        return CustomerDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .surname(entity.getSurname())
                .shippingAddress(entity.getShippingAddress())
                .billingAddress(entity.getBillingAddress())
                .email(entity.getEmail())
                .taxCode(entity.getTaxCode())
                .build();
    }

    public Customer toEntity(CustomerDTO dto) {
        if (dto == null) return null;
        return Customer.builder()
                .id(dto.getId())
                .name(dto.getName())
                .surname(dto.getSurname())
                .shippingAddress(dto.getShippingAddress())
                .billingAddress(dto.getBillingAddress())
                .email(dto.getEmail())
                .taxCode(dto.getTaxCode())
                .build();
    }
}

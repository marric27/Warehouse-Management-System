package com.relatech.warehouse_management_system.outbound.entity.mapper;

import com.relatech.warehouse_management_system.outbound.dto.CustomerDto;
import com.relatech.warehouse_management_system.outbound.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    public CustomerDto toDTO(Customer entity) {
        if (entity == null) return null;
        return CustomerDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .surname(entity.getSurname())
                .shippingAddress(entity.getShippingAddress())
                .billingAddress(entity.getBillingAddress())
                .email(entity.getEmail())
                .taxCode(entity.getTaxCode())
                .customerCode(entity.getCustomerCode())
                .build();
    }
    public Customer toEntity(CustomerDto dto) {
        if (dto == null) return null;
        return Customer.builder()
                .id(dto.getId())
                .name(dto.getName())
                .surname(dto.getSurname())
                .shippingAddress(dto.getShippingAddress())
                .billingAddress(dto.getBillingAddress())
                .email(dto.getEmail())
                .taxCode(dto.getTaxCode())
                .customerCode(dto.getCustomerCode())
                .build();
    }
}

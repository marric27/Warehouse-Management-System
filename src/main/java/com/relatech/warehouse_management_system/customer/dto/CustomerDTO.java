package com.relatech.warehouse_management_system.customer.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDTO {
    private Long id;
    private String name;
    private String surname;
    private String shippingAddress;
    private String billingAddress;
    private String email;
    private String taxCode;
}


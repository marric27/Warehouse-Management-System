package com.relatech.warehouse_management_system.customer.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CustomerDTO {
    private Long id;

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotBlank(message = "Surname must not be blank")
    private String surname;

    @NotBlank(message = "Shipping address must not be blank")
    private String shippingAddress;

    @NotBlank(message = "Billing address must not be blank")
    private String billingAddress;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email must not be blank")
    private String email;

    @NotBlank(message = "Tax code must not be blank")
    @Size(min = 11, max = 16, message = "Tax code length must be between 11 and 16 characters")
    private String taxCode;
}

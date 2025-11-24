package com.relatech.warehouse_management_system.customer.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CustomerDTO {
    @Schema (accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema (description = "Unique identifier of the customer" , example = "123")
    @NotBlank(message = "Name must not be blank")
    private String name;

     @Schema(description = "Customer's first name", example = "Mario")
    @NotBlank(message = "Surname must not be blank")
    private String surname;

     @Schema(description = "Customer's surname", example = "Rossi")
    @NotBlank(message = "Shipping address must not be blank")
    private String shippingAddress;

     @Schema(description = "Billing address of the customer" , example = "Via Milano 20, Milan")
    @NotBlank(message = "Billing address must not be blank")
    private String billingAddress;

     @Schema(description = "Valid email address" , example = "mario.rossie@example.com")
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email must not be blank")
    private String email;

    @Schema(description = "Italian tax code (16 characters)")
    @NotBlank(message = "Tax code must not be blank")
    @Size(min = 16, max = 16, message = "Tax code must be 16 characters")
    private String taxCode;

}

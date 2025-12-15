package com.relatech.warehouse_management_system.customer.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CustomerDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Customer's first name", example = "Mario")
    @NotBlank(message = "Name must not be blank")
    private String name;

    @Schema(description = "Customer's surname", example = "Rossi")
    @NotBlank(message = "Surname must not be blank")
    private String surname;


    @NotBlank(message = "Shipping address must not be blank")
    private String shippingAddress;

    @Schema(description = "Billing address of the customer", example = "Via Milano 20, Milan")
    @NotBlank(message = "Billing address must not be blank")
    private String billingAddress;

    @Schema(description = "Valid email address", example = "mario.rossie@example.com")
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email must not be blank")
    private String email;

    @Schema(description = "Italian tax code (16 characters)")
    @NotBlank(message = "Tax code must not be blank")
    @Size(min = 16, max = 16, message = "Tax code must be 16 characters")
    private String taxCode;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Unique ID of the Customer", example = "CUST-001", accessMode = Schema.AccessMode.READ_ONLY)
    private String customerCode;

}

package com.relatech.warehouse_management_system.goodsIn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.relatech.warehouse_management_system.common.util.State;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request/Response DTO for Goods Receipt Note")
public class GrnDTO {
    @Schema (accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Unique ID of the GRN", example = "GRN-001", accessMode = Schema.AccessMode.READ_ONLY)
    private String code;

    @NotBlank(message = "Supplier cannot be blank")
    @Length(min = 1, max = 100, message = "Supplier name must be between 1 and 100 characters")
    @Schema(description = "Supplier name", example = "ACME Corp")
    private String supplier;

    @NotNull(message = "Receiving date cannot be null")
    @Schema(description = "Date when goods are received", example = "2025-11-21")
    private LocalDate receivingDate;

    @Schema(description = "GRN status", example = "OPEN")
    private State state;

    @Valid
    @Schema(description = "List of items in the GRN", accessMode = Schema.AccessMode.READ_ONLY)
    private List<GrnItemDto> items;
}

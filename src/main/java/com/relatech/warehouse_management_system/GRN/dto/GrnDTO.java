package com.relatech.warehouse_management_system.GRN.dto;

import com.relatech.warehouse_management_system.grnItem.dto.GrnItemDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request/Response DTO for Goods Receipt Note")
public class GrnDTO {

    private Long id;

    @NotBlank(message = "The code cannot be empty or null.")
    @Schema(description = "Unique ID of the GRN", example = "GRN-001")
    private String code;

    @NotBlank(message = "Supplier cannot be blank")
    @Length(min = 1, max = 100, message = "Supplier name must be between 1 and 100 characters")
    @Schema(description = "Supplier name", example = "ACME Corp")
    private String supplier;

    @NotNull(message = "Receiving date cannot be null")
    @Schema(description = "Date when goods are received", example = "2025-11-21")
    private LocalDate receivingDate;

    @Schema(description = "GRN status", example = "OPEN")
    private String state;

    @Valid
    @Schema(description = "List of items in the GRN")
    private List<GrnItemDto> items;
}

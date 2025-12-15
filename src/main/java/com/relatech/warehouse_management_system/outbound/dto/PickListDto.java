package com.relatech.warehouse_management_system.outbound.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Represents a pick list containing customer information and item details.")
public class PickListDto {

    @Schema(description = "Unique identifier of the pick list.", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Auto-generated unique pick list code.", accessMode = Schema.AccessMode.READ_ONLY)
    private String code;

    @Schema(description = "Auto-generated pick list release number.", example = "RLS-01FZ3M7Y8C", accessMode = Schema.AccessMode.READ_ONLY)
    private String releaseNumber;

    @Schema(description = "Code of the customer associated with the pick list.")
    private String customerCode;

    @Schema(description = "List of items included in the pick list.")
    @Builder.Default
    private List<PickListItemDto> pickListItemList = new ArrayList<>();

}

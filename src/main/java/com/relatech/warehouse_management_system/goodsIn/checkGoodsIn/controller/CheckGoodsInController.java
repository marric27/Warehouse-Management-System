package com.relatech.warehouse_management_system.goodsIn.checkGoodsIn.controller;

import com.relatech.warehouse_management_system.goodsIn.checkGoodsIn.service.CheckGoodsInService;
import com.relatech.warehouse_management_system.goodsIn.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/check-goods-in")
@RequiredArgsConstructor
@Tag(
        name = "Check Goods In Management",
        description = "Handles creation of CheckingInfo + StockUnit and assignment to GRN items"
)
public class CheckGoodsInController {

    private final CheckGoodsInService checkGoodsInService;

    /**
     * Create a StockUnit and a CheckingInfo, link them, assign the CheckingInfo to
     * a GRN item, and automatically update the item state.
     * POST /check-goods-in/{grnItemId}/checking-info
     */
    @PostMapping("/{grnItemId}/checking-info")
    public ResponseEntity<GrnItemDto> createCheckingInfo(
            @PathVariable Long grnItemId,
            @RequestBody CreateCheckingInfoRequest request) throws Exception {
        log.info("POST /{}/checking-info - creating checking-info {} and stockUnit {}", grnItemId, request.getCheckingInfo().getCode(), request.getStockUnit().getCode());
        GrnItemDto result = checkGoodsInService.createCheckingInfoAndStockUnit(
                grnItemId,
                request.getCheckingInfo(),
                request.getStockUnit()
        );

        return ResponseEntity.ok(result);
    }


    /**
     * Wrapper request DTO: contains both CheckingInfoDto and StockUnitDto
     */
    @Getter
    @Setter
    public static class CreateCheckingInfoRequest {
        private CheckingInfoDto checkingInfo;
        private StockUnitDTO stockUnit;
    }

    @GetMapping("/checking-infos")
    @Operation(summary = "List all Checking Info")
    public ResponseEntity<List<CheckingInfoDto>> listCheckingInfo() {
        return ResponseEntity.ok(checkGoodsInService.listCheckinginfo());
    }

    @GetMapping("/checking-infos/paged")
    @Operation(summary = "List Checking Info paginated")
    public ResponseEntity<Page<CheckingInfoDto>> listCheckingInfoPaged(Pageable pageable) {
        return ResponseEntity.ok(checkGoodsInService.listCIPaged(pageable));
    }

    @GetMapping("/stock-units")
    @Operation(summary = "List all Stock Units")
    public ResponseEntity<List<StockUnitDTO>> listStockUnits() {
        return ResponseEntity.ok(checkGoodsInService.listStockUnit());
    }

    @GetMapping("/stock-units/paged")
    @Operation(summary = "List Stock Units paginated")
    public ResponseEntity<Page<StockUnitDTO>> listStockUnitsPaged(Pageable pageable) {
        return ResponseEntity.ok(checkGoodsInService.listStockUnitPaged(pageable));
    }
}

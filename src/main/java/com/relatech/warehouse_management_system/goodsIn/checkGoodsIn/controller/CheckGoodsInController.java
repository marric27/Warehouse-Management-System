package com.relatech.warehouse_management_system.goodsIn.checkGoodsIn.controller;

import com.relatech.warehouse_management_system.goodsIn.checkGoodsIn.service.CheckGoodsInService;
import com.relatech.warehouse_management_system.goodsIn.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        log.info("POST /{}/checking-info - creating checking-info {} and stockUnit {}", grnItemId, request.getCheckingInfo().getCode(), request.getStockUnit().getUniqueCode());
        GrnItemDto result = checkGoodsInService.createCheckingInfo(
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
}

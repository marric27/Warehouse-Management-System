package com.relatech.warehouse_management_system.goodsIn.checkGoodsIn.controller;


import com.relatech.warehouse_management_system.goodsIn.checkGoodsIn.service.CheckGoodsInService;
import com.relatech.warehouse_management_system.goodsIn.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/check-goods-in")
@RequiredArgsConstructor
@Tag(name = "Check Goods In Management", description = "Complete workflow for ")
public class CheckGoodsInController {

    private final CheckGoodsInService checkGoodsInService;

    /**
     * Creates CheckingInfo and StockUnit, links them together,
     * and assigns CheckingInfo to a specific GRN item.
     */
    @PostMapping("/{grnItemId}/create-and-assign")
    public ResponseEntity<GrnItemDto> createCheckingInfoWithStockUnitAndAssign(
            @PathVariable Long grnItemId,
            @RequestBody CreateCheckingInfoRequest request) throws Exception {

        GrnItemDto result = checkGoodsInService.createCheckingInfoWithStockUnitAndAssignToGrnItem(
                request.getCheckingInfo(),
                request.getStockUnit(),
                grnItemId
        );

        return ResponseEntity.ok(result);
    }

    /**
     * Wrapper request DTO to carry both CheckingInfoDto and StockUnitDTO.
     */
    @Setter
    @Getter
    public static class CreateCheckingInfoRequest {
        private CheckingInfoDto checkingInfo;
        private StockUnitDTO stockUnit;

    }
}
package com.relatech.warehouse_management_system.outbound.release.controller;

import com.relatech.warehouse_management_system.outbound.dto.OrderDto;
import com.relatech.warehouse_management_system.outbound.release.service.ReleaseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/release")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Release Management",
        description = "Endpoints for managing filters and retrieving outbound orders"
)
public class ReleaseController {

    private final ReleaseService releaseService;

    /**
     * Filtro per customerCode
     */
    @GetMapping("/orders/customer/{customerCode}")
    public Page<OrderDto> getOrdersByCustomer(
            @PathVariable String customerCode,
            Pageable pageable
    ) {
        log.info("Filtering orders by customerCode={}", customerCode);
        return releaseService.getOrdersByCustomer(customerCode, pageable);
    }

    /**
     * Filtro per range di date
     */
    @GetMapping("/orders/date")
    public Page<OrderDto> getOrdersByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            Pageable pageable
    ) {
        log.info("Filtering orders by date range {} - {}", start, end);
        return releaseService.getOrdersByDate(start, end, pageable);
    }

    /**
     * Filtro per productId
     */
    @GetMapping("/orders/product/{productId}")
    public Page<OrderDto> getOrdersByProduct(
            @PathVariable Long productId,
            Pageable pageable
    ) {
        log.info("Filtering orders by productId={}", productId);
        return releaseService.getOrdersByProduct(productId, pageable);
    }

    /**
     * Filtro avanzato (tutti opzionali)
     */
    @GetMapping("/orders/filter")
    public Page<OrderDto> getOrdersByParameters(
            @RequestParam(required = false) String customerCode,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            Pageable pageable
    ) {
        log.info("Filtering orders by parameters -> customerCode={}, productId={}, start={}, end={}",
                customerCode, productId, start, end);

        return releaseService.getOrdersByParameters(customerCode, productId, start, end, pageable);
    }
}

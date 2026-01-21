package com.relatech.warehouse_management_system.outbound.salesOrder;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.outbound.dto.OrderDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sales-order")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Sales Order", description = "Sales order controller for customer and order management workflow")
public class SalesOrderController {
    private final SalesOrderService salesOrderService;

    @PostMapping("/create-order")
    public ResponseEntity<OrderDto> createOrderAndAssign(@RequestBody OrderDto orderDto) throws ResourceNotFoundException {
        String customerCode = orderDto.getCustomerCode();
        log.info("Creating Order for Customer {}", customerCode);
        OrderDto created = salesOrderService.createOrderAndAssign(orderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        log.info("Request received: get all orders");

        List<OrderDto> orders = salesOrderService.getAllOrders();

        log.info("Returning {} orders", orders.size());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders-paged")
    public ResponseEntity<Page<OrderDto>> getAllOrdersPaged(Pageable pageable) {
        log.info("Request received: get orders paged (page={}, size={})",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<OrderDto> orders = salesOrderService.getAllOrdersPaged(pageable);

        log.info("Returning {} orders for page {}", orders.getNumberOfElements(), orders.getNumber());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) throws ResourceNotFoundException {
        log.info("Request received: get orders by id ={}", id);
        return ResponseEntity.ok(salesOrderService.getOrderById(id));
    }

    @GetMapping("/orders/code/{code}")
    public ResponseEntity<OrderDto> getOrderByCode(@PathVariable String code) throws ResourceNotFoundException {
        log.info("Request received: get orders by code ={}", code);
        return ResponseEntity.ok(salesOrderService.getOrderByCode(code));
    }

}

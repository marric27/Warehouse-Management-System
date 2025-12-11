package com.relatech.warehouse_management_system.outbound.salesOrder;

import com.relatech.warehouse_management_system.common.exception.DuplicateResourceException;
import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.outbound.dto.CustomerDTO;
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

    @PostMapping("/customer")
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customerDTO) throws DuplicateResourceException {
        log.info("Creating Customer");
        CustomerDTO created = salesOrderService.createCustomer(customerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/create-order/{customerId}")
    public ResponseEntity<OrderDto> createOrderAndAssign(@PathVariable Long customerId, @RequestBody OrderDto orderDto) throws ResourceNotFoundException {
        log.info("Creating Order for Customer {}", customerId);
        OrderDto created = salesOrderService.createOrderAndAssign(customerId, orderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        log.info("Request received: get all customers");

        List<CustomerDTO> customers = salesOrderService.getAllCustomers();

        log.info("Returning {} customers", customers.size());
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/customers-paged")
    public ResponseEntity<Page<CustomerDTO>> getAllCustomersPaged(Pageable pageable) {
        log.info("Request received: get customers paged (page={}, size={})",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<CustomerDTO> customers = salesOrderService.getAllCustomersPaged(pageable);

        log.info("Returning {} customers for page {}", customers.getNumberOfElements(), customers.getNumber());
        return ResponseEntity.ok(customers);
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

}

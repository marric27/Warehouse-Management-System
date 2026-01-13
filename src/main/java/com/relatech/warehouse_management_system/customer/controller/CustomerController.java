package com.relatech.warehouse_management_system.customer.controller;

import com.relatech.warehouse_management_system.common.exception.CustomerWithActiveOrdersException;
import com.relatech.warehouse_management_system.common.exception.DuplicateResourceException;
import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.customer.entity.CustomerDto;
import com.relatech.warehouse_management_system.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Customer Management",
        description = "Endpoints for managing customers"
)
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(@Valid @RequestBody CustomerDto dto) throws DuplicateResourceException {
        log.info("Request to create customer: {} {}", dto.getName(), dto.getSurname());
        CustomerDto created = customerService.createCustomer(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomer(@PathVariable Long id) throws ResourceNotFoundException {
        log.info("Request to fetch customer with ID: {}", id);
        CustomerDto customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<CustomerDto> getCustomerByCode(@PathVariable String code) throws ResourceNotFoundException {
        log.info("Request to fetch customer with code: {}", code);
        CustomerDto customer = customerService.getCustomerByCode(code);
        return ResponseEntity.ok(customer);
    }

    @GetMapping
    public ResponseEntity<Page<CustomerDto>> getAllCustomersPaged(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        log.info("Request to fetch customers paged: page {}, size {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<CustomerDto> customersPage = customerService.getAllCustomersPaged(pageable);
        return ResponseEntity.ok(customersPage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerDto dto) throws ResourceNotFoundException {
        log.info("Request to update customer with ID: {}", id);
        CustomerDto updated = customerService.updateCustomer(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) throws ResourceNotFoundException, CustomerWithActiveOrdersException {
        log.info("Request to delete customer with ID: {}", id);
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerDto>> searchCustomers(@RequestParam(name = "term", required = false) String term) {
        log.info("Request to search customers with term: {}", term);
        List<CustomerDto> results = customerService.searchCustomers(term);
        return ResponseEntity.ok(results);
    }


}

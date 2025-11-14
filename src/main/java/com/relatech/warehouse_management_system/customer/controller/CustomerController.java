package com.relatech.warehouse_management_system.customer.controller;

import com.relatech.warehouse_management_system.customer.dto.CustomerDTO;
import com.relatech.warehouse_management_system.customer.service.CustomerService;
import com.relatech.warehouse_management_system.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO dto) throws DuplicateResourceException {
        log.info("Request to create customer: {} {}", dto.getName(), dto.getSurname());
        CustomerDTO created = customerService.createCustomer(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable Long id) throws ResourceNotFoundException {
        log.info("Request to fetch customer with ID: {}", id);
        CustomerDTO customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        log.info("Request to fetch all customers");
        List<CustomerDTO> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO dto) throws ResourceNotFoundException {
        log.info("Request to update customer with ID: {}", id);
        CustomerDTO updated = customerService.updateCustomer(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) throws ResourceNotFoundException, CustomerWithActiveOrdersException {
        log.info("Request to delete customer with ID: {}", id);
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerDTO>> searchCustomers(@RequestParam(name = "term", required = false) String term) {
        log.info("Request to search customers with term: {}", term);
        List<CustomerDTO> results = customerService.searchCustomers(term);
        return ResponseEntity.ok(results);
    }
}

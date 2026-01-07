package com.relatech.warehouse_management_system.customer.service;

import com.relatech.warehouse_management_system.common.exception.CustomerWithActiveOrdersException;
import com.relatech.warehouse_management_system.common.exception.DuplicateResourceException;
import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.customer.entity.CustomerDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {

    CustomerDto createCustomer(CustomerDto customerDTO) throws DuplicateResourceException;

    CustomerDto getCustomerById(Long id) throws ResourceNotFoundException;

    List<CustomerDto> getAllCustomers();

    Page<CustomerDto> getAllCustomersPaged(Pageable pageable);

    CustomerDto updateCustomer(Long id, CustomerDto customerDTO) throws ResourceNotFoundException;

    void deleteCustomer(Long id) throws ResourceNotFoundException, CustomerWithActiveOrdersException;

    List<CustomerDto> searchCustomers(String term);

}

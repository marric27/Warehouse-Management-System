package com.relatech.warehouse_management_system.customer.service;

import com.relatech.warehouse_management_system.customer.dto.CustomerDTO;
import com.relatech.warehouse_management_system.exception.*;

import java.util.List;

public interface CustomerService {

    CustomerDTO createCustomer(CustomerDTO customerDTO) throws DuplicateResourceException;

    CustomerDTO getCustomerById(Long id) throws ResourceNotFoundException;

    List<CustomerDTO> getAllCustomers();

    CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) throws ResourceNotFoundException;

    void deleteCustomer(Long id) throws ResourceNotFoundException, CustomerWithActiveOrdersException;

    List<CustomerDTO> searchCustomers(String term);

}

package com.relatech.warehouse_management_system.customer.service;

import com.relatech.warehouse_management_system.common.exception.CustomerWithActiveOrdersException;
import com.relatech.warehouse_management_system.common.exception.DuplicateResourceException;
import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.customer.dto.CustomerDTO;
import com.relatech.warehouse_management_system.customer.entity.Customer;
import com.relatech.warehouse_management_system.customer.mapper.CustomerMapper;
import com.relatech.warehouse_management_system.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional(rollbackFor = DuplicateResourceException.class)
    public CustomerDTO createCustomer(CustomerDTO customerDTO) throws DuplicateResourceException {
        if (customerRepository.findByEmail(customerDTO.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Customer", "email", customerDTO.getEmail());
        }
        if (customerRepository.findByTaxCode(customerDTO.getTaxCode()).isPresent()) {
            throw new DuplicateResourceException("Customer", "taxCode", customerDTO.getTaxCode());
        }
        Customer customer = customerMapper.toEntity(customerDTO);
        Customer saved = customerRepository.save(customer);
        log.info("Customer created with ID: {}", saved.getId());
        return customerMapper.toDTO(saved);
    }

    @Override
    public CustomerDTO getCustomerById(Long id) throws ResourceNotFoundException {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        return customerMapper.toDTO(customer);
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toDTO).toList();
    }


    @Override
    public Page<CustomerDTO> getAllCustomersPaged(Pageable pageable) {
        Page<Customer> customersPage = customerRepository.findAll(pageable);
        return customersPage.map(customerMapper::toDTO);
    }


    @Override
    @Transactional(timeout = 5, propagation = Propagation.REQUIRED)
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) throws ResourceNotFoundException {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        existing.setName(customerDTO.getName());
        existing.setSurname(customerDTO.getSurname());
        existing.setShippingAddress(customerDTO.getShippingAddress());
        existing.setBillingAddress(customerDTO.getBillingAddress());
        existing.setEmail(customerDTO.getEmail());
        existing.setTaxCode(customerDTO.getTaxCode());
        Customer saved = customerRepository.save(existing);
        log.info("Customer updated with ID: {}", saved.getId());
        return customerMapper.toDTO(saved);
    }

    public boolean hasActiveOrders(Long customerId) {
        return false;
    }

    @Override
    @Transactional(rollbackFor = {ResourceNotFoundException.class, CustomerWithActiveOrdersException.class},
            propagation = Propagation.REQUIRES_NEW)
    public void deleteCustomer(Long id) throws ResourceNotFoundException, CustomerWithActiveOrdersException {
        log.info("Deleting customer with ID: {}", id);
        if (hasActiveOrders(id)) {
            throw new CustomerWithActiveOrdersException(id);
        }
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        customerRepository.delete(customer);
        log.info("Customer deleted with ID: {}", id);
    }

    @Override
    public List<CustomerDTO> searchCustomers(String term) {
        if (term == null || term.isEmpty()) {
            return getAllCustomers();
        }
        return customerRepository.searchByTerm(term)
                .stream()
                .map(customerMapper::toDTO).toList();
    }
}
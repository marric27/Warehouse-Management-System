package com.relatech.warehouse_management_system.outbound.salesOrder;

// obiettivo: creazione ordini per customers
// creazione customer
// creazione ordine per customer
// creazione salesorderline insieme al order o anche aggiunta dopo? per ora creo tutto insieme


import com.relatech.warehouse_management_system.common.exception.DuplicateResourceException;
import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.outbound.dto.CustomerDTO;
import com.relatech.warehouse_management_system.outbound.dto.OrderDto;
import com.relatech.warehouse_management_system.outbound.entity.service.CustomerService;
import com.relatech.warehouse_management_system.outbound.entity.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SalesOrderService {
    private final CustomerService customerService;
    private final OrderService orderService;

    @Transactional(rollbackFor = {DuplicateResourceException.class})
    public CustomerDTO createCustomer(CustomerDTO customerDTO) throws DuplicateResourceException {
        return customerService.createCustomer(customerDTO);
    }

    @Transactional(rollbackFor = ResourceNotFoundException.class)
    public OrderDto createOrderAndAssign(Long customerId, OrderDto orderDto) throws ResourceNotFoundException {
        CustomerDTO customerDTO = customerService.getCustomerById(customerId);
        orderDto.setCustomerCode(customerDTO.getCustomerCode());
        return orderService.createOrder(orderDto);
    }

    @Transactional(readOnly = true)
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @Transactional(readOnly = true)
    public Page<CustomerDTO> getAllCustomersPaged(Pageable pageable) {
        return customerService.getAllCustomersPaged(pageable);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getAllOrdersPaged(Pageable pageable) {
        return orderService.getAllOrdersPaged(pageable);
    }




}

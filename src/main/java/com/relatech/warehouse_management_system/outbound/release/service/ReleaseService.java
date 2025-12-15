package com.relatech.warehouse_management_system.outbound.release.service;

import com.relatech.warehouse_management_system.outbound.dto.OrderDto;
import com.relatech.warehouse_management_system.customer.service.CustomerService;
import com.relatech.warehouse_management_system.outbound.entity.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReleaseService {
    private final CustomerService customerService;
    private final OrderService orderService;

    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersByCustomer(String customerCode, Pageable pageable) {
        return orderService.filterByCustomer(customerCode, pageable);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersByDate(LocalDate start, LocalDate end, Pageable pageable) {
        return orderService.filterByDateRange(start, end, pageable);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersByProduct(Long productId, Pageable pageable) {
        return orderService.filterByProduct(productId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersByParameters(String customerCode, Long productId, LocalDate start, LocalDate end, Pageable pageable) {
        return orderService.filterOrders(customerCode, productId, start, end, pageable);
    }
}

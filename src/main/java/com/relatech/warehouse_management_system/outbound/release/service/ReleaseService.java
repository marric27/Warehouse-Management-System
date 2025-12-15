package com.relatech.warehouse_management_system.outbound.release.service;

import com.relatech.warehouse_management_system.common.util.OrderState;
import com.relatech.warehouse_management_system.outbound.dto.OrderDto;
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
    public Page<OrderDto> getOrdersByProduct(String productCode, Pageable pageable) {
        return orderService.filterByProduct(productCode, pageable);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersByParameters(OrderState orderState, String customerCode, String productCode, LocalDate start, LocalDate end, Pageable pageable) {
        return orderService.filterOrders(orderState, customerCode, productCode, start, end, pageable);
    }
}

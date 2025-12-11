package com.relatech.warehouse_management_system.outbound.entity.service;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.outbound.dto.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderDto createOrder(OrderDto orderDto);

    OrderDto getOrderById(Long id) throws ResourceNotFoundException;

    OrderDto getOrderByCode(String code) throws ResourceNotFoundException;

    List<OrderDto> getAllOrders();

    Page<OrderDto> getAllOrdersPaged(Pageable pageable);

}

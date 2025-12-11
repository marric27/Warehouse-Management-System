package com.relatech.warehouse_management_system.outbound.entity.service;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.outbound.dto.OrderDto;
import com.relatech.warehouse_management_system.outbound.entity.Order;
import com.relatech.warehouse_management_system.outbound.entity.mapper.OrderMapper;
import com.relatech.warehouse_management_system.outbound.entity.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    @Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
    public OrderDto createOrder(OrderDto orderDto) {
        log.debug("Creating new Order");
        Order order = OrderMapper.toEntity(orderDto);
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long id) throws ResourceNotFoundException {
        return orderRepository.findById(id)
                .map(OrderMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderByCode(String code) throws ResourceNotFoundException {
        return orderRepository.findByCode(code)
                .map(OrderMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Order", code));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getAllOrdersPaged(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAll(pageable);
        return orderPage.map(OrderMapper::toDto);
    }

    // FILTERS
    @Override
    public Page<OrderDto> filterByCustomer(String customerCode, Pageable pageable) {
        Page<Order> orderPage =  orderRepository.findByCustomerCode(customerCode, pageable);
        return orderPage.map(OrderMapper::toDto);
    }

    @Override
    public Page<OrderDto> filterByDateRange(LocalDate start, LocalDate end, Pageable pageable) {
        Page<Order> orderPage =  orderRepository.findByDateBetween(start, end, pageable);
        return orderPage.map(OrderMapper::toDto);
    }

    @Override
    public Page<OrderDto> filterByProduct(Long productId, Pageable pageable) {
        Page<Order> orderPage =  orderRepository.findByProductId(productId, pageable);
        return orderPage.map(OrderMapper::toDto);
    }

    @Override
    public Page<OrderDto> filterOrders(String customerCode, Long productId, LocalDate start, LocalDate end, Pageable pageable) {
        Page<Order> orderPage =  orderRepository.filterOrders(customerCode, productId, start, end, pageable);
        return orderPage.map(OrderMapper::toDto);
    }
}

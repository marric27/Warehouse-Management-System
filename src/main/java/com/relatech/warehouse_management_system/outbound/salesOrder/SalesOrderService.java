package com.relatech.warehouse_management_system.outbound.salesOrder;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.common.util.OrderState;
import com.relatech.warehouse_management_system.customer.entity.CustomerDto;
import com.relatech.warehouse_management_system.customer.service.CustomerService;
import com.relatech.warehouse_management_system.outbound.dto.OrderDto;
import com.relatech.warehouse_management_system.outbound.dto.SalesOrderLineDto;
import com.relatech.warehouse_management_system.outbound.entity.service.OrderService;
import com.relatech.warehouse_management_system.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SalesOrderService {
    private final CustomerService customerService;
    private final OrderService orderService;
    private final ProductService productService;

    @Transactional(rollbackFor = ResourceNotFoundException.class)
    public OrderDto createOrderAndAssign(Long customerId, OrderDto orderDto) throws ResourceNotFoundException {
        CustomerDto customerDTO = customerService.getCustomerById(customerId);

        List<String> productCodes = orderDto.getSalesOrderLineList().stream()
                .map(SalesOrderLineDto::getProductCode)
                .distinct()
                .toList();

        for (String code : productCodes)
            productService.validateProductExists(code);

        orderDto.setCustomerCode(customerDTO.getCustomerCode());
        orderDto.setState(OrderState.OPEN);
        orderDto.setDate(LocalDate.now());
        orderDto.getSalesOrderLineList().forEach(line -> {line.setStatus(OrderState.OPEN);});
        return orderService.createOrder(orderDto);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getAllOrdersPaged(Pageable pageable) {
        return orderService.getAllOrdersPaged(pageable);
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long id) throws ResourceNotFoundException {
        return orderService.getOrderById(id);
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderByCode(String code) throws ResourceNotFoundException {
        return orderService.getOrderByCode(code);
    }
}

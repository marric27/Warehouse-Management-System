package com.relatech.warehouse_management_system.outbound.release;

import com.relatech.warehouse_management_system.common.util.OrderState;
import com.relatech.warehouse_management_system.outbound.dto.OrderDto;
import com.relatech.warehouse_management_system.outbound.entity.service.OrderService;
import com.relatech.warehouse_management_system.outbound.release.service.ReleaseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReleaseServiceTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private ReleaseService releaseService;

    @Test
    void getOrdersByCustomer_delegatesToOrderService() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderDto> expected = new PageImpl<>(List.of(new OrderDto()));

        when(orderService.filterByCustomer("CUST-01", pageable))
                .thenReturn(expected);

        Page<OrderDto> result = releaseService.getOrdersByCustomer("CUST-01", pageable);

        assertSame(expected, result);
        verify(orderService).filterByCustomer("CUST-01", pageable);
    }

    @Test
    void getOrdersByDate_delegatesToOrderService() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 12, 31);

        Page<OrderDto> expected = new PageImpl<>(List.of(new OrderDto()));

        when(orderService.filterByDateRange(start, end, pageable))
                .thenReturn(expected);

        Page<OrderDto> result = releaseService.getOrdersByDate(start, end, pageable);

        assertSame(expected, result);
        verify(orderService).filterByDateRange(start, end, pageable);
    }

    @Test
    void getOrdersByProduct_delegatesToOrderService() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderDto> expected = new PageImpl<>(List.of(new OrderDto()));

        when(orderService.filterByProduct("PROD-01", pageable))
                .thenReturn(expected);

        Page<OrderDto> result = releaseService.getOrdersByProduct("PROD-01", pageable);

        assertSame(expected, result);
        verify(orderService).filterByProduct("PROD-01", pageable);
    }

    @Test
    void getOrdersByParameters_delegatesToOrderService() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 12, 31);

        Page<OrderDto> expected = new PageImpl<>(List.of(new OrderDto()));

        when(orderService.filterOrders(
                OrderState.OPEN,
                "CUST-01",
                "PROD-01",
                start,
                end,
                pageable
        )).thenReturn(expected);

        Page<OrderDto> result = releaseService.getOrdersByParameters(
                OrderState.OPEN,
                "CUST-01",
                "PROD-01",
                start,
                end,
                pageable
        );

        assertSame(expected, result);
        verify(orderService).filterOrders(
                OrderState.OPEN,
                "CUST-01",
                "PROD-01",
                start,
                end,
                pageable
        );
    }
}

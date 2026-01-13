package com.relatech.warehouse_management_system.picking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.relatech.warehouse_management_system.outbound.dto.PickListItemDto;
import com.relatech.warehouse_management_system.picking.controller.PickingController;
import com.relatech.warehouse_management_system.picking.dto.ConfirmPickingRequest;
import com.relatech.warehouse_management_system.picking.dto.NextItemRequest;
import com.relatech.warehouse_management_system.picking.dto.StockUnitQuantityDto;
import com.relatech.warehouse_management_system.picking.service.PickingService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PickingController.class)
class PickingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PickingService pickingService;

    @Autowired
    private ObjectMapper objectMapper;

    private PickListItemDto sampleItem;

    private Validator validator;

    @BeforeEach
    void setUp() {
        sampleItem = PickListItemDto.builder()
                .code("PKLI-22")
                .productCode("PROD-01")
                .quantity(5)
                .pickedQty(0)
                .build();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testGetNextPickListItem_found() throws Exception {
        NextItemRequest request = new NextItemRequest();
        request.setPickListIds(List.of(1L, 2L));

        Mockito.when(pickingService.getNextPickListItem(any(NextItemRequest.class)))
                .thenReturn(sampleItem);

        mockMvc.perform(post("/picking/next-item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("PKLI-22"))
                .andExpect(jsonPath("$.productCode").value("PROD-01"))
                .andExpect(jsonPath("$.quantity").value(5));
    }

    @Test
    void testGetNextPickListItem_notFound() throws Exception {
        NextItemRequest request = new NextItemRequest();
        request.setPickListIds(List.of(1L));

        Mockito.when(pickingService.getNextPickListItem(any(NextItemRequest.class)))
                .thenReturn(null);

        mockMvc.perform(post("/picking/next-item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void testConfirmPicking_success() throws Exception {
        ConfirmPickingRequest request = new ConfirmPickingRequest();
        request.setPickListCode("PKL-01KCKXFNW3");
        request.setPickListItemCode("PKLI-22");
        StockUnitQuantityDto stockUnitQuantityDto = new StockUnitQuantityDto("STK-01KCH3N988", 3);
        request.setStockUnitQuantities(List.of(stockUnitQuantityDto));
        request.setUser("USER-22");

        Mockito.doNothing().when(pickingService).confirmPicking(any(ConfirmPickingRequest.class));

        mockMvc.perform(post("/picking/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testConfirmPicking_serviceThrowsException() throws Exception {
        ConfirmPickingRequest request = new ConfirmPickingRequest();
        request.setPickListCode("PKL-01KCKXFNW3");
        request.setPickListItemCode("PKLI-22");
        StockUnitQuantityDto stockUnitQuantityDto = new StockUnitQuantityDto("STK-01KCH3N988", 3);
        request.setStockUnitQuantities(List.of(stockUnitQuantityDto));
        request.setUser("USER-22");

        Mockito.doThrow(new RuntimeException("Picking failed")).when(pickingService).confirmPicking(any(ConfirmPickingRequest.class));

        mockMvc.perform(post("/picking/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testConfirmPickingRequest_valid() {
        ConfirmPickingRequest request = new ConfirmPickingRequest();
        request.setPickListCode("PKL-01");
        request.setPickListItemCode("PKLI-22");
        request.setUser("USER-01");
        StockUnitQuantityDto stockUnitQuantityDto = new StockUnitQuantityDto("STK-01", 5);
        request.setStockUnitQuantities(List.of(stockUnitQuantityDto));

        Set<ConstraintViolation<ConfirmPickingRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Non ci dovrebbero essere violazioni");
    }

    @Test
    void testConfirmPickingRequest_missingPickListCode() {
        ConfirmPickingRequest request = new ConfirmPickingRequest();
        request.setPickListItemCode("PKLI-22");
        request.setUser("USER-01");
                StockUnitQuantityDto stockUnitQuantityDto = new StockUnitQuantityDto("STK-01", 5);
        request.setStockUnitQuantities(List.of(stockUnitQuantityDto));

        Set<ConstraintViolation<ConfirmPickingRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("pickListCode")));
    }

    @Test
    void testConfirmPickingRequest_emptyStockUnitQuantities() {
        ConfirmPickingRequest request = new ConfirmPickingRequest();
        request.setPickListCode("PKL-01");
        request.setPickListItemCode("PKLI-22");
        request.setUser("USER-01");
        request.setStockUnitQuantities(List.of());

        Set<ConstraintViolation<ConfirmPickingRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testConfirmPickingRequest_nullStockUnitQuantities() {
        ConfirmPickingRequest request = new ConfirmPickingRequest();
        request.setPickListCode("PKL-01");
        request.setPickListItemCode("PKLI-22");
        request.setUser("USER-01");
        request.setStockUnitQuantities(null);

        Set<ConstraintViolation<ConfirmPickingRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNextItemRequest_emptyPickListIds() {
        NextItemRequest request = new NextItemRequest();
        request.setPickListIds(Collections.emptyList());

        Set<ConstraintViolation<NextItemRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("pickListIds")));
    }
}

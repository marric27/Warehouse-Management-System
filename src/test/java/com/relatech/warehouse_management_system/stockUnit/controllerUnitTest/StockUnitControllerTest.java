package com.relatech.warehouse_management_system.stockUnit.controllerUnitTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.stockUnit.controller.StockUnitController;
import com.relatech.warehouse_management_system.stockUnit.dto.StockUnitDTO;
import com.relatech.warehouse_management_system.stockUnit.service.StockUnitService;
import com.relatech.warehouse_management_system.util.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StockUnitController.class)
@ExtendWith(SpringExtension.class)
class StockUnitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StockUnitService stockUnitService;

    @Autowired
    private ObjectMapper objectMapper;

    private StockUnitDTO buildStockUnitDTO() {
        return StockUnitDTO.builder()
                .id(1L)
                .batchNumber("BN001")
                .uniqueCode("UNIQUE-001")
                .productCode("P001")
                .quantity(10)
                .expirationDate(LocalDate.now().plusDays(30))
                .category(Category.STANDARD)
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/stock-units - create stock unit")
    void testCreateStockUnit() throws Exception {
        StockUnitDTO dto = buildStockUnitDTO();
        Mockito.when(stockUnitService.createStockUnit(any(StockUnitDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/v1/stock-units")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uniqueCode").value("UNIQUE-001"));
    }

    @Test
    @DisplayName("GET /api/v1/stock-units - get all stock units")
    void testGetAllStockUnits() throws Exception {
        StockUnitDTO dto = buildStockUnitDTO();
        Mockito.when(stockUnitService.getAllStockUnits()).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/api/v1/stock-units"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].batchNumber").value("BN001"));
    }

    @Test
    @DisplayName("PATCH /api/v1/stock-units/{stockUnitId}/assign/{productId} - assign product")
    void testAssignProductToStockUnit() throws Exception {
        StockUnitDTO dto = buildStockUnitDTO();
        Mockito.when(stockUnitService.assignProductToStockUnit(eq(1L), eq(2L))).thenReturn(dto);

        mockMvc.perform(patch("/api/v1/stock-units/1/assign/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uniqueCode").value("UNIQUE-001"));
    }

    @Test
    @DisplayName("PATCH /api/v1/stock-units/{stockUnitId}/remove-product - remove product")
    void testRemoveProductFromStockUnit() throws Exception {
        StockUnitDTO dto = buildStockUnitDTO();
        dto.setProductCode(null); // simulate product removed
        Mockito.when(stockUnitService.removeProductFromStockUnit(1L)).thenReturn(dto);

        mockMvc.perform(patch("/api/v1/stock-units/1/remove-product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productCode").doesNotExist());
    }

    @Test
    @DisplayName("DELETE /api/v1/stock-units/{id} - delete stock unit")
    void testDeleteStockUnit() throws Exception {
        Mockito.doNothing().when(stockUnitService).deleteStockUnit(1L);

        mockMvc.perform(delete("/api/v1/stock-units/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PUT /api/v1/stock-units/{id} - update stock unit")
    void testUpdateStockUnit() throws Exception {
        StockUnitDTO dto = buildStockUnitDTO();
        Mockito.when(stockUnitService.updateStockUnit(eq(1L), any(StockUnitDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/v1/stock-units/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.batchNumber").value("BN001"));
    }

    @Test
    @DisplayName("GET /api/v1/stock-units - return empty list")
    void testGetAllStockUnits_Empty() throws Exception {
        Mockito.when(stockUnitService.getAllStockUnits()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/stock-units"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("PATCH /api/v1/stock-units/{id}/assign/{productId} - not found")
    void testAssignProductNotFound() throws Exception {
        Mockito.when(stockUnitService.assignProductToStockUnit(eq(1L), eq(2L)))
                .thenThrow(new ResourceNotFoundException("StockUnit", 1L));

        mockMvc.perform(patch("/api/v1/stock-units/1/assign/2"))
                .andExpect(status().isNotFound());
    }
}

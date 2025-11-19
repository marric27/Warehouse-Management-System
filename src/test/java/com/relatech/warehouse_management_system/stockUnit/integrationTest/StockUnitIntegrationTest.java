package com.relatech.warehouse_management_system.stockUnit.integrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.relatech.warehouse_management_system.stockUnit.dto.StockUnitDTO;
import com.relatech.warehouse_management_system.stockUnit.repository.StockUnitRepository;
import com.relatech.warehouse_management_system.stockUnit.service.StockUnitService;
import com.relatech.warehouse_management_system.util.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class StockUnitIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StockUnitService stockUnitService;

    @Autowired
    private StockUnitRepository stockUnitRepository;

    private StockUnitDTO stockUnitDTO;

    @BeforeEach
    void cleanDatabase() {
        stockUnitRepository.deleteAll();

        stockUnitDTO = StockUnitDTO.builder()
                .batchNumber("BN001")
                .uniqueCode("UNIQUE001")
                .productCode("P001")
                .category(Category.STANDARD)
                .quantity(50)
                .expirationDate(LocalDate.now().plusDays(30))
                .build();
    }

    @Test
    void givenStockUnitExists_whenGetAllStockUnits_thenReturnList() throws Exception {
        stockUnitService.createStockUnit(stockUnitDTO);

        mockMvc.perform(get("/api/v1/stock-units"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uniqueCode").value("UNIQUE001"))
                .andExpect(jsonPath("$[0].quantity").value(50));
    }

    @Test
    void givenStockUnitExists_whenGetStockUnitById_thenReturnStockUnit() throws Exception {
        StockUnitDTO created = stockUnitService.createStockUnit(stockUnitDTO);

        mockMvc.perform(get("/api/v1/stock-units/{id}", created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uniqueCode").value("UNIQUE001"))
                .andExpect(jsonPath("$.quantity").value(50));
    }

    @Test
    void givenNotExistingStockUnit_whenGetStockUnitById_thenReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/stock-units/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenValidStockUnit_whenCreateStockUnit_thenReturnCreated() throws Exception {
        mockMvc.perform(post("/api/v1/stock-units")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockUnitDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uniqueCode").value("UNIQUE001"))
                .andExpect(jsonPath("$.quantity").value(50));
    }

    @Test
    void givenValidStockUnit_whenUpdateStockUnit_thenReturnUpdatedObject() throws Exception {
        StockUnitDTO created = stockUnitService.createStockUnit(stockUnitDTO);
        created.setQuantity(100);
        created.setBatchNumber("BN002");

        mockMvc.perform(put("/api/v1/stock-units/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(100))
                .andExpect(jsonPath("$.batchNumber").value("BN002"));
    }

    @Test
    void givenId_whenDeleteStockUnit_thenReturnNoContent() throws Exception {
        StockUnitDTO created = stockUnitService.createStockUnit(stockUnitDTO);

        mockMvc.perform(delete("/api/v1/stock-units/{id}", created.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/stock-units/{id}", created.getId()))
                .andExpect(status().isNotFound());
    }
}
package com.relatech.warehouse_management_system.stockUnit.integrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDTO;
import com.relatech.warehouse_management_system.goodsIn.entity.repository.StockUnitRepository;
import com.relatech.warehouse_management_system.goodsIn.entity.service.StockUnitService;
import com.relatech.warehouse_management_system.common.util.Category;
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

        mockMvc.perform(get("/stock-units"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uniqueCode").value("UNIQUE001"))
                .andExpect(jsonPath("$[0].quantity").value(50));
    }

    @Test
    void givenStockUnitExists_whenGetStockUnitById_thenReturnStockUnit() throws Exception {
        StockUnitDTO createdStockUnit = stockUnitService.createStockUnit(stockUnitDTO);

        mockMvc.perform(get("/stock-units/{id}", createdStockUnit.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uniqueCode").value("UNIQUE001"))
                .andExpect(jsonPath("$.quantity").value(50));
    }

    @Test
    void givenNotExistingStockUnit_whenGetStockUnitById_thenReturnNotFound() throws Exception {
        mockMvc.perform(get("/stock-units/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenValidStockUnit_whenCreateStockUnit_thenReturnCreated() throws Exception {
        mockMvc.perform(post("/stock-units")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockUnitDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uniqueCode").value("UNIQUE001"))
                .andExpect(jsonPath("$.quantity").value(50));
    }

    @Test
    void givenValidStockUnit_whenUpdateStockUnit_thenReturnUpdatedObject() throws Exception {
        StockUnitDTO createdStockUnit = stockUnitService.createStockUnit(stockUnitDTO);
        createdStockUnit.setQuantity(100);
        createdStockUnit.setBatchNumber("BN002");

        mockMvc.perform(put("/stock-units/{id}", createdStockUnit.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdStockUnit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(100))
                .andExpect(jsonPath("$.batchNumber").value("BN002"));
    }

    @Test
    void givenId_whenDeleteStockUnit_thenReturnNoContent() throws Exception {
        StockUnitDTO createdStockUnit = stockUnitService.createStockUnit(stockUnitDTO);

        mockMvc.perform(delete("/stock-units/{id}", createdStockUnit.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/stock-units/{id}", createdStockUnit.getId()))
                .andExpect(status().isNotFound());
    }
}
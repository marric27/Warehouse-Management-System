package com.relatech.warehouse_management_system.slot.integrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.relatech.warehouse_management_system.warehouse.entity.SlotRepository;
import com.relatech.warehouse_management_system.warehouse.service.SlotService;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDto;
import com.relatech.warehouse_management_system.goodsIn.entity.repository.StockUnitRepository;
import com.relatech.warehouse_management_system.goodsIn.entity.service.StockUnitService;
import com.relatech.warehouse_management_system.common.util.Category;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SlotIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SlotService slotService;

    @Autowired
    private StockUnitService stockUnitService;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private StockUnitRepository stockUnitRepository;

    private final SlotDto slotDTO = new SlotDto(
            null, null, 1, Category.STANDARD, 100, null, null
    );

    private final StockUnitDto stockUnitDTO = new StockUnitDto(
            null, "LOT20251333", LocalDate.now().plusDays(30),
            "PRD-APPLE-006", null, 50, Category.STANDARD, null,null
    );

    @BeforeEach
    void cleanDatabase() {
        stockUnitRepository.deleteAll();
        slotRepository.deleteAll();
    }

    @Test
    void givenSlotExists_whenGetAllSlots_thenReturnList() throws Exception {
        slotService.createSlot(slotDTO);

        mockMvc.perform(get("/slots/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].capacity").value(100));
    }

    @Test
    void givenSlotExists_whenGetSlotById_thenReturnSlot() throws Exception {
        SlotDto createdSlot = slotService.createSlot(slotDTO);

        mockMvc.perform(get("/slots/{id}", createdSlot.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity").value(100));
    }

    @Test
    void givenNotExistingSlot_whenGetSlotById_thenReturnNotFound() throws Exception {
        mockMvc.perform(get("/slots/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenNewSlotWithoutCategory_whenPost_thenReturnBadRequest() throws Exception {
        SlotDto slotWithoutCategory = new SlotDto(null, null, 1,null, 10, null, null);

        mockMvc.perform(post("/slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(slotWithoutCategory)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenValidSlot_whenPost_thenReturnUpdatedObject() throws Exception {
        SlotDto createdSlot = slotService.createSlot(slotDTO);
        createdSlot.setCapacity(30);

        mockMvc.perform(put("/slots/{id}", createdSlot.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdSlot)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity").value(30));
    }

}
package com.relatech.warehouse_management_system.slot.integrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.relatech.warehouse_management_system.slot.repository.SlotRepository;
import com.relatech.warehouse_management_system.slot.service.SlotService;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDTO;
import com.relatech.warehouse_management_system.goodsIn.entity.repository.StockUnitRepository;
import com.relatech.warehouse_management_system.goodsIn.entity.service.StockUnitService;
import com.relatech.warehouse_management_system.common.util.Category;
import com.relatech.warehouse_management_system.slot.dto.SlotDTO;
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

    private final SlotDTO slotDTO = new SlotDTO(
            null, "SLOT001", Category.STANDARD, 100, null, null
    );

    private final StockUnitDTO stockUnitDTO = new StockUnitDTO(
            null, "LOT20251333", LocalDate.now().plusDays(30),
            "PRD-APPLE-006", "SU-0000003331", 50, Category.STANDARD, null,null
    );

    @BeforeEach
    void cleanDatabase() {
        stockUnitRepository.deleteAll();
        slotRepository.deleteAll();
    }

    @Test
    void givenSlotExists_whenGetAllSlots_thenReturnList() throws Exception {
        slotService.createSlot(slotDTO);

        mockMvc.perform(get("/slots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("SLOT001"))
                .andExpect(jsonPath("$[0].capacity").value(100));
    }

    @Test
    void givenSlotExists_whenGetSlotById_thenReturnSlot() throws Exception {
        SlotDTO createdSlot = slotService.createSlot(slotDTO);

        mockMvc.perform(get("/slots/{id}", createdSlot.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SLOT001"))
                .andExpect(jsonPath("$.capacity").value(100));
    }

    @Test
    void givenNotExistingSlot_whenGetSlotById_thenReturnNotFound() throws Exception {
        mockMvc.perform(get("/slots/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenValidSlot_whenCreateSlot_thenReturnCreated() throws Exception {
        mockMvc.perform(post("/slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(slotDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SLOT001"))
                .andExpect(jsonPath("$.capacity").value(100));
    }

    @Test
    void givenNewSlotWithoutCode_whenPost_thenReturnBadRequest() throws Exception {
        SlotDTO slotWithoutCode = new SlotDTO(null, null, Category.FLAMMABLE, 10, null, null);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(slotWithoutCode)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenValidSlot_whenPost_thenReturnUpdatedObject() throws Exception {
        SlotDTO createdSlot = slotService.createSlot(slotDTO);
        createdSlot.setCode("SLOT003");
        createdSlot.setCapacity(30);

        mockMvc.perform(put("/slots/{id}", createdSlot.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdSlot)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity").value(30))
                .andExpect(jsonPath("$.code").value("SLOT003"));
    }

    @Test
    void givenExistingProduct_whenUpdateProductWithExistingCode_thenReturnConflict() throws Exception {
        SlotDTO slotDTO1 = new SlotDTO(null, "SLOT001", Category.STANDARD, 100, null, null);
        SlotDTO slotDTO2 = new SlotDTO(null, "SLOT002", Category.STANDARD, 100, null, null);

        SlotDTO first = slotService.createSlot(slotDTO1);
        SlotDTO toUpdate = slotService.createSlot(slotDTO2);

        toUpdate.setCode(first.getCode()); //duplicated code

        mockMvc.perform(put("/slots/{id}", toUpdate.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toUpdate)))
                .andExpect(status().isConflict());
    }

    @Test
    void givenSlotAndStockUnit_whenAssign_thenReturnUpdatedSlot() throws Exception {
        SlotDTO createdSlot = slotService.createSlot(slotDTO);
        StockUnitDTO createdStockUnit = stockUnitService.createStockUnit(stockUnitDTO);

        mockMvc.perform(patch("/slots/{slotId}/assign/{stockUnitId}", createdSlot.getId(), createdStockUnit.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdSlot.getId()))
                .andExpect(jsonPath("$.stockUnits[0].id").value(createdStockUnit.getId()));
    }

    @Test
    void givenAssignedStockUnit_whenRemove_thenReturnSlotWithoutStockUnit() throws Exception {
        SlotDTO createdSlot = slotService.createSlot(slotDTO);
        StockUnitDTO createdStockUnit = stockUnitService.createStockUnit(stockUnitDTO);

        mockMvc.perform(patch("/slots/{slotId}/assign/{stockUnitId}", createdSlot.getId(), createdStockUnit.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/slots/{slotId}/remove-stock-unit/{stockUnitId}", createdSlot.getId(), createdStockUnit.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockUnits").isEmpty());
    }

    @Test
    void givenWrongCategory_whenAssign_thenReturnBadRequest() throws Exception {
        SlotDTO createdSlot = slotService.createSlot(slotDTO);
        StockUnitDTO createdStockUnit = stockUnitService.createStockUnit(stockUnitDTO);

        createdStockUnit.setCategory(Category.FLAMMABLE);

        mockMvc.perform(put("/stock-units/{id}", createdStockUnit.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdStockUnit)))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/slots/{slotId}/assign/{stockUnitId}", createdSlot.getId(), createdStockUnit.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
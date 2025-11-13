package com.relatech.warehouse_management_system.slot.integrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.relatech.warehouse_management_system.product.dto.ProductDTO;
import com.relatech.warehouse_management_system.product.repository.ProductRepository;
import com.relatech.warehouse_management_system.slot.repository.SlotRepository;
import com.relatech.warehouse_management_system.slot.service.SlotService;
import com.relatech.warehouse_management_system.util.ProductCategory;
import com.relatech.warehouse_management_system.slot.dto.SlotDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
    private SlotRepository slotRepository;

    private final SlotDTO slotDTO = new SlotDTO(
            null, "SLOT001", ProductCategory.STANDARD, 100, null
    );

    @BeforeEach
    void cleanDatabase() {
        slotRepository.deleteAll();
    }

    @Test
    void givenSlotExists_whenGetAllSlots_thenReturnList() throws Exception {
        slotService.createSlot(slotDTO);

        mockMvc.perform(get("/api/slots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("SLOT001"))
                .andExpect(jsonPath("$[0].capacity").value(100));
    }

    @Test
    void givenSlotExists_whenGetSlotById_thenReturnSlot() throws Exception {
        String result = mockMvc.perform(post("/api/slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(slotDTO)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        SlotDTO createdSlot = objectMapper.readValue(result, SlotDTO.class);

        mockMvc.perform(get("/api/slots/{id}", createdSlot.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SLOT001"))
                .andExpect(jsonPath("$.capacity").value(100));
    }

    @Test
    void givenNotExistingSlot_whenGetSlotById_thenReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/slots/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenValidSlot_whenCreateSlot_thenReturnCreated() throws Exception {
        mockMvc.perform(post("/api/slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(slotDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SLOT001"))
                .andExpect(jsonPath("$.capacity").value(100));
    }

    @Test
    void givenNewSlotWithoutCode_whenPost_thenReturnBadRequest() throws Exception {
        SlotDTO slotWithoutCode = new SlotDTO(null, null, ProductCategory.FLAMMABLE, 10, null);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(slotWithoutCode)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenValidSlot_whenPost_thenReturnUpdatedObject() throws Exception {
        String createResult = mockMvc.perform(post("/api/slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(slotDTO)))
                .andReturn().getResponse().getContentAsString();

        SlotDTO created = objectMapper.readValue(createResult, SlotDTO.class);
        created.setCode("SLOT003");
        created.setCapacity(30);

        mockMvc.perform(put("/api/slots/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity").value(30))
                .andExpect(jsonPath("$.code").value("SLOT003"));
    }

    @Test
    void givenExistingProduct_whenUpdateProductWithExistingCode_thenReturnConflict() throws Exception {
        SlotDTO slotDTO1 = new SlotDTO(null, "SLOT001", ProductCategory.STANDARD, 100, null);
        SlotDTO slotDTO2 = new SlotDTO(null, "SLOT002", ProductCategory.STANDARD, 100, null);
        SlotDTO first = slotService.createSlot(slotDTO1);
        SlotDTO toUpdate = slotService.createSlot(slotDTO2);

        slotDTO2.setCode(slotDTO1.getCode()); //duplicated code

        mockMvc.perform(put("/api/slots/{id}", toUpdate.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(slotDTO2)))
                .andExpect(status().isConflict());
    }

    @Test
    void givenId_whenDeleteProduct_thenReturnNoContent() throws Exception {
        String createResult = mockMvc.perform(post("/api/slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(slotDTO)))
                .andReturn().getResponse().getContentAsString();

        ProductDTO created = objectMapper.readValue(createResult, ProductDTO.class);

        mockMvc.perform(delete("/api/slots/{id}", created.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/slots/{id}", created.getId()))
                .andExpect(status().isNotFound());
    }
}
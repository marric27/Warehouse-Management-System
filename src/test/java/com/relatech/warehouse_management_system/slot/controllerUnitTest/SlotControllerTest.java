package com.relatech.warehouse_management_system.slot.controllerUnitTest;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.util.Category;


import com.relatech.warehouse_management_system.slot.controller.SlotController;
import com.relatech.warehouse_management_system.slot.dto.SlotDTO;
import com.relatech.warehouse_management_system.slot.service.SlotService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SlotController.class)
class SlotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SlotService slotService;

    @Autowired
    private ObjectMapper objectMapper;

    private final SlotDTO slotDTO = new SlotDTO(
            1L, "SLOT001", Category.STANDARD, 100, null, null
    );

    //  GET /slots
    @Test
    @DisplayName("GET /slots - should return list of slots")
    void givenSlotsExist_whenGetAllSlots_thenReturnList() throws Exception {
        when(slotService.getAllSlots()).thenReturn(List.of(slotDTO));

        mockMvc.perform(get("/slots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code", is("SLOT001")))
                .andExpect(jsonPath("$[0].capacity", is(100)));
    }

    //  GET /slots/{id}
    @Test
    @DisplayName("GET /slots/{id} - should return slot by id")
    void givenSlotExists_whenGetSlotById_thenReturnSlot() throws Exception {
        when(slotService.getSlotById(1L)).thenReturn(slotDTO);

        mockMvc.perform(get("/slots/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("SLOT001")))
                .andExpect(jsonPath("$.capacity", is(100)));
    }

    @Test
    @DisplayName("GET /slots/{id} - should return 404 when not found")
    void givenSlotNotFound_whenGetSlotById_thenReturnNotFound() throws Exception {
        when(slotService.getSlotById(99L)).thenThrow(new ResourceNotFoundException("Slot", 99L));

        mockMvc.perform(get("/slots/99"))
                .andExpect(status().isNotFound());
    }

    //  POST /slots
    @Test
    @DisplayName("POST /slots - should create new slot")
    void givenValidSlot_whenCreateSlot_thenReturnCreated() throws Exception {
        when(slotService.createSlot(any(SlotDTO.class))).thenReturn(slotDTO);

        mockMvc.perform(post("/slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(slotDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("SLOT001")));
    }

    //  PUT /slots/{id}
    @Test
    @DisplayName("PUT /slots/{id} - should update slot")
    void givenValidSlot_whenUpdateSlot_thenReturnUpdated() throws Exception {
        SlotDTO updated = new SlotDTO(1L, "SLOT002", Category.STANDARD, 200, null, null);
        when(slotService.updateSlot(eq(1L), any(SlotDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/slots/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("SLOT002")))
                .andExpect(jsonPath("$.capacity", is(200)));
    }

    @Test
    @DisplayName("PUT /slots/{id} - should return 404 when slot not found")
    void givenSlotNotFound_whenUpdateSlot_thenReturnNotFound() throws Exception {
        when(slotService.updateSlot(eq(99L), any(SlotDTO.class)))
                .thenThrow(new ResourceNotFoundException("Slot", 99L));

        mockMvc.perform(put("/slots/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(slotDTO)))
                .andExpect(status().isNotFound());
    }

    //  DELETE /slots/{id}
    @Test
    @DisplayName("DELETE /slots/{id} - should delete slot and return no content")
    void givenSlotExists_whenDeleteSlot_thenReturnNoContent() throws Exception {
        doNothing().when(slotService).deleteSlot(1L);

        mockMvc.perform(delete("/slots/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /slots/{id} - should return 404 when slot not found")
    void givenSlotNotFound_whenDeleteSlot_thenReturnNotFound() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Slot", 99L))
                .when(slotService).deleteSlot(99L);

        mockMvc.perform(delete("/slots/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAssignStockUnitToSlot() throws Exception {
        Long slotId = 1L;
        Long stockUnitId = 1L;

        when(slotService.assignStockUnitToSlot(slotId, stockUnitId)).thenReturn(slotDTO);

        mockMvc.perform(patch("/slots/{slotId}/assign/{stockUnitId}", slotId, stockUnitId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(slotDTO.getId()));

        verify(slotService).assignStockUnitToSlot(slotId, stockUnitId);
    }

    @Test
    void testRemoveStockUnitFromSlot() throws Exception {
        Long slotId = 1L;
        Long stockUnitId = 1L;

        when(slotService.removeStockUnitFromSlot(slotId, stockUnitId)).thenReturn(slotDTO);

        mockMvc.perform(patch("/slots/{slotId}/remove-stock-unit/{stockUnitId}", slotId, stockUnitId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(slotDTO.getId()));

        verify(slotService).removeStockUnitFromSlot(slotId, stockUnitId);
    }
}
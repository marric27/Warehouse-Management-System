package com.relatech.warehouse_management_system.grnItem.controller;

import org.junit.jupiter.api.DisplayName;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.grnItem.dto.GrnItemDto;
import com.relatech.warehouse_management_system.grnItem.service.GrnItemService;
import com.relatech.warehouse_management_system.util.State;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GrnItemController.class)
@DisplayName("GrnItemController - REST API Enhanced Tests")
class GrnItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GrnItemService grnItemService;

    @Autowired
    private ObjectMapper objectMapper;

    // -----------------------------------------------------
    // Helper DTO
    // -----------------------------------------------------
    private GrnItemDto createDto() {
        GrnItemDto dto = new GrnItemDto();
        dto.setProductCode("P001");
        dto.setExpectedQty(100);
        dto.setCompliantQty(80);
        dto.setNotCompliantQty(20);
        dto.setReceivedQty(100);
        dto.setState(State.OPEN);
        dto.setCheckingInfoList(null);
        return dto;
    }

    // -----------------------------------------------------
    // POST /grn-items
    // -----------------------------------------------------
    @Test
    void testCreateGrnItem() throws Exception {
        GrnItemDto dto = createDto();

        Mockito.when(grnItemService.createGrnItem(any(GrnItemDto.class)))
                .thenReturn(dto);

        mockMvc.perform(post("/grn-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productCode").value("P001"));
    }

    // -----------------------------------------------------
    // GET /grn-items
    // -----------------------------------------------------
    @Test
    void testGetAllGrnItems() throws Exception {
        Mockito.when(grnItemService.getAllGrnItems())
                .thenReturn(List.of(createDto()));

        mockMvc.perform(get("/grn-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productCode").value("P001"));
    }

    // -----------------------------------------------------
    // GET /grn-items/{id}
    // -----------------------------------------------------
    @Test
    void testGetGrnItemById() throws Exception {
        Mockito.when(grnItemService.getGrnItemById(1L))
                .thenReturn(createDto());

        mockMvc.perform(get("/grn-items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productCode").value("P001"));
    }

    @Test
    void testGetGrnItemById_NotFound() throws Exception {
        Mockito.when(grnItemService.getGrnItemById(99L))
                .thenThrow(new ResourceNotFoundException("GrnItem", 99L));

        mockMvc.perform(get("/grn-items/99"))
                .andExpect(status().isNotFound()); // serve global handler per JSON
    }

    // -----------------------------------------------------
    // PUT /grn-items/{id}
    // -----------------------------------------------------
    @Test
    void testUpdateGrnItem() throws Exception {
        GrnItemDto dto = createDto();
        dto.setProductCode("UPDATED");

        Mockito.when(grnItemService.updateGrnItem(eq(1L), any(GrnItemDto.class)))
                .thenReturn(dto);

        mockMvc.perform(put("/grn-items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productCode").value("UPDATED"));
    }

    // -----------------------------------------------------
    // DELETE /grn-items/{id}
    // -----------------------------------------------------
    @Test
    void testDeleteGrnItem() throws Exception {
        mockMvc.perform(delete("/grn-items/1"))
                .andExpect(status().isNoContent());
    }
}


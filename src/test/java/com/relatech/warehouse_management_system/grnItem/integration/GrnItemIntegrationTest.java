package com.relatech.warehouse_management_system.grnItem.integration;

import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.relatech.warehouse_management_system.grnItem.dto.GrnItemDto;
import com.relatech.warehouse_management_system.grnItem.repository.GrnItemRepository;
import com.relatech.warehouse_management_system.util.State;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("GrnItem API Integration Tests")
class GrnItemIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GrnItemRepository grnItemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        grnItemRepository.deleteAll();
    }

    private GrnItemDto createDto() {
        GrnItemDto dto = new GrnItemDto();
        dto.setCode("Item-001");
        dto.setProductCode("P001");
        dto.setExpectedQty(100);
        dto.setCompliantQty(80);
        dto.setNotCompliantQty(20);
        dto.setReceivedQty(100);
        dto.setState(State.OPEN);
        dto.setCheckingInfoList(null);
        return dto;
    }

    @Test
    @DisplayName("givenValidGrnItem_whenCreateGrnItem_thenReturnsCreatedItem")
    void givenValidGrnItem_whenCreateGrnItem_thenReturnsCreatedItem() throws Exception {
        GrnItemDto dto = createDto();

        mockMvc.perform(post("/grn-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productCode").value("P001"));

        assertThat(grnItemRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("givenGrnItemsExist_whenGetAllGrnItems_thenReturnsList")
    void givenGrnItemsExist_whenGetAllGrnItems_thenReturnsList() throws Exception {
        grnItemRepository.save(
                com.relatech.warehouse_management_system.grnItem.mapper.GrnItemMapper.toEntity(createDto())
        );

        mockMvc.perform(get("/grn-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productCode").value("P001"));
    }

    @Test
    @DisplayName("givenGrnItemExists_whenGetGrnItemById_thenReturnsItem")
    void givenGrnItemExists_whenGetGrnItemById_thenReturnsItem() throws Exception {
        var saved = grnItemRepository.save(
                com.relatech.warehouse_management_system.grnItem.mapper.GrnItemMapper.toEntity(createDto())
        );

        mockMvc.perform(get("/grn-items/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productCode").value("P001"));
    }

    @Test
    @DisplayName("givenGrnItemExists_whenUpdateGrnItem_thenReturnsUpdatedItem")
    void givenGrnItemExists_whenUpdateGrnItem_thenReturnsUpdatedItem() throws Exception {
        var saved = grnItemRepository.save(
                com.relatech.warehouse_management_system.grnItem.mapper.GrnItemMapper.toEntity(createDto())
        );

        GrnItemDto updatedDto = createDto();
        updatedDto.setProductCode("UPDATED");

        mockMvc.perform(put("/grn-items/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productCode").value("UPDATED"));
    }

    @Test
    @DisplayName("givenGrnItemExists_whenDeleteGrnItem_thenItemIsDeleted")
    void givenGrnItemExists_whenDeleteGrnItem_thenItemIsDeleted() throws Exception {
        var saved = grnItemRepository.save(
                com.relatech.warehouse_management_system.grnItem.mapper.GrnItemMapper.toEntity(createDto())
        );

        mockMvc.perform(delete("/grn-items/" + saved.getId()))
                .andExpect(status().isNoContent());

        assertThat(grnItemRepository.findById(saved.getId())).isEmpty();
    }
}

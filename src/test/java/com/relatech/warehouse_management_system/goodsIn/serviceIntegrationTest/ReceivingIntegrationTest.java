package com.relatech.warehouse_management_system.goodsIn.serviceIntegrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.relatech.warehouse_management_system.common.util.Category;
import com.relatech.warehouse_management_system.common.util.State;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnDto;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.product.dto.ProductDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ReceivingIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void testCreateAndFetchGRN() throws Exception {

        GrnDto dto = new GrnDto();
        dto.setSupplier("ACME");
        dto.setReceivingDate(LocalDate.now());

        String body = mapper.writeValueAsString(dto);

        // CREATE
        String response = mockMvc.perform(post("/receiving/grns")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        GrnDto created = mapper.readValue(response, GrnDto.class);

        // FETCH
        mockMvc.perform(get("/receiving/grns/" + created.getId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(created.getId().intValue())))
                .andExpect(jsonPath("$.supplier", is("ACME")))
                .andExpect(jsonPath("$.state", is(State.OPEN.name())));
    }

    @Test
    void testCreateItemAndUpdateItem() throws Exception {
        ProductDto product = new ProductDto();
        product.setName("Test Product");
        product.setCategory(Category.STANDARD);
        String productBody = mapper.writeValueAsString(product);
        String prodResp = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productBody)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ProductDto createdProd = mapper.readValue(prodResp, ProductDto.class);

        // --- Step 1: Create GRN ---
        GrnDto dto = new GrnDto();
        dto.setSupplier("SupplierX");
        dto.setReceivingDate(LocalDate.now());

        String grnBody = mapper.writeValueAsString(dto);

        String resp = mockMvc.perform(post("/receiving/grns")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(grnBody)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        GrnDto createdGrn = mapper.readValue(resp, GrnDto.class);

        // --- Step 2: Create Item ---
        GrnItemDto item = new GrnItemDto();
        item.setProductCode(createdProd.getCode());
        item.setExpectedQty(10);
        item.setReceivedQty(10);
        item.setCompliantQty(10);
        item.setNotCompliantQty(0);

        String itemBody = mapper.writeValueAsString(item);

        String itemResp = mockMvc.perform(
                        post("/receiving/grns/" + createdGrn.getId() + "/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(itemBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        GrnItemDto createdItem = mapper.readValue(itemResp, GrnItemDto.class);

        // --- Step 3: Update Item ---
        createdItem.setReceivedQty(8);
        createdItem.setCompliantQty(8);
        createdItem.setNotCompliantQty(0);

        String updateBody = mapper.writeValueAsString(createdItem);

        mockMvc.perform(
                        put("/receiving/items/" + createdItem.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.receivedQty", is(8)))
                .andExpect(jsonPath("$.compliantQty", is(8)));
    }
}
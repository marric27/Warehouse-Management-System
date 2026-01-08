package com.relatech.warehouse_management_system.product.integrationTest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.relatech.warehouse_management_system.product.dto.ProductDto;
import com.relatech.warehouse_management_system.product.repository.ProductRepository;
import com.relatech.warehouse_management_system.product.service.ProductService;
import com.relatech.warehouse_management_system.common.util.Category;import org.junit.jupiter.api.BeforeEach;
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
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final ProductDto productDTO = new ProductDto(
            null, "P001", "Paracetamolo", Category.STANDARD
    );

    @BeforeEach
    void cleanDatabase() {
        productRepository.deleteAll();
    }

    @Test
    void givenExistingProduct_whenGetProductByCode_thenReturnProduct() throws Exception {
        productService.createProduct(productDTO);

        mockMvc.perform(get("/products/code/{code}", "P001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("P001"))
                .andExpect(jsonPath("$.name").value("Paracetamolo"));
    }

    @Test
    void givenNotExistingProduct_whenGetProductById_thenReturnNotFound() throws Exception {
        mockMvc.perform(get("/products/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenNotExistingProduct_whenGetProductByCode_thenReturnNotFound() throws Exception {
        mockMvc.perform(get("/products/code/{code}", "P999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenExistingProducts_whenGetProductByCategory_thenReturnProduct() throws Exception {
        ProductDto product1 = new ProductDto(null, "C001", "Tachipirina", Category.STANDARD);
        ProductDto product2 = new ProductDto(null, "C991", "Brufen", Category.FLAMMABLE);
        productService.createProduct(product1);
        productService.createProduct(product2);

        mockMvc.perform(get("/products/category/{category}", "STANDARD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("C001"))
                .andExpect(jsonPath("$[0].name").value("Tachipirina"));
    }

    @Test
    void givenProductExists_whenGetAllProducts_thenReturnList() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/products/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Paracetamolo"));
    }

    @Test
    void givenValidProduct_whenCreateProduct_thenReturnCreated() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Paracetamolo"));
    }

    @Test
    void givenValidProduct_whenUpdateProduct_thenReturnUpdatedObject() throws Exception {
        String createResult = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andReturn().getResponse().getContentAsString();

        ProductDto created = objectMapper.readValue(createResult, ProductDto.class);
        created.setName("Aspirina");
        created.setCode("P002");

        mockMvc.perform(put("/products/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Aspirina"));
    }

    @Test
    void givenId_whenDeleteProduct_thenReturnNoContent() throws Exception {
        String createResult = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andReturn().getResponse().getContentAsString();

        ProductDto created = objectMapper.readValue(createResult, ProductDto.class);

        mockMvc.perform(delete("/products/{id}", created.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/products/{id}", created.getId()))
                .andExpect(status().isNotFound());
    }
}
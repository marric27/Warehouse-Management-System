package com.relatech.warehouse_management_system.product.integrationTest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.relatech.warehouse_management_system.product.dto.ProductDTO;
import com.relatech.warehouse_management_system.product.repository.ProductRepository;
import com.relatech.warehouse_management_system.product.service.ProductService;
import com.relatech.warehouse_management_system.util.Category;import org.junit.jupiter.api.BeforeEach;
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

    private final ProductDTO productDTO = new ProductDTO(
            null, "P001", "Paracetamolo", Category.STANDARD, "IT001"
    );

    @BeforeEach
    void cleanDatabase() {
        productRepository.deleteAll();
    }

    @Test
    void givenExistingProduct_whenGetProductByCode_thenReturnProduct() throws Exception {
        productService.createProduct(productDTO);

        mockMvc.perform(get("/api/products/code/{code}", "P001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("P001"))
                .andExpect(jsonPath("$.name").value("Paracetamolo"));
    }

    @Test
    void givenNotExistingProduct_whenGetProductById_thenReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/products/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenNotExistingProduct_whenGetProductByCode_thenReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/products/code/{code}", "P999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenExistingProducts_whenGetProductByCategory_thenReturnProduct() throws Exception {
        ProductDTO product1 = new ProductDTO(null, "C001", "Tachipirina", Category.STANDARD, "IT5");
        ProductDTO product2 = new ProductDTO(null, "C991", "Brufen", Category.FLAMMABLE, "EN6");
        productService.createProduct(product1);
        productService.createProduct(product2);

        mockMvc.perform(get("/api/products/category/{category}", "STANDARD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("C001"))
                .andExpect(jsonPath("$[0].name").value("Tachipirina"));
    }

    @Test
    void givenProductExists_whenGetAllProducts_thenReturnList() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("P001"))
                .andExpect(jsonPath("$[0].name").value("Paracetamolo"));
    }

    @Test
    void givenValidProduct_whenCreateProduct_thenReturnCreated() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("P001"))
                .andExpect(jsonPath("$.name").value("Paracetamolo"));
    }

    @Test
    void whenPostDuplicateMovie_thenReturnConflict() throws Exception {
        productService.createProduct(productDTO);

        ProductDTO duplicate = new ProductDTO(null, "P001", "Brufen", Category.FLAMMABLE, "EN6");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict());
    }

    @Test
    void givenNewProductWithoutCode_whenPost_thenReturnBadRequest() throws Exception {
        ProductDTO prodWithoutCode = new ProductDTO(null, null, "Brufen", Category.FLAMMABLE, "EN6");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(prodWithoutCode)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenValidProduct_whenUpdateProduct_thenReturnUpdatedObject() throws Exception {
        String createResult = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andReturn().getResponse().getContentAsString();

        ProductDTO created = objectMapper.readValue(createResult, ProductDTO.class);
        created.setName("Aspirina");
        created.setCode("P002");

        mockMvc.perform(put("/api/products/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Aspirina"))
                .andExpect(jsonPath("$.code").value("P002"));
    }

    @Test
    void givenExistingProduct_whenUpdateProductWithExistingCode_thenReturnConflict() throws Exception {
        ProductDTO product1 = new ProductDTO(null, "C001", "Tachipirina", Category.STANDARD, "IT5");
        ProductDTO product2 = new ProductDTO(null, "C991", "Brufen", Category.FLAMMABLE, "EN6");
        ProductDTO first = productService.createProduct(product1);
        ProductDTO toUpdate = productService.createProduct(product2);

        product2.setCode(product1.getCode()); //duplicated code

        mockMvc.perform(put("/api/products/{id}", toUpdate.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product2)))
                .andExpect(status().isConflict());
    }

    @Test
    void givenId_whenDeleteProduct_thenReturnNoContent() throws Exception {
        String createResult = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andReturn().getResponse().getContentAsString();

        ProductDTO created = objectMapper.readValue(createResult, ProductDTO.class);

        mockMvc.perform(delete("/api/products/{id}", created.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/products/{id}", created.getId()))
                .andExpect(status().isNotFound());
    }
}
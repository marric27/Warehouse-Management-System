package com.relatech.warehouse_management_system.product.controllerUnitTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.product.controller.ProductController;
import com.relatech.warehouse_management_system.product.dto.ProductDto;
import com.relatech.warehouse_management_system.product.service.ProductService;
import com.relatech.warehouse_management_system.common.util.Category;import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private final ProductDto productDTO = new ProductDto(
            1L, "P001", "Paracetamolo", Category.STANDARD
    );

    //  GET /products/{id}
    @Test
    @DisplayName("GET /products/{id} - should return product by id")
    void givenProductExists_whenGetById_thenReturnProduct() throws Exception {
        when(productService.getProductById(1L)).thenReturn(productDTO);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("P001"))
                .andExpect(jsonPath("$.name").value("Paracetamolo"));
    }

    @Test
    @DisplayName("GET /products/{id} - should return 404 when not found")
    void givenProductNotFound_whenGetById_thenReturn404() throws Exception {
        when(productService.getProductById(99L)).thenThrow(new ResourceNotFoundException("Product",99L));

        mockMvc.perform(get("/products/99"))
                .andExpect(status().isNotFound());
    }

    //  GET /products/code/{code}
    @Test
    @DisplayName("GET /products/code/{code} - should return product by code")
    void givenProductExists_whenGetByCode_thenReturnProduct() throws Exception {
        when(productService.getProductByCode("P001")).thenReturn(productDTO);

        mockMvc.perform(get("/products/code/P001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Paracetamolo"));
    }

    @Test
    @DisplayName("GET /products/code/{code} - should return 404 when not found")
    void givenProductNotFound_whenGetByCode_thenReturn404() throws Exception {
        when(productService.getProductByCode("99")).thenThrow(new ResourceNotFoundException("Product",99));

        mockMvc.perform(get("/products/code/99"))
                .andExpect(status().isNotFound());
    }

    //  GET /products
    @Test
    @DisplayName("GET /products - should return list of products")
    void givenProductsExist_whenGetAll_thenReturnList() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of(productDTO));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].code").value("P001"));
    }

    //  GET /products/category/{category}
    @Test
    @DisplayName("GET /products/category/{category} - should return list by category")
    void givenCategory_whenGetByCategory_thenReturnList() throws Exception {
        when(productService.getAllProductByProductCategory(Category.STANDARD))
                .thenReturn(List.of(productDTO));

        mockMvc.perform(get("/products/category/STANDARD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("STANDARD"));
    }

    //  POST /products
    @Test
    @DisplayName("POST /products - should create new product and return 201")
    void givenValidProduct_whenCreate_thenReturnCreated() throws Exception {
        when(productService.createProduct(any(ProductDto.class))).thenReturn(productDTO);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("P001"));
    }

    //  PUT /products/{id}
    @Test
    @DisplayName("PUT /products/{id} - should update product and return updated DTO")
    void givenProductExists_whenUpdate_thenReturnUpdated() throws Exception {
        ProductDto updated = new ProductDto(1L, "P002", "Aspirina", Category.STANDARD);
        when(productService.updateProduct(eq(1L), any(ProductDto.class))).thenReturn(updated);

        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("P002"))
                .andExpect(jsonPath("$.name").value("Aspirina"));
    }

    @Test
    @DisplayName("PUT /products/{id} - should return 404 when product not found")
    void givenProductNotFound_whenUpdate_thenReturn404() throws Exception {
        when(productService.updateProduct(eq(99L), any(ProductDto.class)))
                .thenThrow(new ResourceNotFoundException("Product",99L));

        mockMvc.perform(put("/products/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isNotFound());
    }

    //  DELETE /products/{id}
    @Test
    @DisplayName("DELETE /products/{id} - should delete and return no content")
    void givenProductExists_whenDelete_thenReturnNoContent() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /products/{id} - should return 404 when product not found")
    void givenProductNotFound_whenDelete_thenReturn404() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Product",99L))
                .when(productService).deleteProduct(99L);

        mockMvc.perform(delete("/products/99"))
                .andExpect(status().isNotFound());
    }
}
package com.relatech.warehouse_management_system.product.controllerUnitTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.product.controller.ProductController;
import com.relatech.warehouse_management_system.product.dto.ProductDTO;
import com.relatech.warehouse_management_system.product.service.ProductService;
import com.relatech.warehouse_management_system.util.ProductCategory;
import org.junit.jupiter.api.DisplayName;
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

    private final ProductDTO productDTO = new ProductDTO(
            1L, "P001", "Paracetamolo", ProductCategory.STANDARD, "IT001"
    );

    //  GET /api/products/{id}
    @Test
    @DisplayName("GET /api/products/{id} - should return product by id")
    void givenProductExists_whenGetById_thenReturnProduct() throws Exception {
        when(productService.getProductById(1L)).thenReturn(productDTO);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("P001"))
                .andExpect(jsonPath("$.name").value("Paracetamolo"));
    }

    @Test
    @DisplayName("GET /api/products/{id} - should return 404 when not found")
    void givenProductNotFound_whenGetById_thenReturn404() throws Exception {
        when(productService.getProductById(99L)).thenThrow(new ResourceNotFoundException("Product",99L));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound());
    }

    //  GET /api/products/code/{code}
    @Test
    @DisplayName("GET /api/products/code/{code} - should return product by code")
    void givenProductExists_whenGetByCode_thenReturnProduct() throws Exception {
        when(productService.getProductByCode("P001")).thenReturn(productDTO);

        mockMvc.perform(get("/api/products/code/P001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Paracetamolo"))
                .andExpect(jsonPath("$.nationalCode").value("IT001"));
    }

    @Test
    @DisplayName("GET /api/products/code/{code} - should return 404 when not found")
    void givenProductNotFound_whenGetByCode_thenReturn404() throws Exception {
        when(productService.getProductByCode("99")).thenThrow(new ResourceNotFoundException("Product",99));

        mockMvc.perform(get("/api/products/code/99"))
                .andExpect(status().isNotFound());
    }

    //  GET /api/products
    @Test
    @DisplayName("GET /api/products - should return list of products")
    void givenProductsExist_whenGetAll_thenReturnList() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of(productDTO));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].code").value("P001"));
    }

    //  GET /api/products/category/{category}
    @Test
    @DisplayName("GET /api/products/category/{category} - should return list by category")
    void givenCategory_whenGetByCategory_thenReturnList() throws Exception {
        when(productService.getAllProductByProductCategory(ProductCategory.STANDARD))
                .thenReturn(List.of(productDTO));

        mockMvc.perform(get("/api/products/category/STANDARD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productCategory").value("STANDARD"));
    }

    //  POST /api/products
    @Test
    @DisplayName("POST /api/products - should create new product and return 201")
    void givenValidProduct_whenCreate_thenReturnCreated() throws Exception {
        when(productService.createProduct(any(ProductDTO.class))).thenReturn(productDTO);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("P001"));
    }

    //  PUT /api/products/{id}
    @Test
    @DisplayName("PUT /api/products/{id} - should update product and return updated DTO")
    void givenProductExists_whenUpdate_thenReturnUpdated() throws Exception {
        ProductDTO updated = new ProductDTO(1L, "P002", "Aspirina", ProductCategory.STANDARD, "IT002");
        when(productService.updateProduct(eq(1L), any(ProductDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("P002"))
                .andExpect(jsonPath("$.name").value("Aspirina"));
    }

    @Test
    @DisplayName("PUT /api/products/{id} - should return 404 when product not found")
    void givenProductNotFound_whenUpdate_thenReturn404() throws Exception {
        when(productService.updateProduct(eq(99L), any(ProductDTO.class)))
                .thenThrow(new ResourceNotFoundException("Product",99L));

        mockMvc.perform(put("/api/products/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isNotFound());
    }

    //  DELETE /api/products/{id}
    @Test
    @DisplayName("DELETE /api/products/{id} - should delete and return no content")
    void givenProductExists_whenDelete_thenReturnNoContent() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - should return 404 when product not found")
    void givenProductNotFound_whenDelete_thenReturn404() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Product",99L))
                .when(productService).deleteProduct(99L);

        mockMvc.perform(delete("/api/products/99"))
                .andExpect(status().isNotFound());
    }
}
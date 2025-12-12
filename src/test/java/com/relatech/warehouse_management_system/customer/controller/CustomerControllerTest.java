package com.relatech.warehouse_management_system.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.relatech.warehouse_management_system.outbound.dto.CustomerDto;
import com.relatech.warehouse_management_system.outbound.entity.service.CustomerService;
import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@DisplayName("CustomerController - REST API Enhanced Tests")
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("POST /customers - Successful Creation")
    void whenValidCustomerDto_thenCreateCustomer() throws Exception {
        CustomerDto input = CustomerDto.builder()
                .name("Mario")
                .surname("Rossi")
                .shippingAddress("Via Roma 1")
                .billingAddress("Via Milano 2")
                .email("mario@rossi.com")
                .taxCode("MRRSSM80A01H501X")
                .build();
        CustomerDto output = input.toBuilder().id(1L).build();

        when(customerService.createCustomer(any(CustomerDto.class))).thenReturn(output);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Mario"));
    }

    @Test
    @DisplayName("POST /customers - Validation Fail (Blank Name)")
    void whenCustomerDtoNameBlank_thenValidationError() throws Exception {
        CustomerDto input = CustomerDto.builder()
                .name("")
                .surname("Rossi")
                .shippingAddress("Via Roma 1")
                .billingAddress("Via Milano 2")
                .email("mario@rossi.com")
                .taxCode("MRRSSM80A01H501X")
                .build();

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /customers/{id} - Found")
    void whenGetCustomerById_thenReturnsCustomer() throws Exception {
        CustomerDto dto = CustomerDto.builder().id(1L).name("Mario").build();
        when(customerService.getCustomerById(1L)).thenReturn(dto);

        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mario"));
    }

    @Test
    @DisplayName("GET /customers/{id} - Not Found")
    void whenGetCustomerByInvalidId_thenReturnsNotFound() throws Exception {
        when(customerService.getCustomerById(99L)).thenThrow(new ResourceNotFoundException("Customer", 99L));

        mockMvc.perform(get("/customers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /customers - Paged Results")
    void whenGetAllCustomersPaged_thenReturnsPagedData() throws Exception {
        CustomerDto dto1 = CustomerDto.builder().id(1L).name("Mario").build();
        CustomerDto dto2 = CustomerDto.builder().id(2L).name("Luigi").build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<CustomerDto> page = new PageImpl<>(List.of(dto1, dto2), pageable, 2);

        when(customerService.getAllCustomersPaged(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/customers").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Mario"));
    }

    @Test
    @DisplayName("PUT /customers/{id} - Update Success")
    void whenUpdateCustomerWithValidData_thenReturnUpdated() throws Exception {
        CustomerDto updateDto = CustomerDto.builder()
                .name("MarioUpdated").surname("Rossi").shippingAddress("New Address").billingAddress("New Billing")
                .email("mario.updated@rossi.com").taxCode("MRRSSM80A01H501X")
                .build();

        CustomerDto updated = updateDto.toBuilder().id(1L).build();

        when(customerService.updateCustomer(eq(1L), any(CustomerDto.class))).thenReturn(updated);

        mockMvc.perform(put("/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("MarioUpdated"));
    }

    @Test
    @DisplayName("DELETE /customers/{id} - Success")
    void whenDeleteCustomer_thenNoContent() throws Exception {
        doNothing().when(customerService).deleteCustomer(1L);

        mockMvc.perform(delete("/customers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /customers/search - With Term")
    void whenSearchCustomersWithTerm_thenReturnMatching() throws Exception {
        List<CustomerDto> results = List.of(CustomerDto.builder().id(1L).name("Mario").build());
        when(customerService.searchCustomers("Mario")).thenReturn(results);

        mockMvc.perform(get("/customers/search").param("term", "Mario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Mario"));
    }

    @Test
    @DisplayName("GET /customers/search - No Term Returns All")
    void whenSearchCustomersNoTerm_thenReturnAll() throws Exception {
        List<CustomerDto> allCustomers = List.of(CustomerDto.builder().id(1L).name("Mario").build());
        when(customerService.searchCustomers(null)).thenReturn(allCustomers);
        when(customerService.searchCustomers("")).thenReturn(allCustomers);

        mockMvc.perform(get("/customers/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Mario"));
    }

    private static String asJsonString(final Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


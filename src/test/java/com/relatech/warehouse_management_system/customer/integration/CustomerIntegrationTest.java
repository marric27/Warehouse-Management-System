package com.relatech.warehouse_management_system.customer.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.relatech.warehouse_management_system.customer.dto.CustomerDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;



import static org.hamcrest.Matchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Customer API Extended Integration Tests")
class CustomerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("Create Customer - Missing Required Field Should Return 400")
    void whenCreateCustomerWithMissingName_thenReturnsBadRequest() throws Exception {
        CustomerDTO invalidCustomer = CustomerDTO.builder()
                .surname("Rossi")
                .shippingAddress("Address1")
                .billingAddress("Address2")
                .email("mario@rossi.com")
                .taxCode("MRRSSM80A01H501X")
                .build();

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidCustomer)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Get All Customers Paged - Verify Pagination Works")
    void whenGetAllCustomersPaged_thenReturnsCorrectPage() throws Exception {

        mockMvc.perform(get("/api/v1/customers")
                        .param("page", "0")
                        .param("size", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(lessThanOrEqualTo(2)))
                .andExpect(jsonPath("$.number").value(0)); // pagina 0
    }

    @Test
    @DisplayName("Search Customers - Return Matches or Empty List")
    void whenSearchCustomers_thenReturnResultsOrEmpty() throws Exception {

        mockMvc.perform(get("/api/v1/customers/search")
                        .param("term", "Rossi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());


        mockMvc.perform(get("/api/v1/customers/search")
                        .param("term", "termNotExpectedToExist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Update Customer - Change Email and Verify Change Persistence")
    void whenUpdateCustomerEmail_thenEmailIsUpdated() throws Exception {
        // Prima crea un customer da aggiornare
        CustomerDTO newCustomer = CustomerDTO.builder()
                .name("Test")
                .surname("User")
                .shippingAddress("Addr1")
                .billingAddress("Addr2")
                .email("testuser@email.com")
                .taxCode("TSTUSR0101012345")
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newCustomer)))
                .andExpect(status().isOk())
                .andReturn();

        CustomerDTO created = fromJsonResult(createResult, CustomerDTO.class);


        CustomerDTO updatedDTO = created.toBuilder().email("updated.email@email.com").build();

        mockMvc.perform(put("/api/v1/customers/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated.email@email.com"));


        mockMvc.perform(get("/api/v1/customers/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated.email@email.com"));
    }

    @Test
    @DisplayName("Delete Customer - Verify Deletion and Subsequent NotFound")
    void whenDeleteCustomer_thenNotFoundOnGet() throws Exception {

        CustomerDTO newCustomer = CustomerDTO.builder()
                .name("Delete")
                .surname("Me")
                .shippingAddress("Addr1")
                .billingAddress("Addr2")
                .email("deleteme@email.com")
                .taxCode("DLTM010101012345")
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newCustomer)))
                .andExpect(status().isOk())
                .andReturn();

        CustomerDTO created = fromJsonResult(createResult, CustomerDTO.class);


        mockMvc.perform(delete("/api/v1/customers/" + created.getId()))
                .andExpect(status().isNoContent());


        mockMvc.perform(get("/api/v1/customers/" + created.getId()))
                .andExpect(status().isNotFound());
    }


    private static String asJsonString(final Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T fromJsonResult(MvcResult result, Class<T> clazz) throws Exception {
        return mapper.readValue(result.getResponse().getContentAsString(), clazz);
    }
}

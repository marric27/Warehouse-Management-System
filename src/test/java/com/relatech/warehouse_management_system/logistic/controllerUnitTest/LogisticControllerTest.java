package com.relatech.warehouse_management_system.logistic.controllerUnitTest;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.logistic.controller.LogisticController;
import com.relatech.warehouse_management_system.logistic.service.LogisticService;
import com.relatech.warehouse_management_system.product.dto.ProductDTO;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDTO;
import com.relatech.warehouse_management_system.common.util.Category;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LogisticController.class)
class LogisticControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LogisticService logisticService;

    @Test
    void assignProductToSlot_ShouldReturnSlotDTOWithProduct() throws Exception {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setCode("PRD-001");
        productDTO.setName("Paracetamolo");
        productDTO.setCategory(Category.STANDARD);

        SlotDTO slotDTO = new SlotDTO();
        slotDTO.setId(1L);
        slotDTO.setCode("SLOT001");
        slotDTO.setAllowedCategory(Category.STANDARD);
        slotDTO.setCapacity(100);
        slotDTO.setProduct(productDTO);

        Mockito.when(logisticService.assignProductToSlot(anyLong(), anyLong()))
                .thenReturn(slotDTO);

        mockMvc.perform(patch("/logistic/1/assign/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("SLOT001"))
                .andExpect(jsonPath("$.product.id").value(1))
                .andExpect(jsonPath("$.product.name").value("Paracetamolo"))
                .andExpect(jsonPath("$.product.category").value("STANDARD"));
    }

    @Test
    void removeProductFromSlot_ShouldReturnSlotDTOWithNullProduct() throws Exception {
        SlotDTO slotDTO = new SlotDTO();
        slotDTO.setId(1L);
        slotDTO.setCode("SLOT001");
        slotDTO.setAllowedCategory(Category.STANDARD);
        slotDTO.setCapacity(100);
        slotDTO.setProduct(null); // prodotto rimosso

        Mockito.when(logisticService.removeProductFromSlot(anyLong()))
                .thenReturn(slotDTO);

        mockMvc.perform(patch("/logistic/1/remove-product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.product").doesNotExist());
    }

    @Test
    void canSlotContainProduct_ShouldReturnTrue() throws Exception {
        Mockito.when(logisticService.canSlotContainProduct(1L, 1L))
                .thenReturn(true);

        mockMvc.perform(get("/logistic/1/can-contain/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void assignProductToSlot_WhenSlotNotFound_ShouldReturn404() throws Exception {
        Mockito.when(logisticService.assignProductToSlot(anyLong(), anyLong()))
                .thenThrow(new ResourceNotFoundException("Slot", 999L));

        mockMvc.perform(patch("/logistic/999/assign/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeProductFromSlot_WhenSlotNotFound_ShouldReturn404() throws Exception {
        Mockito.when(logisticService.removeProductFromSlot(anyLong()))
                .thenThrow(new ResourceNotFoundException("Slot", 999L));

        mockMvc.perform(patch("/logistic/999/remove-product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

package com.relatech.warehouse_management_system.logistic.integrationTest;

import com.relatech.warehouse_management_system.product.dto.ProductDTO;
import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.product.mapper.ProductMapper;
import com.relatech.warehouse_management_system.product.repository.ProductRepository;
import com.relatech.warehouse_management_system.product.service.ProductService;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDto;
import com.relatech.warehouse_management_system.warehouse.entity.Slot;
import com.relatech.warehouse_management_system.warehouse.entity.SlotMapper;
import com.relatech.warehouse_management_system.warehouse.entity.SlotRepository;
import com.relatech.warehouse_management_system.warehouse.service.SlotService;
import com.relatech.warehouse_management_system.common.util.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LogisticIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService productService;

    @Autowired
    private SlotService slotService;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SlotMapper slotMapper;

    private final SlotDto slotDTO = new SlotDto(null, "SLOT001", Category.STANDARD, 100, null, null);
    private final ProductDTO productDTO = new ProductDTO(null, "PRD-001", "Paracetamolo", Category.STANDARD);

    @BeforeEach
    void setup() {
        slotRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void assignProductToSlot_ShouldAssignProduct() throws Exception {
        SlotDto slotDTO = slotService.createSlot(this.slotDTO);
        Slot slot = slotMapper.toEntity(slotDTO);
        ProductDTO productDTO = productService.createProduct(this.productDTO);
        Product product = ProductMapper.toEntity(productDTO);

        mockMvc.perform(patch("/logistic/" + slot.getId() + "/assign/" + product.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(slot.getId()))
                .andExpect(jsonPath("$.product.id").value(productDTO.getId()))
                .andExpect(jsonPath("$.product.name").value("Paracetamolo"))
                .andExpect(jsonPath("$.product.category").value("STANDARD"));
    }

    @Test
    void removeProductFromSlot_ShouldSetProductToNull() throws Exception {
        SlotDto slotDTO = slotService.createSlot(this.slotDTO);
        Slot slot = slotMapper.toEntity(slotDTO);

        productService.createProduct(productDTO);

        slotDTO.setProduct(productDTO);
        slotService.updateSlot(slot.getId(), slotDTO);

        mockMvc.perform(patch("/logistic/" + slot.getId() + "/remove-product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(slot.getId()))
                .andExpect(jsonPath("$.product").doesNotExist());
    }

    @Test
    void canSlotContainProduct_ShouldReturnTrue() throws Exception {
        SlotDto slotDTO = slotService.createSlot(this.slotDTO);
        Slot slot = slotMapper.toEntity(slotDTO);
        ProductDTO productDTO = productService.createProduct(this.productDTO);
        Product product = ProductMapper.toEntity(productDTO);
        mockMvc.perform(get("/logistic/" + slot.getId() + "/can-contain/" + product.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }


    @Test
    void assignProductToSlot_ShouldReturnNotFound_WhenSlotDoesNotExist() throws Exception {
        ProductDTO productDTO = productService.createProduct(this.productDTO);
        Product product = ProductMapper.toEntity(productDTO);

        Long invalidSlotId = 999L;
        mockMvc.perform(patch("/logistic/" + invalidSlotId + "/assign/" + product.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeProductFromSlot_ShouldReturnNotFound_WhenSlotDoesNotExist() throws Exception {
        Long invalidSlotId = 999L;
        mockMvc.perform(patch("/logistic/" + invalidSlotId + "/remove-product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void canSlotContainProduct_ShouldReturnNotFound_WhenSlotDoesNotExist() throws Exception {
        ProductDTO productDTO = productService.createProduct(this.productDTO);
        Product product = ProductMapper.toEntity(productDTO);

        Long invalidSlotId = 999L;
        mockMvc.perform(get("/logistic/" + invalidSlotId + "/can-contain/" + product.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
package com.relatech.warehouse_management_system.logistic.integrationTest;

import com.relatech.warehouse_management_system.product.dto.ProductDTO;
import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.product.mapper.ProductMapper;
import com.relatech.warehouse_management_system.product.repository.ProductRepository;
import com.relatech.warehouse_management_system.product.service.ProductService;
import com.relatech.warehouse_management_system.slot.dto.SlotDTO;
import com.relatech.warehouse_management_system.slot.entity.Slot;
import com.relatech.warehouse_management_system.slot.mapper.SlotMapper;
import com.relatech.warehouse_management_system.slot.repository.SlotRepository;
import com.relatech.warehouse_management_system.slot.service.SlotService;
import com.relatech.warehouse_management_system.util.ProductCategory;
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

    private final SlotDTO slotDTO = new SlotDTO(null, "SLOT001", ProductCategory.STANDARD, 100, null);
    private final ProductDTO productDTO = new ProductDTO(null, "PRD-001", "Paracetamolo", ProductCategory.STANDARD, "IT123");

    @BeforeEach
    void setup() {
        slotRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void assignProductToSlot_ShouldAssignProduct() throws Exception {
        SlotDTO slotDTO = slotService.createSlot(this.slotDTO);
        Slot slot = SlotMapper.toEntity(slotDTO);
        ProductDTO productDTO = productService.createProduct(this.productDTO);
        Product product = ProductMapper.toEntity(productDTO);

        mockMvc.perform(patch("/api/logistic/" + slot.getId() + "/assign/" + product.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(slot.getId()))
                .andExpect(jsonPath("$.code").value("SLOT001"))
                .andExpect(jsonPath("$.product.id").value(productDTO.getId()))
                .andExpect(jsonPath("$.product.name").value("Paracetamolo"))
                .andExpect(jsonPath("$.product.productCategory").value("STANDARD"))
                .andExpect(jsonPath("$.product.nationalCode").value("IT123"));
    }

    @Test
    void removeProductFromSlot_ShouldSetProductToNull() throws Exception {
        SlotDTO slotDTO = slotService.createSlot(this.slotDTO);
        Slot slot = SlotMapper.toEntity(slotDTO);

        productService.createProduct(productDTO);

        slotDTO.setProduct(productDTO);
        slotService.updateSlot(slot.getId(), slotDTO);

        mockMvc.perform(patch("/api/logistic/" + slot.getId() + "/remove-product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(slot.getId()))
                .andExpect(jsonPath("$.product").doesNotExist());
    }

    @Test
    void canSlotContainProduct_ShouldReturnTrue() throws Exception {
        SlotDTO slotDTO = slotService.createSlot(this.slotDTO);
        Slot slot = SlotMapper.toEntity(slotDTO);
        ProductDTO productDTO = productService.createProduct(this.productDTO);
        Product product = ProductMapper.toEntity(productDTO);
        mockMvc.perform(get("/api/logistic/" + slot.getId() + "/can-contain/" + product.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }


    @Test
    void assignProductToSlot_ShouldReturnNotFound_WhenSlotDoesNotExist() throws Exception {
        ProductDTO productDTO = productService.createProduct(this.productDTO);
        Product product = ProductMapper.toEntity(productDTO);

        Long invalidSlotId = 999L;
        mockMvc.perform(patch("/api/logistic/" + invalidSlotId + "/assign/" + product.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeProductFromSlot_ShouldReturnNotFound_WhenSlotDoesNotExist() throws Exception {
        Long invalidSlotId = 999L;
        mockMvc.perform(patch("/api/logistic/" + invalidSlotId + "/remove-product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void canSlotContainProduct_ShouldReturnNotFound_WhenSlotDoesNotExist() throws Exception {
        ProductDTO productDTO = productService.createProduct(this.productDTO);
        Product product = ProductMapper.toEntity(productDTO);

        Long invalidSlotId = 999L;
        mockMvc.perform(get("/api/logistic/" + invalidSlotId + "/can-contain/" + product.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
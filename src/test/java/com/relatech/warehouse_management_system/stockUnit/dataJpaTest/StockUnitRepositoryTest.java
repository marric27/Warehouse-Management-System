package com.relatech.warehouse_management_system.stockUnit.repository;

import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.product.repository.ProductRepository;
import com.relatech.warehouse_management_system.slot.entity.Slot;
import com.relatech.warehouse_management_system.slot.repository.SlotRepository;
import com.relatech.warehouse_management_system.stockUnit.entity.StockUnit;
import com.relatech.warehouse_management_system.util.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class StockUnitRepositoryTest {

    @Autowired
    private StockUnitRepository stockUnitRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SlotRepository slotRepository;

    private StockUnit buildStockUnit() {
        return StockUnit.builder()
                .batchNumber("BN123")
                .expirationDate(LocalDate.now().plusDays(30))
                .productCode("P001")
                .uniqueCode("UNIQUE-123")
                .quantity(10)
                .category(Category.STANDARD)
                .build();
    }

    private Product createTestProduct() {
        Product product = new Product();
        product.setCode("P123");
        product.setName("Paracetamolo");
        product.setCategory(Category.STANDARD);
        return productRepository.save(product);
    }

    private Slot createTestSlot() {
        Slot slot = new Slot();
        slot.setCode("SLOT001");
        slot.setAllowedCategory(Category.STANDARD);
        slot.setCapacity(100);
        return slot;
    }

    @Test
    @DisplayName("Should save StockUnit successfully")
    void testSave() {
        StockUnit su = buildStockUnit();

        StockUnit saved = stockUnitRepository.save(su);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUniqueCode()).isEqualTo("UNIQUE-123");
    }

    @Test
    @DisplayName("Should find StockUnit by ID")
    void testFindById() {
        StockUnit su = stockUnitRepository.save(buildStockUnit());

        StockUnit found = stockUnitRepository.findById(su.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getBatchNumber()).isEqualTo("BN123");
    }

    @Test
    @DisplayName("Should find StockUnit by uniqueCode")
    void testFindByUniqueCode() {
        stockUnitRepository.save(buildStockUnit());

        StockUnit found = stockUnitRepository.findByUniqueCode("UNIQUE-123").orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getProductCode()).isEqualTo("P001");
    }

    @Test
    @DisplayName("Should fail saving duplicate uniqueCode")
    void testUniqueConstraint() {
        stockUnitRepository.save(buildStockUnit());

        StockUnit duplicate = buildStockUnit();
        duplicate.setBatchNumber("BN999"); // change something

        assertThatThrownBy(() -> stockUnitRepository.saveAndFlush(duplicate))
                .isInstanceOf(Exception.class);   // DataIntegrityViolationException in runtime
    }

    @Test
    @DisplayName("Should save StockUnit with Product relationship")
    void testProductRelation() {
        Product p = productRepository.save(createTestProduct());

        StockUnit su = buildStockUnit();
        su.setProduct(p);

        StockUnit saved = stockUnitRepository.save(su);

        assertThat(saved.getProduct()).isNotNull();
        assertThat(saved.getProduct().getName()).isEqualTo("Paracetamolo");
    }

    @Test
    @DisplayName("Should save StockUnit with Slot relationship")
    void testSlotRelation() {
        Slot s = slotRepository.save(createTestSlot());

        StockUnit su = buildStockUnit();
        su.setSlot(s);

        StockUnit saved = stockUnitRepository.save(su);

        assertThat(saved.getSlot()).isNotNull();
        assertThat(saved.getSlot().getCode()).isEqualTo("SLOT001");
    }

}

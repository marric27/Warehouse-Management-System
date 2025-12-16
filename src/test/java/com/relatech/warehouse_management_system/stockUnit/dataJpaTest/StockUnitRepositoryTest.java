package com.relatech.warehouse_management_system.stockUnit.dataJpaTest;

import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.product.repository.ProductRepository;
import com.relatech.warehouse_management_system.warehouse.entity.Slot;
import com.relatech.warehouse_management_system.warehouse.entity.SlotRepository;
import com.relatech.warehouse_management_system.goodsIn.entity.StockUnit;
import com.relatech.warehouse_management_system.goodsIn.entity.repository.StockUnitRepository;
import com.relatech.warehouse_management_system.common.util.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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
                .code("UNIQUE-123")
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
        slot.setPickingSequence(1);
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
        assertThat(saved.getCode()).isEqualTo("UNIQUE-123");
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
    @DisplayName("Should find StockUnit by code")
    void testFindByUniqueCode() {
        stockUnitRepository.save(buildStockUnit());

        StockUnit found = stockUnitRepository.findByCode("UNIQUE-123").orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getProductCode()).isEqualTo("P001");
    }

    @Test
    @DisplayName("Should fail saving duplicate code")
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
    @Test
    void givenNullProduct_whenCanContain_thenReturnFalse() {
        StockUnit su = StockUnit.builder()
                .category(Category.STANDARD)
                .build();

        assertFalse(su.canContain(null));
    }

    @Test
    void givenProductWithSameCategory_whenCanContain_thenReturnTrue() {
        StockUnit su = StockUnit.builder()
                .category(Category.STANDARD)
                .build();

        Product p = createTestProduct();

        assertTrue(su.canContain(p));
    }

    @Test
    void givenProductWithDifferentCategory_whenCanContain_thenReturnFalse() {
        StockUnit su = StockUnit.builder()
                .category(Category.STANDARD)
                .build();

        Product p = createTestProduct();
        p.setCategory(Category.FLAMMABLE);

        assertFalse(su.canContain(p));
    }

    @Test
    void givenProductWithWrongCategory_whenAddProduct_thenThrowException() {
        StockUnit su = StockUnit.builder()
                .category(Category.STANDARD)
                .build();

        Product p = createTestProduct();
        p.setCategory(Category.FLAMMABLE);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> su.addProduct(p)
        );

        assertEquals("Product category not allowed in this stock unit", ex.getMessage());
    }

    @Test
    void givenDifferentProductAlreadyAssigned_whenAddProduct_thenThrowException() {
        Product p = createTestProduct();
        StockUnit su = StockUnit.builder()
                .category(Category.STANDARD)
                .product(p)
                .build();

        Product newProduct = new Product(null, "Code2", "newName", Category.STANDARD);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> su.addProduct(newProduct)
        );

        assertEquals("This stock unit already contains another product type", ex.getMessage());
    }

    @Test
    void givenValidProduct_whenAddProduct_thenAssignCorrectly() {
        StockUnit su = StockUnit.builder()
                .category(Category.STANDARD)
                .build();

        Product p = createTestProduct();

        su.addProduct(p);

        assertEquals(p, su.getProduct());
    }
}

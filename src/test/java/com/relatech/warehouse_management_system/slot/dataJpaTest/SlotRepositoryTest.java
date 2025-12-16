package com.relatech.warehouse_management_system.slot.dataJpaTest;

import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.product.repository.ProductRepository;
import com.relatech.warehouse_management_system.goodsIn.entity.StockUnit;
import com.relatech.warehouse_management_system.common.util.Category;
import com.relatech.warehouse_management_system.warehouse.entity.Slot;
import com.relatech.warehouse_management_system.warehouse.entity.SlotRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class SlotRepositoryTest {

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private ProductRepository productRepository;


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
    @DisplayName("Given slot saved, when findById, then return slot")
    void givenSlotSaved_whenFindById_thenReturnSlot() {
        Slot saved = slotRepository.save(createTestSlot());
        Optional<Slot> found = slotRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("SLOT001");
    }

    @Test
    @DisplayName("Given slot saved, when findByCode, then return slot")
    void givenSlotSaved_whenFindByCode_thenReturnSlot() {
        Slot slot = slotRepository.save(createTestSlot());
        Optional<Slot> found = slotRepository.findByCode("SLOT001");

        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo(slot.getCode());
    }

    @Test
    @DisplayName("Given slot with same code, when save again, then throw DataIntegrityViolationException")
    void givenDuplicateCode_whenSave_thenThrowDataIntegrityViolationException() {
        slotRepository.save(createTestSlot());
        Slot duplicate = createTestSlot();

        assertThrows(DataIntegrityViolationException.class, () -> slotRepository.saveAndFlush(duplicate));
    }

    @Test
    @DisplayName("Given slot with product, when save, then relation is persisted")
    void givenSlotWithProduct_whenSave_thenRelationPersisted() {
        Product product = createTestProduct();
        Slot slot = createTestSlot();
        slot.setProd(product);

        Slot saved = slotRepository.save(slot);
        Optional<Slot> found = slotRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getProd()).isNotNull();
        assertThat(found.get().getProd().getCode()).isEqualTo("P123");
    }

    @Test
    @DisplayName("Given slot saved, when findByProductCategory, then return list of slots")
    void givenSlotSaved_whenFindByProductCategory_thenReturnList() {
        slotRepository.save(createTestSlot());
        List<Slot> found = slotRepository.findByAllowedCategory(Category.STANDARD);

        assertThat(found).isNotEmpty();
        assertThat(found.getFirst().getAllowedCategory()).isEqualTo(Category.STANDARD);
    }

    @Test
    @DisplayName("Given slot, when addProduct with matching category, then assign product")
    void givenSlot_whenAddProductWithMatchingCategory_thenAssignProduct() {
        Product product = createTestProduct();
        Slot slot = createTestSlot();

        slot.addProduct(product);
        assertThat(slot.getProd()).isEqualTo(product);
    }

    @Test
    @DisplayName("Given slot, when addProduct with wrong category, then throw exception")
    void givenSlot_whenAddProductWithWrongCategory_thenThrowException() {
        Product product = createTestProduct();
        Slot slot = createTestSlot();
        slot.setAllowedCategory(Category.FLAMMABLE);
        assertThrows(IllegalArgumentException.class, () -> slot.addProduct(product));
    }

    @Test
    @DisplayName("Given slot with a product, when add different product, then throw exception")
    void givenSlotWithProduct_whenAddDifferentProduct_thenThrowException() {
        Product p1 = createTestProduct();
        Product p2 = new Product();
        p2.setCode("P999");
        p2.setName("Aspirina");
        p2.setCategory(Category.STANDARD);
        productRepository.save(p2);

        Slot slot = createTestSlot();
        slot.addProduct(p1);

        assertThrows(IllegalArgumentException.class, () -> slot.addProduct(p2));
    }

    @Test
    void givenNullStockUnit_whenAddStockUnit_thenThrowException() {
        Slot slot = Slot.builder()
                .allowedCategory(Category.STANDARD)
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> slot.addStockUnit(null)
        );
        assertEquals("StockUnit cannot be null", exception.getMessage());
    }

    @Test
    void givenWrongCategory_whenAddStockUnit_thenThrowException() {
        Slot slot = Slot.builder()
                .allowedCategory(Category.STANDARD)
                .build();

        StockUnit stockUnit = StockUnit.builder()
                .category(Category.FLAMMABLE) // NON CONSENTITA
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> slot.addStockUnit(stockUnit)
        );
        assertEquals("Category not allowed in this slot", exception.getMessage());
    }
}



package com.relatech.warehouse_management_system.grnItem.repository;

import com.relatech.warehouse_management_system.goodsIn.entity.repository.GrnItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.relatech.warehouse_management_system.goodsIn.entity.GrnItem;
import com.relatech.warehouse_management_system.common.util.State;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("GrnItemRepository - CRUD and Query Tests")
class GrnItemRepositoryTest {

    @Autowired
    private GrnItemRepository grnItemRepository;

    private GrnItem createTestGrnItem() {
        GrnItem item = new GrnItem();
        item.setCode("Item-001");
        item.setProductCode("P001");
        item.setExpectedQty(100);
        item.setReceivedQty(90);
        item.setCompliantQty(80);
        item.setNotCompliantQty(10);
        item.setState(State.OPEN);
        item.setCheckingInfoList(null); // opzionale
        return item;
    }

    @Test
    @DisplayName("givenValidGrnItem_whenSaveAndFindById_thenReturnsSameEntity")
    void givenValidGrnItem_whenSaveAndFindById_thenReturnsSameEntity() {
        GrnItem saved = grnItemRepository.save(createTestGrnItem());

        assertThat(saved.getId()).isNotNull();

        GrnItem found = grnItemRepository.findById(saved.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getCode()).isEqualTo("Item-001");
        assertThat(found.getProductCode()).isEqualTo("P001");
    }

    @Test
    @DisplayName("givenExistingGrnItem_whenUpdate_thenValuesAreChanged")
    void givenExistingGrnItem_whenUpdate_thenValuesAreChanged() {
        GrnItem saved = grnItemRepository.save(createTestGrnItem());

        saved.setProductCode("UPDATED");
        saved.setExpectedQty(120);

        GrnItem updated = grnItemRepository.save(saved);

        assertThat(updated.getProductCode()).isEqualTo("UPDATED");
        assertThat(updated.getExpectedQty()).isEqualTo(120);
    }

    @Test
    @DisplayName("givenExistingGrnItem_whenDelete_thenEntityIsRemoved")
    void givenExistingGrnItem_whenDelete_thenEntityIsRemoved() {
        GrnItem saved = grnItemRepository.save(createTestGrnItem());
        Long id = saved.getId();

        grnItemRepository.deleteById(id);

        assertThat(grnItemRepository.findById(id)).isEmpty();
    }
}

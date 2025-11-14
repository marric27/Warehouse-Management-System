package com.relatech.warehouse_management_system.product.dataJpaTest;

import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.product.repository.ProductRepository;
import com.relatech.warehouse_management_system.util.ProductCategory;
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
public class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    private void createTestProduct() {
        testProduct = new Product();
        testProduct.setCode("P123");
        testProduct.setName("Paracetamolo");
        testProduct.setProductCategory(ProductCategory.STANDARD);
        testProduct.setNationalCode("IT001");
    }

    @Test
    @DisplayName("Save and findbyid a product")
    void givenProductSaved_whenFindById_thenProductIsFound() {
        createTestProduct();

        Product saved = productRepository.save(testProduct);

        Optional<Product> found = productRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("findByCode")
    void givenProductSaved_whenFindByCode_thenCorrectProductIsReturned() {
        createTestProduct();
        productRepository.save(testProduct);

        Optional<Product> found = productRepository.findByCode(testProduct.getCode());

        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("P123");
    }

    @Test
    @DisplayName("find by non existing code")
    void givenNoProductWithCode_whenFindByCode_thenReturnEmpty() {
        Optional<Product> result = productRepository.findByCode("NON_EXISTENT");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Test find by productcategory")
    void givenProductSaved_whenFindByProductCategory_thenCorrectProductListIsReturned() {
        createTestProduct();
        productRepository.save(testProduct);

        List<Product> found = productRepository.findByProductCategory(ProductCategory.STANDARD);

        assertThat(found)
                .isNotEmpty()
                .hasSize(1);

        Product productFound = found.getFirst();
        assertEquals(productFound, testProduct);
        assertThat(productFound.getName()).isEqualTo("Paracetamolo");
        assertThat(productFound.getProductCategory()).isEqualTo(ProductCategory.STANDARD);
    }

    @Test
    void givenDuplicateCode_whenSaved_thenThrowDataIntegrityViolationException() {
        createTestProduct();
        productRepository.saveAndFlush(testProduct);

        Product duplicateProduct = new Product();
        duplicateProduct.setCode(testProduct.getCode());

        assertThrows(
                DataIntegrityViolationException.class,
                () -> productRepository.saveAndFlush(duplicateProduct)
        );
    }

    @Test
    @DisplayName("Delete a product")
    void givenProductSaved_whenDeleted_thenProductNoLongerExists() {
        createTestProduct();
        Product product = productRepository.save(testProduct);

        productRepository.deleteById(product.getId());

        Optional<Product> found = productRepository.findById(product.getId());
        assertThat(found).isEmpty();
    }
}
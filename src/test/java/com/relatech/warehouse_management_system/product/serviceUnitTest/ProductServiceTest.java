package com.relatech.warehouse_management_system.product.serviceUnitTest;

import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.product.dto.ProductDTO;
import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.product.mapper.ProductMapper;
import com.relatech.warehouse_management_system.product.repository.ProductRepository;
import com.relatech.warehouse_management_system.product.service.ProductServiceImpl;
import com.relatech.warehouse_management_system.util.Category;import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {

        product = new Product();
        product.setCode("P001");
        product.setName("Paracetamolo");
        product.setCategory(Category.STANDARD);

        productDTO = ProductMapper.toDto(product);
    }

    @Test
    @DisplayName("Given product exists, when getProductById, then return ProductDTO")
    void givenProductExists_whenGetProductById_thenReturnProductDTO() throws ResourceNotFoundException {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        ProductDTO result = productService.getProductById(product.getId());

        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("P001");
        verify(productRepository, times(1)).findById(product.getId());
    }

    @Test
    @DisplayName("Given product does not exist, when getProductById, then throw ResourceNotFoundException")
    void givenProductDoesNotExist_whenGetProductById_thenThrowException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));

        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Given valid productDTO, when createProduct, then save and return ProductDTO")
    void givenValidProductDTO_whenCreateProduct_thenReturnSavedProductDTO() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDTO result = productService.createProduct(productDTO);

        assertThat(result.getCode()).isEqualTo("P001");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Given existing product, when updateProduct, then update fields and return ProductDTO")
    void givenExistingProduct_whenUpdateProduct_thenReturnUpdatedDTO() throws Exception {
        ProductDTO updatedDTO = new ProductDTO(1L, "P002", "Aspirina", Category.STANDARD);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(ProductMapper.toEntity(updatedDTO));

        ProductDTO result = productService.updateProduct(1L, updatedDTO);

        assertThat(result.getCode()).isEqualTo("P002");
        assertThat(result.getName()).isEqualTo("Aspirina");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Given product does not exist, when updateProduct, then throw ResourceNotFoundException")
    void givenNonExistingProduct_whenUpdateProduct_thenThrowException() {
        when(productRepository.findById(10L)).thenReturn(Optional.empty());
        ProductDTO dto = new ProductDTO(10L, "X123", "Ibuprofene", Category.STANDARD);

        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(10L, dto));

        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Given product exists, when deleteProduct, then repository deleteById called")
    void givenProductExists_whenDeleteProduct_thenSuccess() throws ResourceNotFoundException {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Given product does not exist, when deleteProduct, then throw ResourceNotFoundException")
    void givenProductDoesNotExist_whenDeleteProduct_thenThrowException() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(99L));

        verify(productRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Given multiple products, when getAllProducts, then return list of ProductDTO")
    void givenMultipleProducts_whenGetAllProducts_thenReturnList() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductDTO> result = productService.getAllProducts();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCode()).isEqualTo("P001");
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Given products with category, when getAllByProductCategory, then return list of ProductDTO")
    void givenProductsWithCategory_whenGetAllByProductCategory_thenReturnList() {
        when(productRepository.findByCategory(Category.STANDARD)).thenReturn(List.of(product));

        List<ProductDTO> result = productService.getAllProductByProductCategory(Category.STANDARD);

        assertThat(result).isNotEmpty();
        assertThat(result.getFirst().getCategory()).isEqualTo(Category.STANDARD);
        verify(productRepository, times(1)).findByCategory(Category.STANDARD);
    }
}
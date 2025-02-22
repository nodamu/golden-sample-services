package com.backbase.goldensample.product.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.backbase.goldensample.product.mapper.ProductMapper;
import com.backbase.goldensample.product.persistence.ProductEntity;
import com.backbase.goldensample.product.persistence.ProductRepository;
import com.backbase.product.api.service.v1.model.Product;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

    private static final LocalDate TODAY = LocalDate.of(2020, 1, 28);

    private final Product product = new Product().productId(1L).name("Product").weight(20).createDate(TODAY);
    private final ProductEntity productEntity = new ProductEntity(1L, "Product1", 20, TODAY, Collections.singletonMap("popularity","29%"));

    @BeforeEach
    public void init() {
        productService = new ProductServiceImpl(productRepository, productMapper);
    }

    @Test
    void getAllProductsTest() {
        List<ProductEntity> list = new ArrayList<>();
        ProductEntity productOne = new ProductEntity(1L, "Product1", 20, TODAY, null);
        ProductEntity productTwo = new ProductEntity(2L, "Product2", 21, TODAY, null);
        ProductEntity productThree = new ProductEntity(3L, "Product3", 22, TODAY, null);

        list.add(productOne);
        list.add(productTwo);
        list.add(productThree);

        when(productRepository.findAll()).thenReturn(list);

        List<Product> empList = productService.getAllProducts();

        assertEquals(3, empList.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductByIdTest() {
        when(productRepository.findById(1L)).thenReturn(java.util.Optional.of(productEntity));

        Product product = productService.getProduct(1, 0, 0);

        assertAll(
            () -> assertEquals("Product1", product.getName()),
            () -> assertEquals(20, product.getWeight()),
            () -> assertEquals(TODAY, product.getCreateDate()));
    }

    @Test
    void getProductByIdWithDelayTest() {
        when(productRepository.findById(1L)).thenReturn(java.util.Optional.of(productEntity));

        Product product = productService.getProduct(1, 1, 0);

        assertAll(
            () -> assertEquals("Product1", product.getName()),
            () -> assertEquals(20, product.getWeight()),
            () -> assertEquals(TODAY, product.getCreateDate()));
    }

    @Test
    void getProductByIdWithErrorTest() {
        assertThrows(RuntimeException.class, () -> productService.getProduct(1, 0, 100));
    }

    @Test
    void createProductTest() {
        productService.createProduct(product);
        verify(productRepository, times(1)).save(any(ProductEntity.class));
    }

    @Test
    void updateProductTest() {
        productService.updateProduct(product);
        verify(productRepository, times(1)).save(any(ProductEntity.class));
    }

    @Test
    void deleteProductTest() {
        when(productRepository.findById(1L)).thenReturn(java.util.Optional.of(productEntity));
        productService.deleteProduct(product.getProductId());
        verify(productRepository, times(1)).delete(any(ProductEntity.class));
    }
}

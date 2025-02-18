package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(100);
    }

    @Test
    void testCreateProductWithNullId() {
        Product inputProduct = new Product();
        inputProduct.setProductName("Test Product");
        inputProduct.setProductQuantity(10);
        inputProduct.setProductId(null);

        when(productRepository.create(any(Product.class))).thenReturn(inputProduct);

        Product result = productService.create(inputProduct);

        assertNotNull(result.getProductId());
        assertEquals(inputProduct.getProductName(), result.getProductName());
        assertEquals(inputProduct.getProductQuantity(), result.getProductQuantity());
        verify(productRepository).create(inputProduct);
    }

    @Test
    void testCreateProductWithEmptyId() {
        Product inputProduct = new Product();
        inputProduct.setProductName("Test Product");
        inputProduct.setProductQuantity(10);
        inputProduct.setProductId("");

        when(productRepository.create(any(Product.class))).thenReturn(inputProduct);

        Product result = productService.create(inputProduct);

        assertNotNull(result.getProductId());
        assertEquals(inputProduct.getProductName(), result.getProductName());
        assertEquals(inputProduct.getProductQuantity(), result.getProductQuantity());
        verify(productRepository).create(inputProduct);
    }



    @Test
    void testCreateProductWithExistingId() {
        when(productRepository.create(product)).thenReturn(product);

        Product result = productService.create(product);

        assertEquals(product.getProductId(), result.getProductId());
        verify(productRepository).create(product);
    }

    @Test
    void testFindAllProducts() {
        Product product2 = new Product();
        product2.setProductId("a0f9de45-90b1-437d-a0bf-d0821dde9096");

        List<Product> productList = Arrays.asList(product, product2);
        Iterator<Product> iterator = productList.iterator();

        when(productRepository.findAll()).thenReturn(iterator);

        List<Product> result = productService.findAll();

        assertEquals(2, result.size());
        verify(productRepository).findAll();
    }

    @Test
    void testFindByIdExists() {
        when(productRepository.findById(product.getProductId())).thenReturn(product);

        Product result = productService.findById(product.getProductId());

        assertNotNull(result);
        assertEquals(product.getProductId(), result.getProductId());
        verify(productRepository).findById(product.getProductId());
    }

    @Test
    void testFindByIdNotExists() {
        when(productRepository.findById("nonexistent")).thenReturn(null);

        Product result = productService.findById("nonexistent");

        assertNull(result);
        verify(productRepository).findById("nonexistent");
    }

    @Test
    void testUpdateProductSuccess() {
        when(productRepository.update(product)).thenReturn(product);

        Product result = productService.update(product);

        assertNotNull(result);
        assertEquals(product.getProductId(), result.getProductId());
        verify(productRepository).update(product);
    }

    @Test
    void testUpdateProductWithZeroQuantity() {
        Product updateProduct = new Product();
        updateProduct.setProductId("test-id");
        updateProduct.setProductName("Test Product");
        updateProduct.setProductQuantity(0);

        when(productRepository.update(updateProduct)).thenReturn(updateProduct);

        Product result = productService.update(updateProduct);

        assertNotNull(result);
        assertEquals(0, result.getProductQuantity());
        verify(productRepository).update(updateProduct);
    }

    @Test
    void testUpdateProductWithNegativeQuantity() {
        Product updateProduct = new Product();
        updateProduct.setProductId("test-id");
        updateProduct.setProductName("Test Product");
        updateProduct.setProductQuantity(-1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.update(updateProduct);
        });

        assertEquals("Product quantity cannot be negative", exception.getMessage());
        verify(productRepository, never()).update(any());
    }

    @Test
    void testUpdateProductWithEmptyName() {
        Product updateProduct = new Product();
        updateProduct.setProductId("test-id");
        updateProduct.setProductName("");
        updateProduct.setProductQuantity(100);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.update(updateProduct);
        });

        assertEquals("Product name cannot be empty", exception.getMessage());
        verify(productRepository, never()).update(any());
    }

    @Test
    void testUpdateProductWithBlankName() {
        Product updateProduct = new Product();
        updateProduct.setProductId("test-id");
        updateProduct.setProductName("   ");
        updateProduct.setProductQuantity(100);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.update(updateProduct);
        });

        assertEquals("Product name cannot be empty", exception.getMessage());
        verify(productRepository, never()).update(any());
    }

    @Test
    void testUpdateProductWithNullName() {
        Product updateProduct = new Product();
        updateProduct.setProductId("test-id");
        updateProduct.setProductName(null);
        updateProduct.setProductQuantity(100);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.update(updateProduct);
        });

        assertEquals("Product name cannot be empty", exception.getMessage());
        verify(productRepository, never()).update(any());
    }

    @Test
    void testUpdateProductNotFound() {
        when(productRepository.update(product)).thenReturn(null);

        Product result = productService.update(product);

        assertNull(result);
        verify(productRepository).update(product);
    }

    @Test
    void testDeleteById() {
        doNothing().when(productRepository).deleteById(product.getProductId());

        productService.deleteById(product.getProductId());

        verify(productRepository).deleteById(product.getProductId());
    }
}
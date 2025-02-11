package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ProductRepositoryTest {

    @InjectMocks
    ProductRepository productRepository;
    @BeforeEach
    void setUp() {
    }
    @Test
    void testCreateAndFind() {
        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(100);
        productRepository.create(product);

        Iterator<Product> productIterator = productRepository.findAll();
        assertTrue(productIterator.hasNext());
        Product savedProduct = productIterator.next();
        assertEquals(savedProduct.getProductId(), product.getProductId());
        assertEquals(savedProduct.getProductName(), product.getProductName());
        assertEquals(savedProduct.getProductQuantity(), product.getProductQuantity());
    }

    @Test
    void testFindAllIfEmpty() {
        Iterator<Product> productIterator = productRepository.findAll();
        assertFalse(productIterator.hasNext());
    }

    @Test
    void testFindAllIfMoreThanOneProduct() {
        Product product1 = new Product();
        product1.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product1.setProductName("Sampo Cap Bambang");
        product1.setProductQuantity(100);
        productRepository.create(product1);

        Product product2 = new Product();
        product2.setProductId("a0f9de45-90b1-437d-a0bf-d0821dde9096");
        product2.setProductName("Sampo Cap Usep");
        product2.setProductQuantity(50);
        productRepository.create(product2);

        Iterator<Product> productIterator = productRepository.findAll();
        assertTrue(productIterator.hasNext());
        Product savedProduct = productIterator.next();
        assertEquals(product1.getProductId(), savedProduct.getProductId());
        savedProduct = productIterator.next();
        assertEquals(product2.getProductId(), savedProduct.getProductId());
        assertFalse(productIterator.hasNext());
    }

    @Test
    void testEditProduct_Success() {
        Product product = new Product();
        product.setProductId("test-id");
        product.setProductName("Original Name");
        product.setProductQuantity(100);
        productRepository.create(product);

        Product updatedProduct = new Product();
        updatedProduct.setProductId("test-id");
        updatedProduct.setProductName("Updated Name");
        updatedProduct.setProductQuantity(200);

        Product result = productRepository.update(updatedProduct);

        assertNotNull(result);
        assertEquals("Updated Name", result.getProductName());
        assertEquals(200, result.getProductQuantity());
    }

    @Test
    void testEditProduct_PartialUpdate() {
        Product product = new Product();
        product.setProductId("test-id");
        product.setProductName("Original Name");
        product.setProductQuantity(100);
        productRepository.create(product);

        Product partialUpdate = new Product();
        partialUpdate.setProductId("test-id");
        partialUpdate.setProductName("Updated Name");
        partialUpdate.setProductQuantity(100);
        Product result = productRepository.update(partialUpdate);

        assertNotNull(result);
        assertEquals("Updated Name", result.getProductName());
        assertEquals(100, result.getProductQuantity());
    }

    @Test
    void testEditProduct_NotFound() {
        Product nonExistentProduct = new Product();
        nonExistentProduct.setProductId("non-existent-id");
        nonExistentProduct.setProductName("Test");
        nonExistentProduct.setProductQuantity(1);

        Product result = productRepository.update(nonExistentProduct);

        assertNull(result);
    }

    @Test
    void testDeleteProduct_Success() {
        Product product = new Product();
        product.setProductId("test-id");
        product.setProductName("Test Product");
        product.setProductQuantity(100);
        productRepository.create(product);

        productRepository.deleteById(product.getProductId());

        assertNull(productRepository.findById(product.getProductId()));
    }

    @Test
    void testDeleteProduct_WithMultipleProducts() {
        Product product1 = new Product();
        product1.setProductId("id-1");
        product1.setProductName("Product 1");
        product1.setProductQuantity(100);
        productRepository.create(product1);

        Product product2 = new Product();
        product2.setProductId("id-2");
        product2.setProductName("Product 2");
        product2.setProductQuantity(200);
        productRepository.create(product2);

        productRepository.deleteById("id-1");

        Iterator<Product> productIterator = productRepository.findAll();
        assertTrue(productIterator.hasNext());
        Product remainingProduct = productIterator.next();
        assertEquals("id-2", remainingProduct.getProductId());
        assertFalse(productIterator.hasNext());
    }

    @Test
    void testFindById_Success() {
        Product product = new Product();
        product.setProductId("test-id");
        product.setProductName("Test Product");
        product.setProductQuantity(100);
        productRepository.create(product);

        Product result = productRepository.findById(product.getProductId());

        assertNotNull(result);
        assertEquals(product.getProductName(), result.getProductName());
        assertEquals(product.getProductQuantity(), result.getProductQuantity());
    }

    @Test
    void testFindById_NotFound() {
        Product result = productRepository.findById("non-existent-id");
        assertNull(result);
    }
}
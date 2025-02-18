package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class ProductRepositoryTest {

    @InjectMocks
    ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        // Initialize the tests
    }

    @Test
    void testCreateAndFind() {
        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(100);
        Product result = productRepository.create(product);

        assertNotNull(result);
        assertEquals(product, result);

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
    void testEditProductSuccess() {
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
    void testEditProductPartialUpdate() {
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
    void testEditProductNotFound() {
        Product nonExistentProduct = new Product();
        nonExistentProduct.setProductId("non-existent-id");
        nonExistentProduct.setProductName("Test");
        nonExistentProduct.setProductQuantity(1);

        Product result = productRepository.update(nonExistentProduct);

        assertNull(result);
    }

    @Test
    void testDeleteProductSuccess() {
        Product product = new Product();
        product.setProductId("test-id");
        product.setProductName("Test Product");
        product.setProductQuantity(100);
        productRepository.create(product);

        productRepository.deleteById(product.getProductId());

        assertNull(productRepository.findById(product.getProductId()));
    }

    @Test
    void testDeleteProductNonExistent() {
        productRepository.deleteById("non-existent-id");
        assertTrue(true);
    }

    @Test
    void testDeleteProductWithMultipleProducts() {
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
    void testFindByIdSuccess() {
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
    void testFindByIdNotFound() {
        Product result = productRepository.findById("non-existent-id");
        assertNull(result);
    }

    @Test
    void testFindByIdProductIdNotEquals() {
        Product product1 = new Product();
        product1.setProductId("product-1");
        product1.setProductName("Product One");
        product1.setProductQuantity(100);
        productRepository.create(product1);

        Product product2 = new Product();
        product2.setProductId("product-2");
        product2.setProductName("Product Two");
        product2.setProductQuantity(50);
        productRepository.create(product2);

        Product result = productRepository.findById("product-3");

        assertNull(result);
    }


    @Test
    void testFindByIdProductIdIsNull() {
        Product product = new Product();
        product.setProductId(null);
        product.setProductName("Test Product");
        product.setProductQuantity(100);
        productRepository.create(product);

        Product result = productRepository.findById("some-id");
        assertNull(result);
    }


    @Test
    void testCreateNullProductId() {
        Product product = new Product();
        product.setProductId(null);
        product.setProductName("Product with no ID");
        product.setProductQuantity(10);

        Product result = productRepository.create(product);
        assertNotNull(result);
        assertNull(result.getProductId());
        assertEquals("Product with no ID", result.getProductName());
        assertEquals(10, result.getProductQuantity());
    }

    @Test
    void testUpdateNullProductId() {
        Product product = new Product();
        product.setProductId("test-id");
        product.setProductName("Test Product");
        productRepository.create(product);

        Product updateProduct = new Product();
        updateProduct.setProductId(null);
        Product result = productRepository.update(updateProduct);
        assertNull(result);
    }

    @Test
    void testUpdateExistingProductWithNullId() {
        Product product = new Product();
        product.setProductId(null);
        productRepository.create(product);

        Product updateProduct = new Product();
        updateProduct.setProductId("test-id");
        Product result = productRepository.update(updateProduct);
        assertNull(result);
    }

    @Test
    void testFindByIdNullId() {
        Product result = productRepository.findById(null);
        assertNull(result);
    }

    @Test
    void testDeleteByIdNullId() {
        productRepository.deleteById(null);
        assertTrue(true);
    }

    @Test
    void testUpdateNullProduct() {
        Product result = productRepository.update(null);
        assertNull(result);
    }

    @Test
    void testUpdateProductIdMismatch() {
        Product existingProduct = new Product();
        existingProduct.setProductId("existing-id");
        existingProduct.setProductName("Existing Product");
        existingProduct.setProductQuantity(100);
        productRepository.create(existingProduct);

        Product updatedProduct = new Product();
        updatedProduct.setProductId("different-id");
        updatedProduct.setProductName("Updated Product");
        updatedProduct.setProductQuantity(150);

        Product result = productRepository.update(updatedProduct);
        assertNull(result);
    }

}
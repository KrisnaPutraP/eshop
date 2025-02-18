package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProductPage() {
        String viewName = productController.createProductPage(model);

        verify(model).addAttribute(eq("product"), any(Product.class));
        assertEquals("createProduct", viewName);
    }

    @Test
    void testCreateProductPostSuccess() {
        Product product = new Product();
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = productController.createProductPost(product, bindingResult, model);

        verify(productService).create(product);
        assertEquals("redirect:list", viewName);
    }

    @Test
    void testCreateProductPostValidationError() {
        Product product = new Product();
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = productController.createProductPost(product, bindingResult, model);

        verify(productService, never()).create(any(Product.class));
        assertEquals("createProduct", viewName);
    }

    @Test
    void testProductListPage() {
        List<Product> products = Arrays.asList(new Product(), new Product());
        when(productService.findAll()).thenReturn(products);

        String viewName = productController.productListPage(model);

        verify(model).addAttribute("products", products);
        assertEquals("productList", viewName);
    }

    @Test
    void testEditProductPageExistingProduct() {
        String productId = "testId";
        Product product = new Product();
        when(productService.findById(productId)).thenReturn(product);

        String viewName = productController.editProductPage(productId, model);

        verify(model).addAttribute("product", product);
        assertEquals("editProduct", viewName);
    }

    @Test
    void testEditProductPageNonExistingProduct() {
        String productId = "nonExistingId";
        when(productService.findById(productId)).thenReturn(null);

        String viewName = productController.editProductPage(productId, model);

        verify(model, never()).addAttribute(eq("product"), any());
        assertEquals("redirect:/product/list", viewName);
    }

    @Test
    void testEditProductPostSuccess() {
        Product product = new Product();
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = productController.editProductPost(product, bindingResult);

        verify(productService).update(product);
        assertEquals("redirect:/product/list", viewName);
    }

    @Test
    void testEditProductPostValidationError() {
        Product product = new Product();
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = productController.editProductPost(product, bindingResult);

        verify(productService, never()).update(any(Product.class));
        assertEquals("editProduct", viewName);
    }

    @Test
    void testDeleteProduct() {
        String productId = "testId";

        String viewName = productController.deleteProduct(productId);

        verify(productService).deleteById(productId);
        assertEquals("redirect:/product/list", viewName);
    }
}
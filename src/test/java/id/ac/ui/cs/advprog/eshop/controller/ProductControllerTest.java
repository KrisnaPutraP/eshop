package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productController = new ProductController(productService);
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

    // Additional tests for the abstract methods implemented in ProductController

    @Test
    void testGetCreatePath() {
        assertEquals("/product/create", productController.getCreatePath());
    }

    @Test
    void testGetListPath() {
        assertEquals("/product/list", productController.getListPath());
    }

    @Test
    void testGetEditPath() {
        assertEquals("/product/edit", productController.getEditPath());
    }

    @Test
    void testGetDeletePath() {
        assertEquals("/product/delete", productController.getDeletePath());
    }

    @Test
    void testGetCreateViewName() {
        assertEquals("createProduct", productController.getCreateViewName());
    }

    @Test
    void testGetListViewName() {
        assertEquals("productList", productController.getListViewName());
    }

    @Test
    void testGetEditViewName() {
        assertEquals("editProduct", productController.getEditViewName());
    }

    @Test
    void testGetCreateRedirectUrl() {
        assertEquals("redirect:list", productController.getCreateRedirectUrl());
    }

    @Test
    void testGetEditRedirectUrl() {
        assertEquals("redirect:/product/list", productController.getEditRedirectUrl());
    }

    @Test
    void testGetDeleteRedirectUrl() {
        assertEquals("redirect:/product/list", productController.getDeleteRedirectUrl());
    }

    @Test
    void testGetEntityAttributeName() {
        assertEquals("product", productController.getEntityAttributeName());
    }

    @Test
    void testGetEntitiesAttributeName() {
        assertEquals("products", productController.getEntitiesAttributeName());
    }

    @Test
    void testCreateNewEntity() {
        Product product = productController.createNewEntity();
        assertEquals(Product.class, product.getClass());
    }

    @Test
    void testCreateEntity() {
        Product product = new Product();
        product.setProductId("test");

        when(productService.create(product)).thenReturn(product);

        Product result = productController.createEntity(product);
        assertEquals(product, result);
        verify(productService).create(product);
    }

    @Test
    void testFindAllEntities() {
        List<Product> products = Arrays.asList(new Product(), new Product());
        when(productService.findAll()).thenReturn(products);

        List<Product> result = productController.findAllEntities();
        assertEquals(products, result);
        verify(productService).findAll();
    }

    @Test
    void testFindEntityById() {
        String productId = "testId";
        Product product = new Product();
        when(productService.findById(productId)).thenReturn(product);

        Product result = productController.findEntityById(productId);
        assertEquals(product, result);
        verify(productService).findById(productId);
    }

    @Test
    void testUpdateEntity() {
        Product product = new Product();

        productController.updateEntity(product);
        verify(productService).update(product);
    }

    @Test
    void testDeleteEntityById() {
        String productId = "testId";

        productController.deleteEntityById(productId);
        verify(productService).deleteById(productId);
    }

    @Test
    void testGetEntityId() {
        Product product = new Product();
        product.setProductId("testId");

        String result = productController.getEntityId(product);
        assertEquals("testId", result);
    }
}
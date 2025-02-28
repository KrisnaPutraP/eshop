package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController extends AbstractCrudController<Product, ProductService> {

    @Autowired
    public ProductController(ProductService productService) {
        super(productService);
    }

    @Override
    protected String getCreatePath() {
        return "/product/create";
    }

    @Override
    protected String getListPath() {
        return "/product/list";
    }

    @Override
    protected String getEditPath() {
        return "/product/edit";
    }

    @Override
    protected String getDeletePath() {
        return "/product/delete";
    }

    @Override
    protected String getCreateViewName() {
        return "createProduct";
    }

    @Override
    protected String getListViewName() {
        return "productList";
    }

    @Override
    protected String getEditViewName() {
        return "editProduct";
    }

    @Override
    protected String getCreateRedirectUrl() {
        return "redirect:list";
    }

    @Override
    protected String getEditRedirectUrl() {
        return "redirect:/product/list";
    }

    @Override
    protected String getDeleteRedirectUrl() {
        return "redirect:/product/list";
    }

    @Override
    protected String getEntityAttributeName() {
        return "product";
    }

    @Override
    protected String getEntitiesAttributeName() {
        return "products";
    }

    @Override
    protected Product createNewEntity() {
        return new Product();
    }

    @Override
    protected Product createEntity(Product entity) {
        return service.create(entity);
    }

    @Override
    protected List<Product> findAllEntities() {
        return service.findAll();
    }

    @Override
    protected Product findEntityById(String id) {
        return service.findById(id);
    }

    @Override
    protected void updateEntity(Product entity) {
        service.update(entity);
    }

    @Override
    protected void deleteEntityById(String id) {
        service.deleteById(id);
    }

    @Override
    protected String getEntityId(Product entity) {
        return entity.getProductId();
    }

    // Mapping methods with explicit URL paths to match original controller

    @GetMapping("/create")
    public String createProductPage(Model model) {
        return createPage(model);
    }

    @PostMapping("/create")
    public String createProductPost(@Valid @ModelAttribute Product product,
                                    BindingResult bindingResult,
                                    Model model) {
        return createPost(product, bindingResult, model);
    }

    @GetMapping("/list")
    public String productListPage(Model model) {
        return listPage(model);
    }

    @GetMapping("/edit/{id}")
    public String editProductPage(@PathVariable("id") String productId, Model model) {
        return editPage(productId, model);
    }

    @PostMapping("/edit")
    public String editProductPost(@Valid @ModelAttribute Product product,
                                  BindingResult bindingResult) {
        return editPost(product, bindingResult);
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") String productId) {
        return deleteEntity(productId);
    }
}
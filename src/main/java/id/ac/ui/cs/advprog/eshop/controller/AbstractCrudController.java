package id.ac.ui.cs.advprog.eshop.controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

/**
 * Abstract base controller that provides common CRUD operations
 * for entities in the e-shop application.
 *
 * @param <T> The entity type this controller manages
 * @param <S> The service type used to manage the entity
 */
public abstract class AbstractCrudController<T, S> {

    protected final S service;

    /**
     * Constructor-based dependency injection
     */
    protected AbstractCrudController(S service) {
        this.service = service;
    }

    /**
     * Get the full create path (e.g., "/product/create", "/car/createCar")
     */
    protected abstract String getCreatePath();

    /**
     * Get the full list path (e.g., "/product/list", "/car/listCar")
     */
    protected abstract String getListPath();

    /**
     * Get the full edit path (e.g., "/product/edit", "/car/editCar")
     */
    protected abstract String getEditPath();

    /**
     * Get the full delete path (e.g., "/product/delete", "/car/deleteCar")
     */
    protected abstract String getDeletePath();

    /**
     * Get the view name for the create entity page
     */
    protected abstract String getCreateViewName();

    /**
     * Get the view name for the list entities page
     */
    protected abstract String getListViewName();

    /**
     * Get the view name for the edit entity page
     */
    protected abstract String getEditViewName();

    /**
     * Get the redirect URL after create
     */
    protected abstract String getCreateRedirectUrl();

    /**
     * Get the redirect URL after edit
     */
    protected abstract String getEditRedirectUrl();

    /**
     * Get the redirect URL after delete
     */
    protected abstract String getDeleteRedirectUrl();

    /**
     * Get the model attribute name for a single entity
     */
    protected abstract String getEntityAttributeName();

    /**
     * Get the model attribute name for a list of entities
     */
    protected abstract String getEntitiesAttributeName();

    /**
     * Create a new entity instance
     */
    protected abstract T createNewEntity();

    /**
     * Create an entity using the service
     */
    protected abstract T createEntity(T entity);

    /**
     * Find all entities using the service
     */
    protected abstract List<T> findAllEntities();

    /**
     * Find entity by ID using the service
     */
    protected abstract T findEntityById(String id);

    /**
     * Update an entity using the service
     */
    protected abstract void updateEntity(T entity);

    /**
     * Delete an entity by ID using the service
     */
    protected abstract void deleteEntityById(String id);

    /**
     * Get ID from entity
     */
    protected abstract String getEntityId(T entity);

    /**
     * Display the create entity form
     */
    public String createPage(Model model) {
        T entity = createNewEntity();
        model.addAttribute(getEntityAttributeName(), entity);
        return getCreateViewName();
    }

    /**
     * Process the create entity form submission
     */
    public String createPost(@Valid @ModelAttribute T entity,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            return getCreateViewName();
        }
        createEntity(entity);
        return getCreateRedirectUrl();
    }

    /**
     * Display the list of entities
     */
    public String listPage(Model model) {
        List<T> allEntities = findAllEntities();
        model.addAttribute(getEntitiesAttributeName(), allEntities);
        return getListViewName();
    }

    /**
     * Display the edit entity form
     */
    public String editPage(@PathVariable String id, Model model) {
        T entity = findEntityById(id);
        if (entity == null) {
            return getEditRedirectUrl();
        }
        model.addAttribute(getEntityAttributeName(), entity);
        return getEditViewName();
    }

    /**
     * Process the edit entity form submission
     */
    public String editPost(@Valid @ModelAttribute T entity,
                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return getEditViewName();
        }
        updateEntity(entity);
        return getEditRedirectUrl();
    }

    /**
     * Delete an entity
     */
    public String deleteEntity(@RequestParam String id) {
        deleteEntityById(id);
        return getDeleteRedirectUrl();
    }
}
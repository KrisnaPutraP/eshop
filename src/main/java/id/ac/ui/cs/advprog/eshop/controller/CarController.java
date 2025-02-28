package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Car;
import id.ac.ui.cs.advprog.eshop.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@Controller
@RequestMapping("/car")
class CarController extends AbstractCrudController<Car, CarService> {

    @Autowired
    public CarController(CarService carService) {
        super(carService);
    }

    @Override
    protected String getCreatePath() {
        return "/car/createCar";
    }

    @Override
    protected String getListPath() {
        return "/car/listCar";
    }

    @Override
    protected String getEditPath() {
        return "/car/editCar";
    }

    @Override
    protected String getDeletePath() {
        return "/car/deleteCar";
    }

    @Override
    protected String getCreateViewName() {
        return "createCar";
    }

    @Override
    protected String getListViewName() {
        return "carList";
    }

    @Override
    protected String getEditViewName() {
        return "editCar";
    }

    @Override
    protected String getCreateRedirectUrl() {
        return "redirect:listCar";
    }

    @Override
    protected String getEditRedirectUrl() {
        return "redirect:listCar";
    }

    @Override
    protected String getDeleteRedirectUrl() {
        return "redirect:listCar";
    }

    @Override
    protected String getEntityAttributeName() {
        return "car";
    }

    @Override
    protected String getEntitiesAttributeName() {
        return "cars";
    }

    @Override
    protected Car createNewEntity() {
        return new Car();
    }

    @Override
    protected Car createEntity(Car entity) {
        return service.create(entity);
    }

    @Override
    protected List<Car> findAllEntities() {
        return service.findAll();
    }

    @Override
    protected Car findEntityById(String id) {
        return service.findById(id);
    }

    @Override
    protected void updateEntity(Car entity) {
        service.update(entity.getCarId(), entity);
    }

    @Override
    protected void deleteEntityById(String id) {
        service.deleteCarById(id);
    }

    @Override
    protected String getEntityId(Car entity) {
        return entity.getCarId();
    }

    // Mapping methods with explicit URL paths to match original controller

    @GetMapping("/createCar")
    public String createCarPage(Model model) {
        return createPage(model);
    }

    @PostMapping("/createCar")
    public String createCarPost(@Valid @ModelAttribute Car car,
                                BindingResult bindingResult,
                                Model model) {
        return createPost(car, bindingResult, model);
    }

    @GetMapping("/listCar")
    public String carListPage(Model model) {
        return listPage(model);
    }

    @GetMapping("/editCar/{carId}")
    public String editCarPage(@PathVariable String carId, Model model) {
        return editPage(carId, model);
    }

    @PostMapping("/editCar")
    public String editCarPost(@Valid @ModelAttribute Car car,
                              BindingResult bindingResult) {
        return editPost(car, bindingResult);
    }

    @PostMapping("/deleteCar")
    public String deleteCar(@RequestParam("carId") String carId) {
        return deleteEntity(carId);
    }
}
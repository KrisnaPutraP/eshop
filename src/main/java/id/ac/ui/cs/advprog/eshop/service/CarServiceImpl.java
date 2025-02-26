package id.ac.ui.cs.advprog.eshop.service;
import id.ac.ui.cs.advprog.eshop.model.Car;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
public class CarServiceImpl implements CarService {

    @Autowired
    private CarRepository carRepository;

    @Override
    public Car create (Car car) {
        if (car.getCarId() == null || car.getCarId().isEmpty()) {
            car.setCarId(UUID.randomUUID().toString());
        }
        validateCar(car);
        return carRepository.create(car);
    }

    @Override
    public List <Car> findAll() {
        Iterator <Car> carIterator = carRepository.findAll();
        List <Car> allCar = new ArrayList<>();
        carIterator.forEachRemaining(allCar::add);
        return allCar;
    }

    @Override
    public Car findById (String carId) {
        Car car = carRepository.findById(carId);
        return car;
    }

    @Override
    public void update (String carId, Car car) {
        carRepository.update(carId, car);
    }

    @Override
    public void deleteCarById (String carId) {
        carRepository.delete(carId);
    }

    private void validateCar(Car car) {
        if (car.getCarName() == null || car.getCarName().trim().isEmpty() || car.getCarColor() == null || car.getCarColor().trim().isEmpty()) {
            throw new IllegalArgumentException("Car name and color cannot be empty");
        }
        if (car.getCarQuantity() < 0) {
            throw new IllegalArgumentException("Car quantity cannot be negative");
        }
    }
}
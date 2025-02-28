package id.ac.ui.cs.advprog.eshop.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;


@Getter @Setter
public class Car {
    private String carId;

    @NotBlank(message = "Car name is required")
    private String carName;

    @NotBlank(message = "Car color is required")
    private String carColor;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int carQuantity;
}
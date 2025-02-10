package id.ac.ui.cs.advprog.eshop.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;

@Getter @Setter
public class Product {
    private String productId;

    @NotBlank(message = "Product name is required")
    private String productName;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int productQuantity;
}

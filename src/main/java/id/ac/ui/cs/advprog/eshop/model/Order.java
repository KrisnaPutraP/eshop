package id.ac.ui.cs.advprog.eshop.model;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;

@Builder
@Getter
@Setter
public class Order {
    String id;
    List<Product> products;
    Long orderTime;
    String author;
    String status;

    // Add a no-args constructor
    public Order() {
        this.products = new ArrayList<>();
        this.orderTime = System.currentTimeMillis();
        this.status = OrderStatus.WAITING_PAYMENT.getValue();
    }

    public Order(String id, List<Product> products, Long orderTime, String author) {
        this.id = id;
        this.orderTime = orderTime;
        this.author = author;
        this.status = OrderStatus.WAITING_PAYMENT.getValue();

        if (products == null || products.isEmpty()) {
            Product defaultProduct = new Product();
            defaultProduct.setProductId("default-" + id);
            defaultProduct.setProductName("Default Product");
            defaultProduct.setProductQuantity(1);
            this.products = List.of(defaultProduct);
        } else {
            this.products = products;
        }
    }

    public Order(String id, List<Product> products, long orderTime, String author, String status) {
        this(id, products, orderTime, author);
        this.setStatus(status);
    }

    public void setStatus(String status) {
        if (OrderStatus.contains(status)) {
            this.status = status;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
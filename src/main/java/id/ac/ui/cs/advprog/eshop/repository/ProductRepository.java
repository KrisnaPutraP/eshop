package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Repository
public class ProductRepository {
    private final List<Product> productData = new ArrayList<>();

    public Product create(Product product) {
        productData.add(product);
        return product;
    }

    public Iterator<Product> findAll() {
        return productData.iterator();
    }

    public Product findById(String productId) {
        if (productId == null) {
            return null;
        }

        for (Product product : productData) {
            if (product.getProductId() != null && product.getProductId().equals(productId)) {
                return product;
            }
        }
        return null;
    }

    public Product update(Product updatedProduct) {
        if (updatedProduct == null || updatedProduct.getProductId() == null) {
            return null;
        }

        for (int i = 0; i < productData.size(); i++) {
            Product currentProduct = productData.get(i);
            if (currentProduct.getProductId() != null &&
                    currentProduct.getProductId().equals(updatedProduct.getProductId())) {
                productData.set(i, updatedProduct);
                return updatedProduct;
            }
        }
        return null;
    }

    public void deleteById(String productId) {
        productData.removeIf(product -> product.getProductId().equals(productId));
    }
}
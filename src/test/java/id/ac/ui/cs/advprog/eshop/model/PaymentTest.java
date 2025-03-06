package id.ac.ui.cs.advprog.eshop.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentTest {
    private Map<String, String> paymentData;
    private Order order;

    @BeforeEach
    void setUp() {
        this.paymentData = new HashMap<>();
        paymentData.put("bankName", "BCA");
        paymentData.put("referenceCode", "REF123456");

        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(2);

        java.util.List<Product> products = new java.util.ArrayList<>();
        products.add(product);

        this.order = new Order("13652556-012a-4c07-b546-54eb1396d79b",
                products, 1708560000L, "Safira Sudrajat");
    }

    @Test
    void testCreatePayment() {
        Payment payment = new Payment("payment-123", "BANK_TRANSFER", "WAITING", this.paymentData, this.order);

        assertEquals("payment-123", payment.getId());
        assertEquals("BANK_TRANSFER", payment.getMethod());
        assertEquals("WAITING", payment.getStatus());
        assertEquals(this.paymentData, payment.getPaymentData());
        assertEquals(this.order, payment.getOrder());
    }

    @Test
    void testSetStatus() {
        Payment payment = new Payment("payment-123", "BANK_TRANSFER", "WAITING", this.paymentData, this.order);
        payment.setStatus("SUCCESS");

        assertEquals("SUCCESS", payment.getStatus());
    }

    @Test
    void testPaymentBuilder() {
        Payment payment = Payment.builder()
                .id("payment-456")
                .method("VOUCHER")
                .status("WAITING")
                .paymentData(this.paymentData)
                .order(this.order)
                .build();

        assertEquals("payment-456", payment.getId());
        assertEquals("VOUCHER", payment.getMethod());
        assertEquals("WAITING", payment.getStatus());
        assertEquals(this.paymentData, payment.getPaymentData());
        assertEquals(this.order, payment.getOrder());
    }
}
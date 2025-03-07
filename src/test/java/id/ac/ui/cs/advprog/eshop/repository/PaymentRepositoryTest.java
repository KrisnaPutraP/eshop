package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.enums.PaymentMethod;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentRepositoryTest {
    PaymentRepository paymentRepository;
    Payment payment1;
    Payment payment2;

    @BeforeEach
    void setUp() {
        paymentRepository = new PaymentRepository();

        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(2);

        java.util.List<Product> products = new java.util.ArrayList<>();
        products.add(product);

        Order order1 = new Order("order-1", products, 1708560000L, "Customer1");
        Order order2 = new Order("order-2", products, 1708560001L, "Customer2");

        Map<String, String> paymentData1 = new HashMap<>();
        paymentData1.put("bankName", "BCA");
        paymentData1.put("referenceCode", "REF123456");

        Map<String, String> paymentData2 = new HashMap<>();
        paymentData2.put("voucherCode", "ESHOP12345678ABCD");

        payment1 = Payment.builder()
                .id("payment-1")
                .method(PaymentMethod.BANK_TRANSFER.getValue())
                .status(PaymentStatus.WAITING.getValue())
                .paymentData(paymentData1)
                .order(order1)
                .build();

        payment2 = Payment.builder()
                .id("payment-2")
                .method(PaymentMethod.VOUCHER.getValue())
                .status(PaymentStatus.SUCCESS.getValue())
                .paymentData(paymentData2)
                .order(order2)
                .build();
    }

    @Test
    void testSaveAndFindById() {
        Payment savedPayment = paymentRepository.save(payment1);
        assertEquals(payment1.getId(), savedPayment.getId());

        Payment retrievedPayment = paymentRepository.findById(payment1.getId());
        assertNotNull(retrievedPayment);
        assertEquals(payment1.getId(), retrievedPayment.getId());
        assertEquals(payment1.getMethod(), retrievedPayment.getMethod());
    }

    @Test
    void testFindAllPayments() {
        paymentRepository.save(payment1);
        paymentRepository.save(payment2);

        List<Payment> payments = paymentRepository.findAll();
        assertEquals(2, payments.size());
        assertTrue(payments.contains(payment1));
        assertTrue(payments.contains(payment2));
    }

    @Test
    void testFindNonExistentPayment() {
        Payment payment = paymentRepository.findById("non-existent-id");
        assertNull(payment);
    }
}
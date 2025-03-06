package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PaymentServiceImplTest {
    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Order order;
    private Map<String, String> validBankTransferData;
    private Map<String, String> invalidBankTransferData;
    private Map<String, String> validVoucherData;
    private Map<String, String> invalidVoucherData;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setProductId("product-1");
        product.setProductName("Test Product");
        product.setProductQuantity(1);
        products.add(product);

        order = new Order("order-1", products, System.currentTimeMillis(), "Customer");

        validBankTransferData = new HashMap<>();
        validBankTransferData.put("bankName", "BCA");
        validBankTransferData.put("referenceCode", "REF123456");

        invalidBankTransferData = new HashMap<>();
        invalidBankTransferData.put("bankName", "BCA");
        invalidBankTransferData.put("referenceCode", "");

        // Setup valid voucher data
        validVoucherData = new HashMap<>();
        validVoucherData.put("voucherCode", "ESHOP12345678ABC");

        // Setup invalid voucher data
        invalidVoucherData = new HashMap<>();
        invalidVoucherData.put("voucherCode", "INVALID");

        // Mock repository save to return the same payment
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock orderService.updateStatus to return the updated order
        when(orderService.updateStatus(anyString(), anyString())).thenAnswer(invocation -> {
            String orderId = invocation.getArgument(0);
            String status = invocation.getArgument(1);
            // Create a new order with the updated status
            Order updatedOrder = new Order(
                    order.getId(),
                    order.getProducts(),
                    order.getOrderTime(),
                    order.getAuthor(),
                    status
            );
            return updatedOrder;
        });

        // Mock findById to return a payment
        when(paymentRepository.findById(anyString())).thenAnswer(invocation -> {
            String id = invocation.getArgument(0);
            if ("payment-1".equals(id)) {
                return Payment.builder()
                        .id(id)
                        .method("BANK_TRANSFER")
                        .status("WAITING")
                        .paymentData(validBankTransferData)
                        .order(order)
                        .build();
            }
            return null;
        });

        // Mock findAll to return a list of payments
        when(paymentRepository.findAll()).thenReturn(List.of(
                Payment.builder()
                        .id("payment-1")
                        .method("BANK_TRANSFER")
                        .status("WAITING")
                        .paymentData(validBankTransferData)
                        .order(order)
                        .build(),
                Payment.builder()
                        .id("payment-2")
                        .method("VOUCHER")
                        .status("SUCCESS")
                        .paymentData(validVoucherData)
                        .order(order)
                        .build()
        ));
    }

    @Test
    void testAddPaymentWithBankTransfer() {
        Payment payment = paymentService.addPayment(order, "BANK_TRANSFER", validBankTransferData);

        assertNotNull(payment);
        assertEquals("BANK_TRANSFER", payment.getMethod());
        assertEquals("SUCCESS", payment.getStatus());
        assertEquals(validBankTransferData, payment.getPaymentData());
        assertEquals(order, payment.getOrder());

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(orderService, times(1)).updateStatus(order.getId(), "SUCCESS");
    }

    @Test
    void testAddPaymentWithInvalidBankTransfer() {
        Payment payment = paymentService.addPayment(order, "BANK_TRANSFER", invalidBankTransferData);

        assertNotNull(payment);
        assertEquals("BANK_TRANSFER", payment.getMethod());
        assertEquals("REJECTED", payment.getStatus());
        assertEquals(invalidBankTransferData, payment.getPaymentData());
        assertEquals(order, payment.getOrder());

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(orderService, times(1)).updateStatus(order.getId(), "FAILED");
    }

    @Test
    void testAddPaymentWithValidVoucher() {
        Payment payment = paymentService.addPayment(order, "VOUCHER", validVoucherData);

        assertNotNull(payment);
        assertEquals("VOUCHER", payment.getMethod());
        assertEquals("SUCCESS", payment.getStatus());
        assertEquals(validVoucherData, payment.getPaymentData());
        assertEquals(order, payment.getOrder());

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(orderService, times(1)).updateStatus(order.getId(), "SUCCESS");
    }

    @Test
    void testAddPaymentWithInvalidVoucher() {
        Payment payment = paymentService.addPayment(order, "VOUCHER", invalidVoucherData);

        assertNotNull(payment);
        assertEquals("VOUCHER", payment.getMethod());
        assertEquals("REJECTED", payment.getStatus());
        assertEquals(invalidVoucherData, payment.getPaymentData());
        assertEquals(order, payment.getOrder());

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(orderService, times(1)).updateStatus(order.getId(), "FAILED");
    }

    @Test
    void testSetStatusToSuccess() {
        Payment payment = Payment.builder()
                .id("payment-test")
                .method("BANK_TRANSFER")
                .status("WAITING")
                .paymentData(validBankTransferData)
                .order(order)
                .build();

        Payment updatedPayment = paymentService.setStatus(payment, "SUCCESS");

        assertEquals("SUCCESS", updatedPayment.getStatus());

        verify(paymentRepository, times(1)).save(payment);
        verify(orderService, times(1)).updateStatus(order.getId(), "SUCCESS");
    }

    @Test
    void testSetStatusToRejected() {
        Payment payment = Payment.builder()
                .id("payment-test")
                .method("BANK_TRANSFER")
                .status("WAITING")
                .paymentData(validBankTransferData)
                .order(order)
                .build();

        Payment updatedPayment = paymentService.setStatus(payment, "REJECTED");

        assertEquals("REJECTED", updatedPayment.getStatus());

        verify(paymentRepository, times(1)).save(payment);
        verify(orderService, times(1)).updateStatus(order.getId(), "FAILED");
    }

    @Test
    void testGetPaymentById() {
        Payment payment = paymentService.getPayment("payment-1");

        assertNotNull(payment);
        assertEquals("payment-1", payment.getId());
        assertEquals("BANK_TRANSFER", payment.getMethod());

        verify(paymentRepository, times(1)).findById("payment-1");
    }

    @Test
    void testGetAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();

        assertNotNull(payments);
        assertEquals(2, payments.size());

        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    void testValidateVoucherCode() {
        assertTrue(paymentService.isValidVoucherCode("ESHOP12345678BWG"));
        assertTrue(paymentService.isValidVoucherCode("ESHOP1234BZF5678"));

        // Invalid: Not 16 chars
        assertFalse(paymentService.isValidVoucherCode("ESHOP123456"));

        // Invalid: Doesn't start with "ESHOP"
        assertFalse(paymentService.isValidVoucherCode("SHOP1234567890ABCD"));

        // Invalid: Doesn't contain 8 numeric chars
        assertFalse(paymentService.isValidVoucherCode("ESHOPABCDEFGHIJKL"));
        assertFalse(paymentService.isValidVoucherCode("ESHOP1234ABCDEFG"));
    }

    @Test
    void testValidateBankTransferData() {
        // Valid: Both fields are non-empty
        assertTrue(paymentService.isValidBankTransferData(validBankTransferData));

        // Invalid: Missing reference code
        assertFalse(paymentService.isValidBankTransferData(invalidBankTransferData));

        // Invalid: Missing bank name
        Map<String, String> missingBankName = new HashMap<>();
        missingBankName.put("bankName", "");
        missingBankName.put("referenceCode", "REF123456");
        assertFalse(paymentService.isValidBankTransferData(missingBankName));

        // Invalid: Null data
        assertFalse(paymentService.isValidBankTransferData(null));

        // Invalid: Missing fields
        Map<String, String> missingFields = new HashMap<>();
        assertFalse(paymentService.isValidBankTransferData(missingFields));
    }
}
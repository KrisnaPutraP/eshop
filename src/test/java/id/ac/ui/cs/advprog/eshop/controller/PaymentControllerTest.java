package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    private PaymentController paymentController;

    private Payment samplePayment;
    private List<Payment> paymentList;
    private Order sampleOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Manually create the controller with mocks
        paymentController = new PaymentController(paymentService);

        // Create sample product
        Product product = new Product();
        product.setProductId("prod-1");
        product.setProductName("Product 1");
        product.setProductQuantity(10);

        List<Product> products = new ArrayList<>();
        products.add(product);

        // Create sample order
        sampleOrder = new Order("order-1", products, System.currentTimeMillis(), "John Doe");

        // Create sample payment
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "ESHOP12345678");
        samplePayment = new Payment("payment-1", "VOUCHER", "SUCCESS", paymentData, sampleOrder);

        // Create payment list
        paymentList = new ArrayList<>();
        paymentList.add(samplePayment);
    }

    @Test
    void testDetailPage() {
        String viewName = paymentController.detailPage(model);
        assertEquals("payment/detail-form", viewName);
        verify(model, times(1)).addAttribute(eq("paymentId"), eq(""));
    }

    @Test
    void testShowPaymentDetail() {
        when(paymentService.getPayment("payment-1")).thenReturn(samplePayment);

        String viewName = paymentController.showPaymentDetail("payment-1", model);
        assertEquals("payment/detail", viewName);
        verify(paymentService, times(1)).getPayment("payment-1");
        verify(model, times(1)).addAttribute("payment", samplePayment);
    }

    @Test
    void testShowPaymentDetailNotFound() {
        when(paymentService.getPayment("non-existent")).thenReturn(null);

        String viewName = paymentController.showPaymentDetail("non-existent", model);
        assertEquals("redirect:/payment/detail", viewName);
        verify(paymentService, times(1)).getPayment("non-existent");
    }

    @Test
    void testListAdminPage() {
        when(paymentService.getAllPayments()).thenReturn(paymentList);

        String viewName = paymentController.listAdminPage(model);
        assertEquals("payment/admin/list", viewName);
        verify(paymentService, times(1)).getAllPayments();
        verify(model, times(1)).addAttribute("payments", paymentList);
    }

    @Test
    void testShowAdminPaymentDetail() {
        when(paymentService.getPayment("payment-1")).thenReturn(samplePayment);

        String viewName = paymentController.showAdminPaymentDetail("payment-1", model);
        assertEquals("payment/admin/detail", viewName);
        verify(paymentService, times(1)).getPayment("payment-1");
        verify(model, times(1)).addAttribute("payment", samplePayment);
        verify(model, times(1)).addAttribute(eq("statuses"), any(String[].class));
    }

    @Test
    void testShowAdminPaymentDetailNotFound() {
        when(paymentService.getPayment("non-existent")).thenReturn(null);

        String viewName = paymentController.showAdminPaymentDetail("non-existent", model);
        assertEquals("redirect:/payment/admin/list", viewName);
        verify(paymentService, times(1)).getPayment("non-existent");
    }

    @Test
    void testSetPaymentStatus() {
        when(paymentService.getPayment("payment-1")).thenReturn(samplePayment);
        when(paymentService.setStatus(samplePayment, "REJECTED")).thenReturn(samplePayment);

        String viewName = paymentController.setPaymentStatus("payment-1", "REJECTED", model);
        assertEquals("redirect:/payment/admin/detail/payment-1", viewName);
        verify(paymentService, times(1)).getPayment("payment-1");
        verify(paymentService, times(1)).setStatus(samplePayment, "REJECTED");
    }

    @Test
    void testSetPaymentStatusNotFound() {
        when(paymentService.getPayment("non-existent")).thenReturn(null);

        String viewName = paymentController.setPaymentStatus("non-existent", "REJECTED", model);
        assertEquals("redirect:/payment/admin/list", viewName);
        verify(paymentService, times(1)).getPayment("non-existent");
        verify(paymentService, never()).setStatus(any(Payment.class), anyString());
    }

    @Test
    void testAbstractCrudControllerMethods() {
        assertEquals("/payment/create", paymentController.getCreatePath());
        assertEquals("/payment/admin/list", paymentController.getListPath());
        assertEquals("/payment/admin/detail", paymentController.getEditPath());
        assertEquals("/payment/delete", paymentController.getDeletePath());
        assertEquals("payment/create", paymentController.getCreateViewName());
        assertEquals("payment/admin/list", paymentController.getListViewName());
        assertEquals("payment/admin/detail", paymentController.getEditViewName());
        assertEquals("redirect:/payment/admin/list", paymentController.getCreateRedirectUrl());
        assertEquals("redirect:/payment/admin/list", paymentController.getEditRedirectUrl());
        assertEquals("redirect:/payment/admin/list", paymentController.getDeleteRedirectUrl());
        assertEquals("payment", paymentController.getEntityAttributeName());
        assertEquals("payments", paymentController.getEntitiesAttributeName());
        assertEquals(null, paymentController.createNewEntity());
    }

    @Test
    void testFindAllEntitiesMethod() {
        when(paymentService.getAllPayments()).thenReturn(paymentList);
        List<Payment> result = paymentController.findAllEntities();
        assertEquals(paymentList, result);
        verify(paymentService, times(1)).getAllPayments();
    }

    @Test
    void testFindEntityByIdMethod() {
        when(paymentService.getPayment("payment-1")).thenReturn(samplePayment);
        Payment result = paymentController.findEntityById("payment-1");
        assertEquals(samplePayment, result);
        verify(paymentService, times(1)).getPayment("payment-1");
    }

    @Test
    void testUpdateEntityMethod() {
        when(paymentService.setStatus(samplePayment, samplePayment.getStatus())).thenReturn(samplePayment);
        paymentController.updateEntity(samplePayment);
        verify(paymentService, times(1)).setStatus(samplePayment, samplePayment.getStatus());
    }

    @Test
    void testGetEntityIdMethod() {
        String id = paymentController.getEntityId(samplePayment);
        assertEquals("payment-1", id);
    }
}
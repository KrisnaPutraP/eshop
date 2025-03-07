package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
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

public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    private OrderController orderController;

    private Order sampleOrder;
    private List<Order> orderList;
    private Payment samplePayment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Manually create and inject the controller with mocks
        orderController = new OrderController(orderService);
        orderController.paymentService = paymentService;

        // Create sample product
        Product product = new Product();
        product.setProductId("prod-1");
        product.setProductName("Product 1");
        product.setProductQuantity(10);

        List<Product> products = new ArrayList<>();
        products.add(product);

        // Create sample order
        sampleOrder = new Order("order-1", products, System.currentTimeMillis(), "John Doe");

        // Create order list
        orderList = new ArrayList<>();
        orderList.add(sampleOrder);

        // Create sample payment
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "ESHOP12345678");
        samplePayment = new Payment("payment-1", "VOUCHER", "SUCCESS", paymentData, sampleOrder);
    }

    @Test
    void testCreateOrderPage() {
        String viewName = orderController.createOrderPage(model);
        assertEquals("order/create", viewName);
        verify(model, times(1)).addAttribute(eq("order"), any(Order.class));
    }

    @Test
    void testCreateOrderPost() {
        when(orderService.createOrder(any(Order.class))).thenReturn(sampleOrder);
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = orderController.createPost(sampleOrder, bindingResult, model);
        assertEquals("redirect:/order/history", viewName);
        verify(orderService, times(1)).createOrder(sampleOrder);
    }

    @Test
    void testHistoryPage() {
        String viewName = orderController.historyPage(model);
        assertEquals("order/history", viewName);
        verify(model, times(1)).addAttribute(eq("authorName"), eq(""));
    }

    @Test
    void testShowOrderHistory() {
        when(orderService.findAllByAuthor("John Doe")).thenReturn(orderList);

        String viewName = orderController.showOrderHistory("John Doe", model);
        assertEquals("order/history-result", viewName);
        verify(orderService, times(1)).findAllByAuthor("John Doe");
        verify(model, times(1)).addAttribute("orders", orderList);
        verify(model, times(1)).addAttribute("authorName", "John Doe");
    }

    @Test
    void testShowPaymentPage() {
        when(orderService.findById("order-1")).thenReturn(sampleOrder);

        String viewName = orderController.showPaymentPage("order-1", model);
        assertEquals("order/payment", viewName);
        verify(orderService, times(1)).findById("order-1");
        verify(model, times(1)).addAttribute("order", sampleOrder);
        verify(model, times(1)).addAttribute(eq("paymentMethods"), any(String[].class));
    }

    @Test
    void testShowPaymentPageOrderNotFound() {
        when(orderService.findById("non-existent")).thenReturn(null);

        String viewName = orderController.showPaymentPage("non-existent", model);
        assertEquals("redirect:/order/history", viewName);
        verify(orderService, times(1)).findById("non-existent");
    }

    @Test
    void testProcessPaymentVoucher() {
        when(orderService.findById("order-1")).thenReturn(sampleOrder);
        when(paymentService.addPayment(eq(sampleOrder), eq("VOUCHER"), any(Map.class))).thenReturn(samplePayment);

        String viewName = orderController.processPayment(
                "order-1",
                "VOUCHER",
                "ESHOP12345678",
                null,
                null,
                model
        );

        assertEquals("order/payment-confirmation", viewName);
        verify(orderService, times(1)).findById("order-1");
        verify(paymentService, times(1)).addPayment(eq(sampleOrder), eq("VOUCHER"), any(Map.class));
        verify(model, times(1)).addAttribute("payment", samplePayment);
    }

    @Test
    void testProcessPaymentBankTransfer() {
        when(orderService.findById("order-1")).thenReturn(sampleOrder);
        when(paymentService.addPayment(eq(sampleOrder), eq("BANK_TRANSFER"), any(Map.class))).thenReturn(samplePayment);

        String viewName = orderController.processPayment(
                "order-1",
                "BANK_TRANSFER",
                null,
                "Bank XYZ",
                "REF123456",
                model
        );

        assertEquals("order/payment-confirmation", viewName);
        verify(orderService, times(1)).findById("order-1");
        verify(paymentService, times(1)).addPayment(eq(sampleOrder), eq("BANK_TRANSFER"), any(Map.class));
        verify(model, times(1)).addAttribute("payment", samplePayment);
    }

    @Test
    void testProcessPaymentOrderNotFound() {
        when(orderService.findById("non-existent")).thenReturn(null);

        String viewName = orderController.processPayment(
                "non-existent",
                "VOUCHER",
                "ESHOP12345678",
                null,
                null,
                model
        );

        assertEquals("redirect:/order/history", viewName);
        verify(orderService, times(1)).findById("non-existent");
        verify(paymentService, never()).addPayment(any(Order.class), anyString(), any(Map.class));
    }

    @Test
    void testAbstractCrudControllerMethods() {
        assertEquals("/order/create", orderController.getCreatePath());
        assertEquals("/order/history", orderController.getListPath());
        assertEquals("/order/edit", orderController.getEditPath());
        assertEquals("/order/delete", orderController.getDeletePath());
        assertEquals("order/create", orderController.getCreateViewName());
        assertEquals("order/list", orderController.getListViewName());
        assertEquals("order/edit", orderController.getEditViewName());
        assertEquals("redirect:/order/history", orderController.getCreateRedirectUrl());
        assertEquals("redirect:/order/history", orderController.getEditRedirectUrl());
        assertEquals("redirect:/order/history", orderController.getDeleteRedirectUrl());
        assertEquals("order", orderController.getEntityAttributeName());
        assertEquals("orders", orderController.getEntitiesAttributeName());
    }

    @Test
    void testCreateEntityMethod() {
        when(orderService.createOrder(sampleOrder)).thenReturn(sampleOrder);
        Order result = orderController.createEntity(sampleOrder);
        assertEquals(sampleOrder, result);
        verify(orderService, times(1)).createOrder(sampleOrder);
    }

    @Test
    void testUpdateEntityMethod() {
        orderController.updateEntity(sampleOrder);
        verify(orderService, times(1)).updateStatus(sampleOrder.getId(), sampleOrder.getStatus());
    }

    @Test
    void testGetEntityIdMethod() {
        String id = orderController.getEntityId(sampleOrder);
        assertEquals("order-1", id);
    }
}
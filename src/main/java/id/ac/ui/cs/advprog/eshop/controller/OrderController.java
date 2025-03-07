package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/order")
public class OrderController extends AbstractCrudController<Order, OrderService> {

    // Make it package-private so tests can access it directly
    @Autowired
    PaymentService paymentService;

    @Autowired
    public OrderController(OrderService orderService) {
        super(orderService);
    }

    @GetMapping("/create")
    public String createOrderPage(Model model) {
        return createPage(model);
    }



    @GetMapping("/history")
    public String historyPage(Model model) {
        model.addAttribute("authorName", "");
        return "order/history";
    }

    @PostMapping("/history")
    public String showOrderHistory(@RequestParam String authorName, Model model) {
        List<Order> orders = service.findAllByAuthor(authorName);
        model.addAttribute("orders", orders);
        model.addAttribute("authorName", authorName);
        return "order/history-result";
    }

    // Additional endpoints for payment
    @GetMapping("/pay/{orderId}")
    public String showPaymentPage(@PathVariable String orderId, Model model) {
        Order order = service.findById(orderId);
        if (order == null) {
            return "redirect:/order/history";
        }
        model.addAttribute("order", order);
        model.addAttribute("paymentMethods", new String[]{"VOUCHER", "BANK_TRANSFER"});
        return "order/payment";
    }

    @PostMapping("/pay/{orderId}")
    public String processPayment(@PathVariable String orderId,
                                 @RequestParam String paymentMethod,
                                 @RequestParam(required = false) String voucherCode,
                                 @RequestParam(required = false) String bankName,
                                 @RequestParam(required = false) String referenceCode,
                                 Model model) {
        Order order = service.findById(orderId);
        if (order == null) {
            return "redirect:/order/history";
        }

        Map<String, String> paymentData = new HashMap<>();
        if ("VOUCHER".equals(paymentMethod)) {
            paymentData.put("voucherCode", voucherCode);
        } else if ("BANK_TRANSFER".equals(paymentMethod)) {
            paymentData.put("bankName", bankName);
            paymentData.put("referenceCode", referenceCode);
        }

        try {
            Payment payment = paymentService.addPayment(order, paymentMethod, paymentData);
            model.addAttribute("payment", payment);
            return "order/payment-confirmation";
        } catch (Exception e) {
            // Log error
            System.err.println("Error processing payment: " + e.getMessage());
            // Add error message to model
            model.addAttribute("errorMessage", "Error processing payment: " + e.getMessage());
            model.addAttribute("order", order);
            model.addAttribute("paymentMethods", new String[]{"VOUCHER", "BANK_TRANSFER"});
            return "order/payment";
        }
    }

    // AbstractCrudController implementations
    @Override
    protected String getCreatePath() {
        return "/order/create";
    }

    @Override
    protected String getListPath() {
        return "/order/history";
    }

    @Override
    protected String getEditPath() {
        return "/order/edit";
    }

    @Override
    protected String getDeletePath() {
        return "/order/delete";
    }

    @Override
    protected String getCreateViewName() {
        return "order/create";
    }

    @Override
    protected String getListViewName() {
        return "order/list";
    }

    @Override
    protected String getEditViewName() {
        return "order/edit";
    }

    @Override
    protected String getCreateRedirectUrl() {
        return "redirect:/order/history";
    }

    @Override
    protected String getEditRedirectUrl() {
        return "redirect:/order/history";
    }

    @Override
    protected String getDeleteRedirectUrl() {
        return "redirect:/order/history";
    }

    @Override
    protected String getEntityAttributeName() {
        return "order";
    }

    @Override
    protected String getEntitiesAttributeName() {
        return "orders";
    }

    @Override
    protected Order createNewEntity() {
        // Create a simple new Order for the form
        List<Product> products = new ArrayList<>();
        Product dummyProduct = new Product();
        dummyProduct.setProductId("dummy-1");
        dummyProduct.setProductName("Dummy Product");
        dummyProduct.setProductQuantity(1);
        products.add(dummyProduct);

        return new Order("", products, System.currentTimeMillis(), "");
    }

    @Override
    protected Order createEntity(Order entity) {
        return service.createOrder(entity);
    }

    @Override
    protected List<Order> findAllEntities() {
        return null; // Not used directly, we use findAllByAuthor instead
    }

    @Override
    protected Order findEntityById(String id) {
        return service.findById(id);
    }

    @Override
    protected void updateEntity(Order entity) {
        service.updateStatus(entity.getId(), entity.getStatus());
    }

    @Override
    protected void deleteEntityById(String id) {
        // Not implemented for orders
    }

    @Override
    protected String getEntityId(Order entity) {
        return entity.getId();
    }
}
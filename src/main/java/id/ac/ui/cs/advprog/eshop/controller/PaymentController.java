package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/payment")
public class PaymentController extends AbstractCrudController<Payment, PaymentService> {

    @Autowired
    public PaymentController(PaymentService paymentService) {
        super(paymentService);
    }

    @GetMapping("/detail")
    public String detailPage(Model model) {
        model.addAttribute("paymentId", "");
        return "payment/detail-form";
    }

    @GetMapping("/detail/{paymentId}")
    public String showPaymentDetail(@PathVariable String paymentId, Model model) {
        Payment payment = service.getPayment(paymentId);
        if (payment == null) {
            return "redirect:/payment/detail";
        }
        model.addAttribute("payment", payment);
        return "payment/detail";
    }

    @GetMapping("/admin/list")
    public String listAdminPage(Model model) {
        List<Payment> payments = service.getAllPayments();
        model.addAttribute("payments", payments);
        return "payment/admin/list";
    }

    @GetMapping("/admin/detail/{paymentId}")
    public String showAdminPaymentDetail(@PathVariable String paymentId, Model model) {
        Payment payment = service.getPayment(paymentId);
        if (payment == null) {
            return "redirect:/payment/admin/list";
        }
        model.addAttribute("payment", payment);
        model.addAttribute("statuses", new String[]{"WAITING", "SUCCESS", "REJECTED"});
        return "payment/admin/detail";
    }

    @PostMapping("/admin/set-status/{paymentId}")
    public String setPaymentStatus(@PathVariable String paymentId,
                                   @RequestParam String status,
                                   Model model) {
        Payment payment = service.getPayment(paymentId);
        if (payment == null) {
            return "redirect:/payment/admin/list";
        }

        payment = service.setStatus(payment, status);
        model.addAttribute("payment", payment);
        return "redirect:/payment/admin/detail/" + paymentId;
    }

    // AbstractCrudController implementations
    @Override
    protected String getCreatePath() {
        return "/payment/create";
    }

    @Override
    protected String getListPath() {
        return "/payment/admin/list";
    }

    @Override
    protected String getEditPath() {
        return "/payment/admin/detail";
    }

    @Override
    protected String getDeletePath() {
        return "/payment/delete";
    }

    @Override
    protected String getCreateViewName() {
        return "payment/create";
    }

    @Override
    protected String getListViewName() {
        return "payment/admin/list";
    }

    @Override
    protected String getEditViewName() {
        return "payment/admin/detail";
    }

    @Override
    protected String getCreateRedirectUrl() {
        return "redirect:/payment/admin/list";
    }

    @Override
    protected String getEditRedirectUrl() {
        return "redirect:/payment/admin/list";
    }

    @Override
    protected String getDeleteRedirectUrl() {
        return "redirect:/payment/admin/list";
    }

    @Override
    protected String getEntityAttributeName() {
        return "payment";
    }

    @Override
    protected String getEntitiesAttributeName() {
        return "payments";
    }

    @Override
    protected Payment createNewEntity() {
        return null; // Not used directly as payments are created from orders
    }

    @Override
    protected Payment createEntity(Payment entity) {
        // Not implemented directly, we use addPayment in service
        return entity;
    }

    @Override
    protected List<Payment> findAllEntities() {
        return service.getAllPayments();
    }

    @Override
    protected Payment findEntityById(String id) {
        return service.getPayment(id);
    }

    @Override
    protected void updateEntity(Payment entity) {
        service.setStatus(entity, entity.getStatus());
    }

    @Override
    protected void deleteEntityById(String id) {
        // Not implemented for payments
    }

    @Override
    protected String getEntityId(Payment entity) {
        return entity.getId();
    }
}
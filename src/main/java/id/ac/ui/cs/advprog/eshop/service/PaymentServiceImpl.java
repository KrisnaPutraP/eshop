package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderService orderService;

    @Override
    public Payment addPayment(Order order, String method, Map<String, String> paymentData) {
        String paymentId = UUID.randomUUID().toString();
        String status = determineInitialStatus(method, paymentData);

        Payment payment = Payment.builder()
                .id(paymentId)
                .method(method)
                .status(status)
                .paymentData(paymentData)
                .order(order)
                .build();

        if ("SUCCESS".equals(status)) {
            orderService.updateStatus(order.getId(), "SUCCESS");
        } else if ("REJECTED".equals(status)) {
            orderService.updateStatus(order.getId(), "FAILED");
        }

        return paymentRepository.save(payment);
    }

    @Override
    public Payment setStatus(Payment payment, String status) {
        payment.setStatus(status);

        if ("SUCCESS".equals(status)) {
            orderService.updateStatus(payment.getOrder().getId(), "SUCCESS");
        } else if ("REJECTED".equals(status)) {
            orderService.updateStatus(payment.getOrder().getId(), "FAILED");
        }

        return paymentRepository.save(payment);
    }

    @Override
    public Payment getPayment(String paymentId) {
        return paymentRepository.findById(paymentId);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    private String determineInitialStatus(String method, Map<String, String> paymentData) {
        if ("VOUCHER".equals(method)) {
            return isValidVoucherCode(paymentData.get("voucherCode")) ? "SUCCESS" : "REJECTED";
        } else if ("BANK_TRANSFER".equals(method)) {
            return isValidBankTransferData(paymentData) ? "SUCCESS" : "REJECTED";
        }
        return "WAITING";
    }

    boolean isValidVoucherCode(String voucherCode) {
        if (voucherCode == null) {
            return false;
        }

        if (voucherCode.length() != 16) {
            return false;
        }

        if (!voucherCode.startsWith("ESHOP")) {
            return false;
        }

        int numericCount = 0;
        for (char c : voucherCode.toCharArray()) {
            if (Character.isDigit(c)) {
                numericCount++;
            }
        }

        return numericCount == 8;
    }

    boolean isValidBankTransferData(Map<String, String> paymentData) {
        if (paymentData == null) {
            return false;
        }

        String bankName = paymentData.get("bankName");
        String referenceCode = paymentData.get("referenceCode");

        return bankName != null && !bankName.isEmpty() &&
                referenceCode != null && !referenceCode.isEmpty();
    }
}
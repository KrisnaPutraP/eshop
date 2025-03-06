package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Payment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PaymentRepository {
    private Map<String, Payment> paymentMap = new HashMap<>();

    public Payment save(Payment payment) {
    }

    public Payment findById(String id) {
    }

    public List<Payment> findAll() {
    }
}
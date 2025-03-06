package id.ac.ui.cs.advprog.eshop.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    BANK_TRANSFER("BANK_TRANSFER"),
    VOUCHER("VOUCHER");

    private final String value;

    PaymentMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean contains(String method) {
        for (PaymentMethod paymentMethod : PaymentMethod.values()) {
            if (paymentMethod.getValue().equals(method)) {
                return true;
            }
        }
        return false;
    }
}
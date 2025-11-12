package com.vendeskgt.vendeskgt.domain.enums;

public enum PaymentMethodEnum {
    CREDITCARD("Credit Card"),
    TRANSFER("Transferencia");

    private final String paymentMethodDisplay;

    PaymentMethodEnum(String paymentMethodDisplay){
        this.paymentMethodDisplay = paymentMethodDisplay;
    }

    public String getPaymentMethodDisplay() {
        return paymentMethodDisplay;
    }
}

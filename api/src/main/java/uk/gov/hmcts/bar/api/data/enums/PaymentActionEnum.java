package uk.gov.hmcts.bar.api.data.enums;

import java.util.Arrays;
import java.util.Optional;

public enum PaymentActionEnum {
    PROCESS("payment-actions-process", "Process"), SUSPENSE("payment-actions-suspense", "Suspense"),
    SUSPENSE_DEFICIENCY("payment-actions-suspence-deficiency", "Suspense Deficiency"),
    RETURN("payment-actions-return", "Return"), REFUND("payment-actions-refund", "Refund");

    private String featureKey = null;

    private String displayValue = null;

    PaymentActionEnum(String featureKey, String displayValue) {
        this.featureKey = featureKey;
        this.displayValue = displayValue;
    }

    public String featureKey() {
        return this.featureKey;
    }

    public String displayValue() {
        return this.displayValue;
    }

    public static Optional<PaymentActionEnum> findByDisplayValue(String displayValue) {
        return Arrays.stream(PaymentActionEnum.values())
            .filter(paymentActionEnum -> paymentActionEnum.displayValue.equals(displayValue))
            .findFirst();
    }

}

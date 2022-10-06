package uk.gov.hmcts.bar.api.data.utils;

import uk.gov.hmcts.bar.api.data.enums.PaymentStatusEnum;

import java.beans.PropertyEditorSupport;

public class PaymentStatusEnumConverter extends PropertyEditorSupport {
    @Override
    public void setAsText(final String text) {
        setValue(PaymentStatusEnum.getPaymentStatusEnum(text));
    }
}

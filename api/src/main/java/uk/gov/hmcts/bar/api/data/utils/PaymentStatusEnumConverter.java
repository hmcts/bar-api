package uk.gov.hmcts.bar.api.data.utils;

import java.beans.PropertyEditorSupport;

import uk.gov.hmcts.bar.api.data.enums.PaymentStatusEnum;

public class PaymentStatusEnumConverter extends PropertyEditorSupport {
	@Override
	public void setAsText(final String text) throws IllegalArgumentException {
		setValue(PaymentStatusEnum.getPaymentStatusEnum(text));
	}
}

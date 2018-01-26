package uk.gov.hmcts.bar.api.data.enums;

import java.util.HashMap;
import java.util.Map;

public enum PaymentActionEnum {

	PROCESS("P", "Process"), SUSPENSE("S", "Suspense"), SUSPENSE_DEFICIENCY("SD", "Suspence Deficiency"), RETURN("R",
			"Return"), REFUND("RF", "Refund");

	private static final Map<String, PaymentActionEnum> paymentActionEnumMap = new HashMap<>();

	static {
		PaymentActionEnum[] paymentActionEnums = PaymentActionEnum.values();
		for (PaymentActionEnum paymentActionEnum : paymentActionEnums) {
			paymentActionEnumMap.put(paymentActionEnum.dbKey(), paymentActionEnum);
		}
	}

	private String dbKey = null;

	private String displayValue = null;

	PaymentActionEnum(String dbKey, String displayValue) {
		this.dbKey = dbKey;
		this.displayValue = displayValue;
	}

	public String dbKey() {
		return this.dbKey;
	}

	public String displayValue() {
		return this.displayValue;
	}
	
	public static PaymentActionEnum getPaymentActionEnum(String dbKey) {
		return paymentActionEnumMap.get(dbKey);
	}
}

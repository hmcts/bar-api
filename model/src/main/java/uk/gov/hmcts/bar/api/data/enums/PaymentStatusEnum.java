package uk.gov.hmcts.bar.api.data.enums;

import java.util.HashMap;
import java.util.Map;

public enum PaymentStatusEnum {

	DRAFT("D", "Draft"), PENDING("P", "Pending"), VALIDATED("V", "Validated"), PENDING_APPROVAL("PA",
			"Pending Approval"), APPROVED("A", "Approved"), TRANSFERREDTOBAR("TTB", "Transferred to bar"), REJECTED("REJ", "Rejected");

	private static final Map<String, PaymentStatusEnum> paymentStatusEnumMap = new HashMap<>();

	static {
		PaymentStatusEnum[] paymentStatusEnums = PaymentStatusEnum.values();
		for (PaymentStatusEnum paymentStatusEnum : paymentStatusEnums) {
			paymentStatusEnumMap.put(paymentStatusEnum.dbKey(), paymentStatusEnum);
		}
	}

	private String dbKey = null;

	private String displayValue = null;

	PaymentStatusEnum(String dbKey, String displayValue) {
		this.dbKey = dbKey;
		this.displayValue = displayValue;
	}

	public String dbKey() {
		return this.dbKey;
	}

	public String displayValue() {
		return this.displayValue;
	}

	public static PaymentStatusEnum getPaymentStatusEnum(String dbKey) {
		return paymentStatusEnumMap.get(dbKey);
	}


}

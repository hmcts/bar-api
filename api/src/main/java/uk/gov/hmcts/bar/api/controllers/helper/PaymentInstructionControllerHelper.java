package uk.gov.hmcts.bar.api.controllers.helper;

import java.util.List;
import java.util.stream.Collectors;

import uk.gov.hmcts.bar.api.data.enums.PaymentStatusEnum;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;

public class PaymentInstructionControllerHelper {
	
	private PaymentInstructionControllerHelper() {
		
	}
	
	public static List<PaymentInstruction> updateStatusDisplayValue(final List<PaymentInstruction> paymentInstructions) {
		return paymentInstructions.stream().map(paymentInstruction -> {
			paymentInstruction
					.setStatus(PaymentStatusEnum.getPaymentStatusEnum(paymentInstruction.getStatus()).displayValue());
			return paymentInstruction;
		}).collect(Collectors.toList());
	}

}

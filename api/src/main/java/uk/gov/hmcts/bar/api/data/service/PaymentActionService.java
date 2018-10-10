package uk.gov.hmcts.bar.api.data.service;

import java.util.List;
import java.util.stream.Collectors;

import org.ff4j.FF4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.hmcts.bar.api.data.enums.PaymentActionEnum;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionAction;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionActionRepository;

@Service
public class PaymentActionService {
	private final PaymentInstructionActionRepository paymentInstructionActionRepository;

	private final FF4j ff4j;

	@Autowired
	public PaymentActionService(PaymentInstructionActionRepository paymentInstructionActionRepository, FF4j ff4j) {
		this.paymentInstructionActionRepository = paymentInstructionActionRepository;
		this.ff4j = ff4j;
	}

	public List<PaymentInstructionAction> getAllPaymentInstructionAction() {
		List<PaymentInstructionAction> piaList = paymentInstructionActionRepository.findAll();
		return piaList.stream()
				.filter(action -> checkIfActionEnabled(action.getAction())).collect(Collectors.toList());
	}

	private boolean checkIfActionEnabled(String action) {
		boolean[] ret = { true };
		PaymentActionEnum.findByDisplayValue(action)
				.ifPresent(paymentActionEnum -> ret[0] = ff4j.check(paymentActionEnum.featureKey()));
		return ret[0];
	}
}

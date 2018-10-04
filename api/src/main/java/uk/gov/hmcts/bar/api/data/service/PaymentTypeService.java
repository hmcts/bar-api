package uk.gov.hmcts.bar.api.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import uk.gov.hmcts.bar.api.data.model.PaymentInstructionAction;
import uk.gov.hmcts.bar.api.data.model.PaymentType;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionActionRepository;
import uk.gov.hmcts.bar.api.data.repository.PaymentTypeRepository;

@Service
public class PaymentTypeService {

	private final PaymentTypeRepository paymentTypeRepository;

	private final PaymentInstructionActionRepository paymentInstructionActionRepository;

	@Autowired
	public PaymentTypeService(PaymentTypeRepository paymentTypeRepository,
			PaymentInstructionActionRepository paymentInstructionActionRepository) {
		this.paymentTypeRepository = paymentTypeRepository;
		this.paymentInstructionActionRepository = paymentInstructionActionRepository;
	}

	public List<PaymentType> getAllPaymentTypes() {
		return paymentTypeRepository.findAll();
	}

	public List<PaymentInstructionAction> getAllPaymentInstructionAction() {
		return paymentInstructionActionRepository.findAll();
	}

	@Cacheable("paymentTypes")
	public PaymentType getPaymentTypeById(String id) {
		return paymentTypeRepository.getOne(id);
	}

}

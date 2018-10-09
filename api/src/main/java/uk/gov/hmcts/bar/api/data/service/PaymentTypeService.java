package uk.gov.hmcts.bar.api.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import uk.gov.hmcts.bar.api.data.model.PaymentType;
import uk.gov.hmcts.bar.api.data.repository.PaymentTypeRepository;

@Service
public class PaymentTypeService {

	private final PaymentTypeRepository paymentTypeRepository;

	@Autowired
	public PaymentTypeService(PaymentTypeRepository paymentTypeRepository) {
		this.paymentTypeRepository = paymentTypeRepository;
	}

	public List<PaymentType> getAllPaymentTypes() {
		return paymentTypeRepository.findAll();
	}

	@Cacheable("paymentTypes")
	public PaymentType getPaymentTypeById(String id) {
		return paymentTypeRepository.getOne(id);
	}

}

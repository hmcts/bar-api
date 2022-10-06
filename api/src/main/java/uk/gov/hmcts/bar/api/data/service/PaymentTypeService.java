package uk.gov.hmcts.bar.api.data.service;

import org.ff4j.FF4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.bar.api.data.model.PaymentType;
import uk.gov.hmcts.bar.api.data.repository.PaymentTypeRepository;

import java.util.List;

@Service
public class PaymentTypeService {

    private final PaymentTypeRepository paymentTypeRepository;
    private static final String FULL_REMISSION_ID = "FULL_REMISSION";
    private static final String FULL_REMISSION_FEATURE_ID = "full-remission";
    private final FF4j ff4j;

    @Autowired
    public PaymentTypeService(PaymentTypeRepository paymentTypeRepository,FF4j ff4j) {
        this.paymentTypeRepository = paymentTypeRepository;
        this.ff4j = ff4j;
    }

    public List<PaymentType> getAllPaymentTypes() {
        List<PaymentType> paymentTypes = paymentTypeRepository.findAll();
        if (!(ff4j.check(FULL_REMISSION_FEATURE_ID))) {
        paymentTypes.removeIf(pt -> pt.getId().equals(FULL_REMISSION_ID));
        }
        return paymentTypes;
    }

    @Cacheable("paymentTypes")
    public PaymentType getPaymentTypeById(String id) {
        return paymentTypeRepository.getOne(id);
    }

}

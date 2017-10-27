package uk.gov.hmcts.bar.api.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ReferenceDataService {

    private final PaymentTypeRepository paymentTypeRepository;

    @Autowired
    public ReferenceDataService(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }

    public List<PaymentType> getAllPaymentTypes(){
        return paymentTypeRepository.findAll();
    }

}

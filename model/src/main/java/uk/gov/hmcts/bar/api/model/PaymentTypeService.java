package uk.gov.hmcts.bar.api.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PaymentTypeService {

    private final PaymentTypeRepository paymentTypeRepository;

    @Autowired
    public PaymentTypeService(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }

    public List<PaymentType> getAllPaymentTypes(){
        return paymentTypeRepository.findAll();
    }


    public PaymentType getPaymentTypeById(Integer id){
        return paymentTypeRepository.findOne(id);
    }

}

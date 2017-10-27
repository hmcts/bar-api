package uk.gov.hmcts.bar.api.model;


import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentTypeRepository paymentTypeRepository;

    public PaymentService(PaymentRepository paymentRepository,PaymentTypeRepository paymentTypeRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentTypeRepository = paymentTypeRepository;
    }

    public List<Payment> getPayments(String userId, LocalDateTime fromDate, LocalDateTime toDate ){
        return paymentRepository.
        findByCreatedByUserIdAndPaymentDateBetween(userId,fromDate.truncatedTo(ChronoUnit.DAYS),toDate);
    }

    public Payment savePayment(Payment payment){
        return paymentRepository.save(payment);
    }

    public List<PaymentType> findAllPaymentTypes(){
        return paymentTypeRepository.findAll();
    }

    public PaymentType findPaymentType(Integer id){
        return paymentTypeRepository.findOne(id);
    }

}

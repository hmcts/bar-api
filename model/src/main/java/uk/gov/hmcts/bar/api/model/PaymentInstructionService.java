package uk.gov.hmcts.bar.api.model;


import org.springframework.stereotype.Service;

@Service
public class PaymentInstructionService {

    private PaymentInstructionRepository paymentInstructionRepository;


    public PaymentInstructionService(PaymentInstructionRepository paymentInstructionRepository) {
        this.paymentInstructionRepository = paymentInstructionRepository;

    }

    public PaymentInstruction createPaymentInstruction(PaymentInstruction paymentInstruction){
        paymentInstruction.setStatus(PaymentInstruction.DRAFT);
        return paymentInstructionRepository.save(paymentInstruction);
    }


}

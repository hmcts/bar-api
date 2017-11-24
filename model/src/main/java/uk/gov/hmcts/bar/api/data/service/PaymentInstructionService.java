package uk.gov.hmcts.bar.api.data.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentReference;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionRepository;

@Service
@Transactional
public class PaymentInstructionService {

    private static final String SITE_ID="BR01";
    private PaymentInstructionRepository paymentInstructionRepository;
    private PaymentReferenceService paymentReferenceService;


    public PaymentInstructionService(PaymentReferenceService paymentReferenceService,
                                     PaymentInstructionRepository paymentInstructionRepository) {
        this.paymentReferenceService = paymentReferenceService;
        this.paymentInstructionRepository = paymentInstructionRepository;

    }

    public PaymentInstruction createPaymentInstruction(PaymentInstruction paymentInstruction){
        paymentInstruction.setStatus(PaymentInstruction.DRAFT);
        PaymentReference nextPaymentReference = paymentReferenceService.getNextPaymentReferenceSequenceBySite(SITE_ID);
        paymentInstruction.setSiteId(SITE_ID);
        paymentInstruction.setDailySequenceId(nextPaymentReference.getDailySequenceId());
        return paymentInstructionRepository.save(paymentInstruction);
    }


}

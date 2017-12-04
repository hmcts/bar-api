package uk.gov.hmcts.bar.api.data.service;


import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.bar.api.data.exceptions.PaymentInstructionNotFoundException;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentReference;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionRepository;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.time.LocalDateTime.now;

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


    public List<PaymentInstruction> getAllPaymentInstructions() {
        return paymentInstructionRepository.findBySiteIdAndPaymentDateIsAfter(SITE_ID, now().truncatedTo(ChronoUnit.DAYS));
    }

    public void deleteCurrentPaymentInstructionWithDraftStatus(Integer id){
        try {
            paymentInstructionRepository.deletePaymentInstructionByIdAndStatusAndPaymentDateAfter(id,PaymentInstruction.DRAFT,now().truncatedTo(ChronoUnit.DAYS));
        }
        catch (EmptyResultDataAccessException erdae){
            throw new PaymentInstructionNotFoundException(id);
        }

    }
}

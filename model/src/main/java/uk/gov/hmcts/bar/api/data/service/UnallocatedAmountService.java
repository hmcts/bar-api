package uk.gov.hmcts.bar.api.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.bar.api.data.model.CaseReference;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.repository.CaseReferenceRepository;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionRepository;

import java.util.List;

@Service
public class UnallocatedAmountService {

    private final PaymentInstructionRepository paymentInstructionRepository;

    @Autowired
    public UnallocatedAmountService(PaymentInstructionRepository paymentInstructionRepository){
        this.paymentInstructionRepository = paymentInstructionRepository;
    }

    public int calculateUnallocatedAmount(int paymentId){
        PaymentInstruction paymentInstruction = this.paymentInstructionRepository.getOne(paymentId);
        return paymentInstruction.getAmount() -
            (paymentInstruction.getCaseReferences() == null ? 0 : paymentInstruction.getCaseReferences().stream()
                .mapToInt(reference -> reference.getCaseFeeDetails() == null ? 0 : reference.getCaseFeeDetails().stream()
                    .mapToInt(value -> validateAmount(value.getAmount()) - validateAmount(value.getRemissionAmount()) + validateAmount(value.getRefundAmount()))
                .sum())
            .sum());
    }

    private int validateAmount(Integer value){
        return value != null ? value : 0;
    }
}

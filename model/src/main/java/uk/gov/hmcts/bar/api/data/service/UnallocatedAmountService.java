package uk.gov.hmcts.bar.api.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionRepository;

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
            (paymentInstruction.getCaseFeeDetails() == null ? 0 : paymentInstruction.getCaseFeeDetails().stream()
                .mapToInt(caseFeeDetail -> validateAmount(caseFeeDetail.getAmount()) - validateAmount(caseFeeDetail.getRemissionAmount()) + validateAmount(caseFeeDetail.getRefundAmount()))
                .sum());
    }

    private int validateAmount(Integer value){
        return value != null ? value : 0;
    }
}

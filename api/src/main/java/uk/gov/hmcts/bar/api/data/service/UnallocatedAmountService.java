package uk.gov.hmcts.bar.api.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.hmcts.bar.api.data.model.CaseFeeDetail;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionRepository;

@Service
public class UnallocatedAmountService {

    private final PaymentInstructionRepository paymentInstructionRepository;

    @Autowired
    public UnallocatedAmountService(PaymentInstructionRepository paymentInstructionRepository){
        this.paymentInstructionRepository = paymentInstructionRepository;
    }
    
	public int calculateUnallocatedAmount(int paymentId) {
		PaymentInstruction paymentInstruction = this.paymentInstructionRepository.getOne(paymentId);
		List<CaseFeeDetail> cfdList = paymentInstruction.getCaseFeeDetails();
		if (cfdList.isEmpty()) {
			cfdList = this.paymentInstructionRepository.getCaseFeeDetails(paymentId);
		}
		return paymentInstruction.getAmount() - (cfdList.stream()
				.mapToInt(caseFeeDetail -> validateAmount(caseFeeDetail.getAmount())
						- validateAmount(caseFeeDetail.getRemissionAmount())
						+ validateAmount(caseFeeDetail.getRefundAmount()))
				.sum());
	}

    private int validateAmount(Integer value){
        return value != null ? value : 0;
    }
}

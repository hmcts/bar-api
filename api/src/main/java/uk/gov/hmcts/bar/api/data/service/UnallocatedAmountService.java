package uk.gov.hmcts.bar.api.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.bar.api.data.model.CaseFeeDetail;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionRepository;

import java.util.List;

@Service
public class UnallocatedAmountService {

    private final PaymentInstructionRepository paymentInstructionRepository;

    @Autowired
    public UnallocatedAmountService(PaymentInstructionRepository paymentInstructionRepository){
        this.paymentInstructionRepository = paymentInstructionRepository;
    }

	public int calculateUnallocatedAmount(int paymentId) {
		PaymentInstruction paymentInstruction = this.paymentInstructionRepository.getOne(paymentId);
		int unallocatedAmount = 0 ;
		if (!(paymentInstruction.getPaymentType().getId().equals("FULL_REMISSION"))){

		List<CaseFeeDetail> cfdList = paymentInstruction.getCaseFeeDetails();
		if (cfdList.isEmpty()) {
			cfdList = this.paymentInstructionRepository.getCaseFeeDetails(paymentId);
		}
          unallocatedAmount =  paymentInstruction.getAmount() - (cfdList.stream()
				.mapToInt(caseFeeDetail -> validateAmount(caseFeeDetail.getAmount())
						- validateAmount(caseFeeDetail.getRemissionAmount())
						+ validateAmount(caseFeeDetail.getRefundAmount()))
				.sum());
		}
		return unallocatedAmount;
	}

    private int validateAmount(Integer value){
        return value != null ? value : 0;
    }
}

package uk.gov.hmcts.bar.api.data.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.bar.api.data.enums.PaymentActionEnum;
import uk.gov.hmcts.bar.api.data.exceptions.PaymentProcessException;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionUpdateRequest;
import uk.gov.hmcts.bar.api.data.service.UnallocatedAmountService;

@Component
public class UnallocatedAmountValidator implements Validator {

    private final UnallocatedAmountService unallocatedAmountService;

    @Autowired
    public UnallocatedAmountValidator(UnallocatedAmountService unallocatedAmountService) {
        this.unallocatedAmountService = unallocatedAmountService;
    }

    @Override
    public void validate(PaymentInstruction paymentInstruction, PaymentInstructionUpdateRequest updateRequest) throws PaymentProcessException {
        if (PaymentActionEnum.PROCESS.displayValue().equals(updateRequest.getAction())
            && unallocatedAmountService.calculateUnallocatedAmount(paymentInstruction) != 0) {
            throw new PaymentProcessException("Please allocate all amount before processing.");
        }
    }
}

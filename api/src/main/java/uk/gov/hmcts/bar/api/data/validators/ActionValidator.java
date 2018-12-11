package uk.gov.hmcts.bar.api.data.validators;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.bar.api.data.enums.PaymentActionEnum;
import uk.gov.hmcts.bar.api.data.exceptions.PaymentProcessException;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionUpdateRequest;

@Component
public class ActionValidator implements Validator {


    @Override
    public void validate(PaymentInstruction paymentInstruction, PaymentInstructionUpdateRequest updateRequest) throws PaymentProcessException {
        if (!PaymentActionEnum.RETURN.displayValue().equals(updateRequest.getAction()) &&
            !PaymentActionEnum.WITHDRAW.displayValue().equals(updateRequest.getAction())) {
            return;
        }
        if (!paymentInstruction.getCaseFeeDetails().isEmpty()) {
            throw new PaymentProcessException("Please remove all case and fee details before attempting this action.");
        }
    }
}

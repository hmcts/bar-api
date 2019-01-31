package uk.gov.hmcts.bar.api.data.validators;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.bar.api.data.exceptions.PaymentProcessException;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionUpdateRequest;

@Component
public class FullRemissionValidator implements Validator {

    @Override
    public void validate(PaymentInstruction paymentInstruction, PaymentInstructionUpdateRequest updateRequest) throws PaymentProcessException {
        if (!isFullRemission(paymentInstruction)){
            return;
        }
        if (paymentInstruction.getCaseFeeDetails().size() != 1){
            throw new PaymentProcessException("Full Remission must have one and only one fee");
        }
    }

    private boolean isFullRemission(PaymentInstruction paymentInstruction) {
        return paymentInstruction.getPaymentType().getId().equals("FULL_REMISSION");
    }
}

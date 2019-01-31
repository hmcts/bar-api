package uk.gov.hmcts.bar.api.data.validators;

import uk.gov.hmcts.bar.api.data.exceptions.PaymentProcessException;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionUpdateRequest;

public interface Validator {

    void validate(PaymentInstruction paymentInstruction, PaymentInstructionUpdateRequest updateRequest) throws PaymentProcessException;
}

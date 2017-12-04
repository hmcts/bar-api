package uk.gov.hmcts.bar.api.data.exceptions;

public class PaymentInstructionNotFoundException extends ResourceNotFoundException {
    public PaymentInstructionNotFoundException(Integer id) {
        super("payment instruction", "id", id);
    }
}


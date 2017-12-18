package uk.gov.hmcts.bar.api.data.exceptions;

@SuppressWarnings("serial")
public class PaymentInstructionNotFoundException extends ResourceNotFoundException {
    public PaymentInstructionNotFoundException(Integer id) {
        super("payment instruction", "id", id);
    }
}


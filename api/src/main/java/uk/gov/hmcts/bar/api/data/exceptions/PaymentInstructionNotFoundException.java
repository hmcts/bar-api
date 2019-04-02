package uk.gov.hmcts.bar.api.data.exceptions;

@SuppressWarnings("serial")
public class PaymentInstructionNotFoundException extends ResourceNotFoundException {

    public PaymentInstructionNotFoundException(Integer id) {
        super("payment instruction", "id", id);
    }

    public PaymentInstructionNotFoundException(Integer id, String siteId) {
        super("payment instruction on site " + siteId, "id", id);
    }
}


package uk.gov.hmcts.bar.api.data.exceptions;

public class PaymentInstructionConverterException extends RuntimeException {

    public PaymentInstructionConverterException(String message){
        super(message);
    }
}

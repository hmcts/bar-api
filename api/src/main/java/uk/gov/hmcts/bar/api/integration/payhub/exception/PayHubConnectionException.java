package uk.gov.hmcts.bar.api.integration.payhub.exception;

public class PayHubConnectionException extends RuntimeException {

    public PayHubConnectionException(Throwable t) {
        super(t);
    }
}

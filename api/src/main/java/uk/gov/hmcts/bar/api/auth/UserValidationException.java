package uk.gov.hmcts.bar.api.auth;

public class UserValidationException extends RuntimeException {

    public UserValidationException(String message) {
        super(message);
    }
}

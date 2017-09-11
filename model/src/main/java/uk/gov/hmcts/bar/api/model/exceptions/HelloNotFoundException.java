package uk.gov.hmcts.bar.api.model.exceptions;

public class HelloNotFoundException extends ResourceNotFoundException {
    public HelloNotFoundException(String hello) {
        super("hello", "hello", hello);
    }
}

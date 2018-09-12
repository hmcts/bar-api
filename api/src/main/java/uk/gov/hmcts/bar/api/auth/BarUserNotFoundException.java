package uk.gov.hmcts.bar.api.auth;

public class BarUserNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public BarUserNotFoundException(String msg) {
        super(msg);
    }

}

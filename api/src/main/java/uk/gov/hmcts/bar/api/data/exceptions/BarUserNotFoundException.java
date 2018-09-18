package uk.gov.hmcts.bar.api.data.exceptions;

public class BarUserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BarUserNotFoundException(String msg) {
        super(msg);
    }

}


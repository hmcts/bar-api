package uk.gov.hmcts.bar.api.data.exceptions;

public class MissingSiteIdException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MissingSiteIdException(String msg) {
        super(msg);
    }

}

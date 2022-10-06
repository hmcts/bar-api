package uk.gov.hmcts.bar.api.data.exceptions;

public class InvalidActionException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidActionException(String msg) {
        super(msg);
    }

}

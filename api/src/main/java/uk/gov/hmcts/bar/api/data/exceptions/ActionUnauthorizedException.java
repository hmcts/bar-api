package uk.gov.hmcts.bar.api.data.exceptions;

public class ActionUnauthorizedException  extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public ActionUnauthorizedException(String msg) {
        super(msg);
    }
}

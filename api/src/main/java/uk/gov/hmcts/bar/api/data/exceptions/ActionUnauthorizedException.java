package uk.gov.hmcts.bar.api.data.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(value= HttpStatus.FORBIDDEN)
public class ActionUnauthorizedException  extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public ActionUnauthorizedException(String msg) {
        super(msg);
    }
}

package uk.gov.hmcts.bar.api.data.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = -6958128182491079251L;

    private String value;
    private String message;

    public BadRequestException() {
        super();
    }

    public BadRequestException(String value, String message) {
        this.value = value;
        this.message = message;
    }

    public String getValue() {
        return value;
    }

    public String getMessage() {
        return this.message;
    }

}

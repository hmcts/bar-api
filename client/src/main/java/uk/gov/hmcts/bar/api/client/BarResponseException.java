package uk.gov.hmcts.bar.api.client;


import lombok.Getter;

@Getter
public class BarResponseException extends RuntimeException {

    private final int status;
    private final String reason;

    public BarResponseException(int status, String reason) {
        this.status = status;
        this.reason = reason;
    }
}

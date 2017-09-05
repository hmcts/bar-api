package uk.gov.hmcts.bar.api.contract;

import lombok.Data;

@Data
public class ErrorDto {
    private final String message;
}

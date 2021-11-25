package uk.gov.hmcts.bar.api.data.exceptions;

public class CaseFeeDetailNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public CaseFeeDetailNotFoundException(Integer idValue) {
        super("CaseFeeDetail", "id", idValue);
    }
}

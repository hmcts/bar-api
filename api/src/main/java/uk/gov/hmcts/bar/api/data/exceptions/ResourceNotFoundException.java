package uk.gov.hmcts.bar.api.data.exceptions;

import lombok.Getter;

@SuppressWarnings("serial")
@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final String resourceName;
    private final String idName;
    private final Object idValue;

    public ResourceNotFoundException(String resourceName, String idName, Object idValue) {
        this.resourceName = resourceName;
        this.idName = idName;
        this.idValue = idValue;
    }

    @Override
    public String getMessage() {
    		return resourceName+": "+idName+" = "+idValue;
    }
}

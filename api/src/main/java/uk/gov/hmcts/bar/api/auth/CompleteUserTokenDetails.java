package uk.gov.hmcts.bar.api.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompleteUserTokenDetails {

    private String defaultService;
    private String email;
    private String forename;
    private String surname;
    private final String id;
    private final Set<String> roles;
}

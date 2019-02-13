package uk.gov.hmcts.bar.api.auth;

import lombok.Getter;
import uk.gov.hmcts.reform.auth.checker.core.user.User;

import java.util.Set;

public class BarUserPrincipal extends User {

    @Getter
    private String forename;
    @Getter
    private String surname;
    @Getter
    private String email;

    public BarUserPrincipal(String principleId, Set<String> roles, String forename, String surname, String email) {
        super(principleId, roles);
        this.forename = forename;
        this.surname = surname;
        this.email = email;
    }
}

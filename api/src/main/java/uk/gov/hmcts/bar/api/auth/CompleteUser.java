package uk.gov.hmcts.bar.api.auth;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uk.gov.hmcts.reform.auth.checker.core.user.User;

import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class CompleteUser extends User {

    private String email;
    private String forename;
    private String surname;

    public CompleteUser(String principleId, Set<String> roles) {
        super(principleId, roles);
    }

    public CompleteUser(String principleId, Set<String> roles, String email, String forename, String surname) {
        super(principleId, roles);
        this.email = email;
        this.forename = forename;
        this.surname = surname;
    }
}

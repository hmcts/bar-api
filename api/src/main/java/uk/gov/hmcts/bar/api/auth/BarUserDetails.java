package uk.gov.hmcts.bar.api.auth;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import uk.gov.hmcts.reform.auth.checker.spring.useronly.UserDetails;

import java.util.Collection;

@EqualsAndHashCode
public class BarUserDetails extends UserDetails {

    @Getter
    private String forename;
    @Getter
    private String surname;
    @Getter
    private String email;


    public BarUserDetails(String username, String token, Collection<String> authorities, String surname, String forename, String email) {
        super(username, token, authorities);
        this.surname = surname;
        this.forename = forename;
        this.email = email;
    }

}

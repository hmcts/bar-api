package uk.gov.hmcts.bar.api.auth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import uk.gov.hmcts.bar.api.data.model.BarUser;
import uk.gov.hmcts.reform.auth.checker.spring.useronly.UserDetails;

import java.util.Collection;

import static java.util.stream.Collectors.joining;

@Getter
public class CompleteUserDetails extends UserDetails {

    private final String forename;
    private final String surname;
    private final String email;

    public CompleteUserDetails(String username, String token, Collection<String> authorities, String forename, String surname, String email) {
        super(username, token, authorities);
        this.forename = forename;
        this.surname = surname;
        this.email = email;
    }

    public BarUser createBarUser(){
        return BarUser.builder()
            .forename(this.getForename())
            .surname(this.getSurname())
            .roles(this.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(joining(", ")))
            .idamId(this.getUsername())
            .build();
    }
}

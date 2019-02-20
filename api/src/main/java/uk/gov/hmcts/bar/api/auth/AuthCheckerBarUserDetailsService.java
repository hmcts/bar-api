package uk.gov.hmcts.bar.api.auth;

import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import uk.gov.hmcts.reform.auth.checker.core.user.User;

public class AuthCheckerBarUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) {
        Object principal = token.getPrincipal();

        if (principal instanceof User) {
            BarUserDetails user = (BarUserDetails) principal;
            return user;
        }

        throw new UserAuthenticationException("Failed to retrieve bar user principal");
    }
}

package uk.gov.hmcts.bar.api.auth;

import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import uk.gov.hmcts.reform.auth.checker.core.service.Service;
import uk.gov.hmcts.reform.auth.checker.spring.serviceanduser.ServiceAndUserDetails;
import uk.gov.hmcts.reform.auth.checker.spring.serviceanduser.ServiceAndUserPair;
import uk.gov.hmcts.reform.auth.checker.spring.serviceonly.ServiceDetails;


public class UserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    public UserDetailsService() {
    }

    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {
        Object principal = token.getPrincipal();
        if (principal instanceof Service) {
            return new ServiceDetails(((Service) principal).getPrincipal());
        } else if (principal instanceof CompleteUser) {
            CompleteUser user = (CompleteUser) principal;
            return new CompleteUserDetails(user.getPrincipal(), (String) token.getCredentials(), user.getRoles(),
                user.getForename(), user.getSurname(), user.getEmail());
        } else {
            ServiceAndUserPair serviceAndUserPair = (ServiceAndUserPair) principal;
            return new ServiceAndUserDetails(serviceAndUserPair.getUser().getPrincipal(), (String) token.getCredentials(), serviceAndUserPair.getUser().getRoles(), serviceAndUserPair.getService().getPrincipal());
        }
    }
}

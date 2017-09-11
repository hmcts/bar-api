package uk.gov.hmcts.bar.api.configuration;


import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import uk.gov.hmcts.auth.checker.RequestAuthorizer;
import uk.gov.hmcts.auth.checker.spring.useronly.AuthCheckerUserOnlyFilter;
import uk.gov.hmcts.auth.checker.user.User;

@Configuration
public class AuthCheckerConfiguration {

    @Bean
    public Function<HttpServletRequest, Collection<String>> authorizedRolesExtractor() {
        return (any) -> Collections.singletonList("admin");
    }

    @Bean
    public Function<HttpServletRequest, Optional<String>> userIdExtractor() {
        return (request) -> Optional.empty();
    }

    @Bean
    public AuthCheckerUserOnlyFilter authCheckerServiceAndUserFilter(RequestAuthorizer<User> userRequestAuthorizer,
                                                                     AuthenticationManager authenticationManager) {
        AuthCheckerUserOnlyFilter filter = new AuthCheckerUserOnlyFilter(userRequestAuthorizer);
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }
}

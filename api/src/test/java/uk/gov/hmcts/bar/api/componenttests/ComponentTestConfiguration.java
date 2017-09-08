package uk.gov.hmcts.bar.api.componenttests;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.auth.checker.SubjectResolver;
import uk.gov.hmcts.auth.checker.user.User;
import uk.gov.hmcts.bar.api.componenttests.backdoors.UserResolverBackdoor;

@Configuration
public class ComponentTestConfiguration {

    @Bean
    @ConditionalOnProperty(name = "idam.client.backdoor", havingValue = "true")
    public SubjectResolver<User> userResolver() {
        return new UserResolverBackdoor();
    }
}

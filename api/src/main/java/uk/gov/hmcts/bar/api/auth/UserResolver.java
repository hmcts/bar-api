package uk.gov.hmcts.bar.api.auth;

import uk.gov.hmcts.bar.api.data.model.BarUser;
import uk.gov.hmcts.bar.api.data.service.BarUserService;
import uk.gov.hmcts.reform.auth.checker.core.SubjectResolver;
import uk.gov.hmcts.reform.auth.checker.core.user.User;
import uk.gov.hmcts.reform.auth.parser.idam.core.user.token.UserTokenParser;

public class UserResolver implements SubjectResolver<User> {

    private final UserTokenParser<UserTokenDetails> userTokenParser;
    private final BarUserService barUserService;

    public UserResolver(UserTokenParser userTokenParser, BarUserService barUserService) {
        this.userTokenParser = userTokenParser;
        this.barUserService = barUserService;
    }

    @Override
    public User getTokenDetails(String bearerToken) {
        UserTokenDetails details = userTokenParser.parse(bearerToken);
        BarUser barUser = new BarUser(
            details.getId(),
            details.getRoles(),
            details.getEmail(),
            details.getForename(),
            details.getSurname()
        );
        barUserService.saveUser(barUser);
        return new BarUserDetails(details.getId(), bearerToken, details.getRoles(), details.getForename(), details.getSurname(), details.getEmail());
    }
}

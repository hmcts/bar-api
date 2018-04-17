package uk.gov.hmcts.bar.api.auth;

import uk.gov.hmcts.bar.api.data.model.BarUser;
import uk.gov.hmcts.bar.api.data.service.BarUserService;
import uk.gov.hmcts.reform.auth.checker.core.SubjectResolver;
import uk.gov.hmcts.reform.auth.checker.core.user.User;
import uk.gov.hmcts.reform.auth.parser.idam.core.user.token.UserTokenParser;

import static java.util.stream.Collectors.joining;

public class CompleteUserResolver implements SubjectResolver<User> {

    private final UserTokenParser<CompleteUserTokenDetails> userTokenParser;
    private final BarUserService barUserService;

    public CompleteUserResolver(UserTokenParser userTokenParser, BarUserService barUserService) {
        this.userTokenParser = userTokenParser;
        this.barUserService = barUserService;
    }

    @Override
    public User getTokenDetails(String bearerToken) {
        CompleteUserTokenDetails details = userTokenParser.parse(bearerToken);
        BarUser barUser = new BarUser(
            details.getId(),
            details.getRoles(),
            details.getEmail(),
            details.getForename(),
            details.getSurname()
        );
        barUserService.checkUser(barUser);
        return new User(details.getId(), details.getRoles());
    }
}

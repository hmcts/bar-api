package uk.gov.hmcts.bar.api.auth;

import uk.gov.hmcts.bar.api.data.model.BarUser;
import uk.gov.hmcts.bar.api.data.service.BarUserService;
import uk.gov.hmcts.reform.auth.checker.core.SubjectResolver;
import uk.gov.hmcts.reform.auth.parser.idam.core.user.token.UserTokenParser;

import static java.util.stream.Collectors.joining;

public class CompleteUserResolver implements SubjectResolver<CompleteUser> {

    private final UserTokenParser<CompleteUserTokenDetails> userTokenParser;
    private final BarUserService barUserService;

    public CompleteUserResolver(UserTokenParser userTokenParser, BarUserService barUserService) {
        this.userTokenParser = userTokenParser;
        this.barUserService = barUserService;
    }

    @Override
    public CompleteUser getTokenDetails(String bearerToken) {
        CompleteUserTokenDetails details = userTokenParser.parse(bearerToken);
        BarUser barUser = BarUser.builder()
            .forename(details.getForename())
            .surname(details.getSurname())
            .roles(details.getRoles().stream().collect(joining(", ")))
            .idamId(details.getId())
            .build();
        barUserService.checkUser(barUser);
        return new CompleteUser(details.getId(), details.getRoles(), details.getEmail(), details.getForename(), details.getSurname());
    }
}

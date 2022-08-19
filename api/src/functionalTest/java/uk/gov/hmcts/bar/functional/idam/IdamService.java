package uk.gov.hmcts.bar.functional.idam;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.bar.functional.config.TestConfigProperties;
import uk.gov.hmcts.bar.functional.idam.models.User;

import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

import static uk.gov.hmcts.bar.functional.idam.IdamApi.*;
import static uk.gov.hmcts.bar.functional.idam.IdamApi.CreateUserRequest.*;

@Service
public class IdamService {
    private static final Logger LOG = LoggerFactory.getLogger(IdamService.class);
    private static final String SCOPES_MANAGE_USER = "openid profile roles manage-user";
    private static final String GRANT_TYPE = "password";
    private static final String BEARER = "Bearer ";

    private static IdamApi idamApi;
    private final TestConfigProperties testConfig;

    @Autowired
    public IdamService(TestConfigProperties testConfig) {
        this.testConfig = testConfig;
        idamApi = Feign.builder()
            .encoder(new JacksonEncoder())
            .decoder(new JacksonDecoder())
            .target(IdamApi.class, testConfig.getIdamApiUrl());
    }

    public String authenticateUserWithManageScope(String username, String password) {

        try {
            TokenExchangeResponse tokenExchangeResponse = idamApi.tokenExchangeResponse(
                username,
                password,
                SCOPES_MANAGE_USER,
                GRANT_TYPE,
                testConfig.getOauth2().getClientId(),
                testConfig.getOauth2().getClientSecret(),
                testConfig.getOauth2().getRedirectUrl());

            return BEARER + tokenExchangeResponse.getAccessToken();
        } catch (Exception ex) {
            LOG.info(ex.getMessage());
        }
        return null;
    }

    public User createUserWith(String email, String... roles) {
        CreateUserRequest userRequest = userRequest(email, roles);
        idamApi.createUser(userRequest);

        String accessToken = authenticateUserWithManageScope(email, testConfig.getTestUserPassword());

        return User.userWith()
            .authorisationToken(accessToken)
            .email(email)
            .build();
    }

    public static void deleteUser(String emailAddress)
    {
        idamApi.deleteUser(emailAddress);
    }

    private CreateUserRequest userRequest(String email, String[] roles) {
        return userRequestWith()
            .email(email)
            .password(testConfig.getTestUserPassword())
            .roles(Stream.of(roles)
                .map(Role::new)
                .collect(toList()))
            .build();
    }

}

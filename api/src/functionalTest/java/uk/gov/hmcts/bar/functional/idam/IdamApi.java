package uk.gov.hmcts.bar.functional.idam;

import com.fasterxml.jackson.annotation.JsonProperty;
import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

public interface IdamApi {

    @RequestLine("POST /testing-support/accounts")
    @Headers("Content-Type: application/json")
    void createUser(CreateUserRequest createUserRequest);

    @RequestLine("DELETE /testing-support/accounts/{email}")
    void deleteUser(@Param("email") String email);

    @RequestLine("POST /o/token")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("username={username}&password={password}&scope={scope}&grant_type="
        + "{grant_type}&client_id={client_id}&client_secret={client_secret}&redirect_uri={redirect_uri}")
    TokenExchangeResponse tokenExchangeResponse(@Param("username") String username,
                                       @Param("password") String password,
                                       @Param("scope") String scope,
                                       @Param("grant_type") String grantType,
                                       @Param("client_id") String clientId,
                                       @Param("client_secret") String clientSecret,
                                       @Param("redirect_uri") String redirectUri);

    @Data
    @AllArgsConstructor
    @Builder(builderMethodName = "userRequestWith")
    class CreateUserRequest {
        private final String email;
        private final String forename = "BarApiFunctional";
        private final String surname = "Tests";
        private final List<Role> roles;
        private final String password;
    }

    @AllArgsConstructor
    @Getter
    class Role {
        private String code;
    }

    @Data
    class TokenExchangeResponse {
        @JsonProperty("access_token")
        private String accessToken;
    }
}

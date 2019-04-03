package uk.gove.hmcts.bar.functional;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@TestPropertySource("classpath:functional-test.properties")
@Slf4j
public abstract class FunctionalTest {

    @Value("${TEST_URL:http://localhost:8080}")
    private String testUrl;

    @Value("${bar.web.url}")
    private String barWebUrl;

    @Value("${proxy.url}")
    private String proxyUrl;

    @Value("${proxy.port}")
    private int proxyPort;

    @Value("${proxy.enabled}")
    private boolean proxyEnabled;

    protected AuthenticatorClient authenticatorClient;

    public static Map<Roles, String> users = new HashMap<>();

    static {
        users.put(Roles.DELIVERY_MANAGER_Y431, "barpreprod@mailinator.com");
        users.put(Roles.SENIOR_FEE_CLERK_Y431, "barpreprodsrfeeclerk@mailinator.com");
        users.put(Roles.FEE_CLERK_Y431, "barpreprodfeeclerk@mailinator.com");
        users.put(Roles.POST_CLERK_Y431, "barpreprodpostclerk@mailinator.com");

        users.put(Roles.SENIOR_FEE_CLERK_Y610, "barsrfeeclerksite2@mailnesia.com");
        users.put(Roles.FEE_CLERK_Y610, "barfeeclerksite2@mailnesia.com");
        users.put(Roles.POST_CLERK_Y610, "barpostsite2@mailnesia.com");
    }

    public static String password = "LevelAt12";

    public enum Sites {
        Y431, Y610
    }

    public enum Roles {
        POST_CLERK_Y431, FEE_CLERK_Y431, SENIOR_FEE_CLERK_Y431, DELIVERY_MANAGER_Y431,
        POST_CLERK_Y610, FEE_CLERK_Y610, SENIOR_FEE_CLERK_Y610, DELIVERY_MANAGER_Y610
    }

    @Before
    public void setup() {
        RestAssured.baseURI = testUrl;
        if (proxyEnabled) {
            RestAssured.proxy(proxyUrl, proxyPort);
        }
        log.info("Bar-Api base url is :{}", testUrl);
        authenticatorClient = new AuthenticatorClient(barWebUrl, proxyEnabled, proxyUrl, proxyPort);
        // assign users to sites
        users.keySet().stream().filter(key -> key.name().contains(Sites.Y431.name()))
            .forEach(role -> addUserToSite(users.get(role), Sites.Y431.name()));
        users.keySet().stream().filter(key -> key.name().contains(Sites.Y610.name()))
            .forEach(role -> addUserToSite(users.get(role), Sites.Y610.name()));
    }

    protected void addUserToSite(String email, String siteId) {
        String adminUser = "barpreprod@mailinator.com";
        String password = "LevelAt12";
        String token = authenticatorClient.authenticate(adminUser, password);
        given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, "application/json")
            .header("Authorization", token)
            .header("SiteId", siteId)
            .when()
            .post("/sites/" + siteId + "/users/" + email);
    }

    protected Response createCardPaymentInstruction(String payload, String token, String siteId) {
        return given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, "application/json")
            .header("Authorization", token)
            .header("SiteId", siteId)
            .body(payload)
            .when()
            .post("/cards");
    }
}

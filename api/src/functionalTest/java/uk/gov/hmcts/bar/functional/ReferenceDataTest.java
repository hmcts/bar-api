package uk.gov.hmcts.bar.functional;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.bar.functional.idam.IdamService;
import uk.gov.hmcts.bar.functional.util.BarTestService;

import java.util.UUID;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@RunWith(SpringIntegrationSerenityRunner.class)
@ContextConfiguration(classes = TestContextConfiguration.class)
@TestPropertySource("classpath:functional-test.properties")
public class ReferenceDataTest {

    @Autowired
    private IdamService idamService;

    private static String DELIVERY_MANGER_USER_EMAIL;
    private static String USER_TOKEN_DELIVERY_MANAGER_Y431;
    private static boolean TOKENS_INITIALIZED = false;

    @Before
    public void setUp() {
        if (!TOKENS_INITIALIZED) {
            DELIVERY_MANGER_USER_EMAIL = String.format("bardeliverymanager-%s@mailtest.gov.uk", UUID.randomUUID().toString());
            USER_TOKEN_DELIVERY_MANAGER_Y431 = idamService.createUserWith(DELIVERY_MANGER_USER_EMAIL, "bar-delivery-manager").getAuthorisationToken();
            // assign user to Y431 site
            BarTestService.addUsersToSite(USER_TOKEN_DELIVERY_MANAGER_Y431, BarTestService.Sites.Y431.name(), DELIVERY_MANGER_USER_EMAIL);
            TOKENS_INITIALIZED = true;
        }
    }

    @Test
    public void shouldReturnPaymentTypes() {
        SerenityRest.given()
            .header(CONTENT_TYPE, "application/json")
            .when()
            .get("/payment-types")
            .then()
            .statusCode(200);
    }

    @Test
    public void testReturnAvailableActions() {
        BarTestService.givenWithAuthHeaders(USER_TOKEN_DELIVERY_MANAGER_Y431, BarTestService.Sites.Y431.name())
            .when()
            .get("/payment-action")
            .then()
            .statusCode(200);
    }

    @AfterClass
    public static void tearDown()
    {
        // remove test users from site
        BarTestService.removeUserFromSite(USER_TOKEN_DELIVERY_MANAGER_Y431, BarTestService.Sites.Y431.name(), DELIVERY_MANGER_USER_EMAIL);

        // delete idam test user
        IdamService.deleteUser(DELIVERY_MANGER_USER_EMAIL);
    }


}

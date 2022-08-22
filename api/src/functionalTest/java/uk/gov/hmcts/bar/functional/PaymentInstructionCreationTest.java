package uk.gov.hmcts.bar.functional;

import io.restassured.response.Response;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.bar.functional.idam.IdamService;
import uk.gov.hmcts.bar.functional.util.BarTestService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RunWith(SpringIntegrationSerenityRunner.class)
@ContextConfiguration(classes = TestContextConfiguration.class)
@TestPropertySource("classpath:functional-test.properties")
public class PaymentInstructionCreationTest {

    @Autowired
    private IdamService idamService;

    private static List<String> users = new ArrayList<>();
    private static String USER_TOKEN_DELIVERY_MANAGER;
    private static String USER_TOKEN_FEE_CLERK_Y431;
    private static String PAYMENT_INSTRUCTION_ID;

    private static boolean TOKENS_INITIALIZED = false;

    @Before
    public void setUp() {
        if (!TOKENS_INITIALIZED) {

            String deliveryMangerUserEmail = String.format("bardeliverymanager-%s@mailtest.gov.uk", UUID.randomUUID().toString());
            USER_TOKEN_DELIVERY_MANAGER = idamService.createUserWith(deliveryMangerUserEmail, "bar-delivery-manager").getAuthorisationToken();

            String feeClerkY431SiteUserEmail = String.format("barfeelclerk1-%s@mailtest.gov.uk", UUID.randomUUID().toString());
            USER_TOKEN_FEE_CLERK_Y431 = idamService.createUserWith(feeClerkY431SiteUserEmail, "bar-fee-clerk").getAuthorisationToken();

            users.add(deliveryMangerUserEmail);
            users.add(feeClerkY431SiteUserEmail);

            // assign users to sites
           BarTestService.addUsersToSite(USER_TOKEN_DELIVERY_MANAGER, BarTestService.Sites.Y431.name(), feeClerkY431SiteUserEmail);

            TOKENS_INITIALIZED = true;
        }
    }

    @Test
    public void testCardPaymentCreationWithCorrectSiteId() throws JSONException {
        JSONObject payload = new JSONObject()
            .put("payer_name", "John Doe")
            .put("amount", 550)
            .put("status", "D")
            .put("currency", "GBP")
            .put("authorization_code", "123456")
            .put("site_id", BarTestService.Sites.Y610.name());

        Response response = BarTestService.createCardPaymentInstruction(payload.toString(), USER_TOKEN_FEE_CLERK_Y431, BarTestService.Sites.Y431.name());
        response.then().statusCode(201);
        PAYMENT_INSTRUCTION_ID = response.path("id").toString();

        Assert.assertEquals(BarTestService.Sites.Y431.name(), response.as(HashMap.class).get("site_id"));
    }

    @Test
    public void testCardPaymentCreationWithInvalidSiteId() throws JSONException {
        JSONObject payload = new JSONObject()
            .put("payer_name", "John Doe")
            .put("amount", 550)
            .put("status", "D")
            .put("currency", "GBP")
            .put("authorization_code", "123456");

        BarTestService.createCardPaymentInstruction(payload.toString(), USER_TOKEN_FEE_CLERK_Y431, BarTestService.Sites.Y610.name()).then()
            .statusCode(403);
    }

    @After
    public void tearDownAfterEachTest()
    {
        if (PAYMENT_INSTRUCTION_ID != null)
        {
            BarTestService.deleteCardPaymentInstruction(USER_TOKEN_FEE_CLERK_Y431, BarTestService.Sites.Y431.name(), PAYMENT_INSTRUCTION_ID);
            PAYMENT_INSTRUCTION_ID = null;
        }
    }

    @AfterClass
    public static void tearDown()
    {

        if (!users.isEmpty()) {
            // remove test users from site
            users.forEach(userEmail -> BarTestService.removeUserFromSite(USER_TOKEN_DELIVERY_MANAGER, BarTestService.Sites.Y431.name(), userEmail));

            // delete idam test user
            users.forEach(IdamService::deleteUser);
        }
    }
}

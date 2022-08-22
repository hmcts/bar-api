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
public class PaymentInstructionUpdateTest {

    @Autowired
    private IdamService idamService;

    private static List<String> users = new ArrayList<>();
    private static String USER_TOKEN_DELIVERY_MANAGER;
    private static String USER_TOKEN_FEE_CLERK_Y431;
    private static String USER_TOKEN_FEE_CLERK_Y610;
    private static String PAYMENT_INSTRUCTION_ID_Y431;
    private static String PAYMENT_INSTRUCTION_ID_Y610;
    private static String CASE_FEE_ID_Y610;
    private static String CASE_FEE_ID_Y431;

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

            // assign user to Y431 site
            BarTestService.addUsersToSite(USER_TOKEN_DELIVERY_MANAGER, BarTestService.Sites.Y431.name(), feeClerkY431SiteUserEmail);

            String feeClerkY610SiteUserEmail = String.format("barfeelclerk2-%s@mailtest.gov.uk", UUID.randomUUID().toString());
            USER_TOKEN_FEE_CLERK_Y610 = idamService.createUserWith(feeClerkY610SiteUserEmail, "bar-fee-clerk").getAuthorisationToken();
            users.add(feeClerkY610SiteUserEmail);
            // assign user to Y610 site
            BarTestService.addUsersToSite(USER_TOKEN_DELIVERY_MANAGER, BarTestService.Sites.Y610.name(), feeClerkY610SiteUserEmail);

            TOKENS_INITIALIZED = true;
        }
    }

    @Test
    public void updatePaymentInstructionWithValidSiteId() throws JSONException {
        JSONObject payload = new JSONObject()
            .put("payer_name", "John Doe")
            .put("amount", 550)
            .put("status", "D")
            .put("currency", "GBP")
            .put("authorization_code", "123456");


        HashMap createdPayment = BarTestService.createCardPaymentInstruction(payload.toString(), USER_TOKEN_FEE_CLERK_Y431, BarTestService.Sites.Y431.name()).getBody().as(HashMap.class);
        PAYMENT_INSTRUCTION_ID_Y431 = createdPayment.get("id").toString();

        payload = new JSONObject()
            .put("payer_name", "Jane Doe");

        BarTestService.updateCardPaymentInstruction(payload.toString(), USER_TOKEN_FEE_CLERK_Y431, BarTestService.Sites.Y431.name(), createdPayment.get("id").toString())
            .then()
            .statusCode(200);
    }

    @Test
    public void updatePaymentInstructionWithInvalidSiteId() throws JSONException {
        JSONObject payload = new JSONObject()
            .put("payer_name", "John Doe")
            .put("amount", 550)
            .put("status", "D")
            .put("currency", "GBP")
            .put("authorization_code", "123456");

        HashMap createdPayment = BarTestService.createCardPaymentInstruction(payload.toString(), USER_TOKEN_FEE_CLERK_Y431, BarTestService.Sites.Y431.name()).getBody().as(HashMap.class);
        PAYMENT_INSTRUCTION_ID_Y431 = createdPayment.get("id").toString();

        payload = new JSONObject()
            .put("payer_name", "Jane Doe")
            .put("status", "P");

        BarTestService.updateCardPaymentInstruction(payload.toString(), USER_TOKEN_FEE_CLERK_Y431, BarTestService.Sites.Y610.name(), createdPayment.get("id").toString())
            .then()
            .statusCode(403);
    }

    @Test
    public void updatePaymentInstructionWithDifferentSiteId() throws JSONException {
        JSONObject payload = new JSONObject()
            .put("payer_name", "John Doe Milton")
            .put("amount", 550)
            .put("status", "D")
            .put("currency", "GBP")
            .put("authorization_code", "123456");

        HashMap createdPayment = BarTestService.createCardPaymentInstruction(payload.toString(), USER_TOKEN_FEE_CLERK_Y610, BarTestService.Sites.Y610.name()).getBody().as(HashMap.class);
        PAYMENT_INSTRUCTION_ID_Y610 = createdPayment.get("id").toString();

        payload = new JSONObject()
            .put("payer_name", "Jane Doe")
            .put("status", "P");

        Response response = BarTestService.updateCardPaymentInstruction(payload.toString(), USER_TOKEN_FEE_CLERK_Y431, BarTestService.Sites.Y431.name(), createdPayment.get("id").toString());
        response.then().statusCode(404);
        Assert.assertEquals("{\"message\":\"payment instruction on site " + BarTestService.Sites.Y431.name() + " for id=" +
            createdPayment.get("id") + " was not found\"}", response.getBody().print());
    }

    @Test
    public void submitPaymentInstructionWithValidSiteId() throws JSONException {
        // Create payment
        JSONObject payload = new JSONObject()
            .put("payer_name", "John Doe")
            .put("amount", 550)
            .put("status", "D")
            .put("currency", "GBP")
            .put("authorization_code", "123456");

        HashMap createdPayment = BarTestService.createCardPaymentInstruction(payload.toString(), USER_TOKEN_FEE_CLERK_Y431, BarTestService.Sites.Y431.name()).getBody().as(HashMap.class);
        PAYMENT_INSTRUCTION_ID_Y431 = createdPayment.get("id").toString();

        // Add fee
        Response response = BarTestService.addCaseFee(550, USER_TOKEN_FEE_CLERK_Y431, createdPayment.get("id").toString(), BarTestService.Sites.Y431.name());
        response.then().statusCode(201);
        CASE_FEE_ID_Y431 = response.path("case_fee_id").toString();

        // Make changes
        payload = new JSONObject()
            .put("status", "V")
            .put("action", "Process");

        BarTestService.submitPaymentInstruction(payload.toString(), USER_TOKEN_FEE_CLERK_Y431, BarTestService.Sites.Y431.name(), createdPayment.get("id").toString())
            .then()
            .statusCode(200);
    }

    @Test
    public void submitPaymentInstructionWithDifferentSiteId() throws JSONException {
        // Create payment
        JSONObject payload = new JSONObject()
            .put("payer_name", "John Doe")
            .put("amount", 550)
            .put("status", "D")
            .put("currency", "GBP")
            .put("authorization_code", "123456");

        HashMap createdPayment = BarTestService.createCardPaymentInstruction(payload.toString(), USER_TOKEN_FEE_CLERK_Y610, BarTestService.Sites.Y610.name()).getBody().as(HashMap.class);
        PAYMENT_INSTRUCTION_ID_Y610 = createdPayment.get("id").toString();

        // Add fee
        Response response = BarTestService.addCaseFee(550, USER_TOKEN_FEE_CLERK_Y610, createdPayment.get("id").toString(), BarTestService.Sites.Y610.name());
        response.then().statusCode(201);
        CASE_FEE_ID_Y610 = response.path("case_fee_id").toString();

        // Make changes
        payload = new JSONObject()
            .put("status", "V")
            .put("action", "Process");

        BarTestService.submitPaymentInstruction(payload.toString(), USER_TOKEN_FEE_CLERK_Y610, BarTestService.Sites.Y431.name(), createdPayment.get("id").toString())
            .then()
            .statusCode(403);
    }

    @After
    public void tearDownAfterEachTest() {
        if (CASE_FEE_ID_Y431 != null) {
            BarTestService.deleteCaseFeeDetails(USER_TOKEN_FEE_CLERK_Y431, BarTestService.Sites.Y431.name(), CASE_FEE_ID_Y431);
            CASE_FEE_ID_Y431 = null;
        }

        if (PAYMENT_INSTRUCTION_ID_Y431 != null) {
            BarTestService.deleteCardPaymentInstruction(USER_TOKEN_FEE_CLERK_Y431, BarTestService.Sites.Y431.name(), PAYMENT_INSTRUCTION_ID_Y431);
            PAYMENT_INSTRUCTION_ID_Y431 = null;
        }

        if (CASE_FEE_ID_Y610 != null) {
            BarTestService.deleteCaseFeeDetails(USER_TOKEN_FEE_CLERK_Y610, BarTestService.Sites.Y610.name(), CASE_FEE_ID_Y610);
            CASE_FEE_ID_Y610 = null;
        }

        if (PAYMENT_INSTRUCTION_ID_Y610 != null) {
            BarTestService.deleteCardPaymentInstruction(USER_TOKEN_FEE_CLERK_Y610, BarTestService.Sites.Y610.name(), PAYMENT_INSTRUCTION_ID_Y610);
            PAYMENT_INSTRUCTION_ID_Y610 = null;
        }
    }

    @AfterClass
    public static void tearDown()
    {
        // remove test users from Y431 site
        users.stream().filter(userEmail -> userEmail.contains(BarTestService.Sites.Y431.name())).forEach(userEmail -> BarTestService.removeUserFromSite(USER_TOKEN_DELIVERY_MANAGER, BarTestService.Sites.Y431.name(), userEmail));

        // remove test users from Y610 site
        users.stream().filter(userEmail -> userEmail.contains(BarTestService.Sites.Y610.name())).forEach(userEmail -> BarTestService.removeUserFromSite(USER_TOKEN_DELIVERY_MANAGER, BarTestService.Sites.Y610.name(), userEmail));

        // delete idam test user
        users.forEach(IdamService::deleteUser);
    }
}

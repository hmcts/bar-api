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

import java.util.*;

@RunWith(SpringIntegrationSerenityRunner.class)
@ContextConfiguration(classes = TestContextConfiguration.class)
@TestPropertySource("classpath:functional-test.properties")
public class CaseFeeDetailTest {

    @Autowired
    private IdamService idamService;

    private static List<String> users = new ArrayList<>();
    private static String USER_TOKEN_DELIVERY_MANAGER;
    private static String USER_TOKEN_FEE_CLERK_Y431;
    private static String CASE_FEE_ID;
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

            // assign user to site
            BarTestService.addUsersToSite(USER_TOKEN_DELIVERY_MANAGER, BarTestService.Sites.Y431.name(), feeClerkY431SiteUserEmail);

            TOKENS_INITIALIZED = true;
        }
    }

    @Test
    public void createCaseFeeDetailWithValidSiteId() throws JSONException {
        JSONObject paymentInstructionPayload = new JSONObject()
            .put("payer_name", "John Doe")
            .put("amount", 550)
            .put("status", "D")
            .put("currency", "GBP")
            .put("authorization_code", "123456");

        HashMap createdPayment = BarTestService.createCardPaymentInstruction(paymentInstructionPayload.toString(), USER_TOKEN_FEE_CLERK_Y431, BarTestService.Sites.Y431.name()).getBody().as(HashMap.class);
        PAYMENT_INSTRUCTION_ID = createdPayment.get("id").toString();

        JSONObject caseFeeDetailPayLoad = new JSONObject()
            .put("payment_instruction_id", PAYMENT_INSTRUCTION_ID)
            .put("amount", 550)
            .put("fee_code", "FEE00007")
            .put("fee_description", "description")
            .put("fee_version", "3")
            .put("case_reference", "case1");

        Response response = BarTestService.createCaseFeeForPaymentInstruction(caseFeeDetailPayLoad.toString(), USER_TOKEN_FEE_CLERK_Y431, BarTestService.Sites.Y431.name());
        response.then().statusCode(201);
        CASE_FEE_ID = response.path("case_fee_id").toString();
    }

    @Test
    public void createCaseFeeDetailWithInvalidSiteId() throws JSONException {

        JSONObject paymentInstructionPayload = new JSONObject()
            .put("payer_name", "John Doe")
            .put("amount", 550)
            .put("status", "D")
            .put("currency", "GBP")
            .put("authorization_code", "123456");

        HashMap createdPayment = BarTestService.createCardPaymentInstruction(paymentInstructionPayload.toString(), USER_TOKEN_FEE_CLERK_Y431, BarTestService.Sites.Y431.name()).getBody().as(HashMap.class);
        PAYMENT_INSTRUCTION_ID = createdPayment.get("id").toString();

        JSONObject caseFeeDetailPayLoad = new JSONObject()
            .put("payment_instruction_id", PAYMENT_INSTRUCTION_ID)
            .put("amount", 550)
            .put("fee_code", "FEE00007")
            .put("fee_description", "description")
            .put("fee_version", "3")
            .put("case_reference", "case1");

        BarTestService.createCaseFeeForPaymentInstruction(caseFeeDetailPayLoad.toString(), USER_TOKEN_FEE_CLERK_Y431, BarTestService.Sites.Y610.name())
            .then()
            .statusCode(403);
    }


    @Test
    public void updateCaseFeeDetailWithValidSiteId() throws JSONException {
        JSONObject paymentInstructionPayload = new JSONObject()
            .put("payer_name", "John Doe")
            .put("amount", 550)
            .put("status", "D")
            .put("currency", "GBP")
            .put("authorization_code", "123456");

        HashMap createdPayment = BarTestService.createCardPaymentInstruction(paymentInstructionPayload.toString(), USER_TOKEN_FEE_CLERK_Y431, BarTestService.Sites.Y431.name()).getBody().as(HashMap.class);
        PAYMENT_INSTRUCTION_ID = createdPayment.get("id").toString();

        JSONObject caseFeeDetailPayLoad = new JSONObject()
            .put("payment_instruction_id", PAYMENT_INSTRUCTION_ID)
            .put("amount", 550)
            .put("fee_code", "FEE00007")
            .put("fee_description", "description")
            .put("fee_version", "3")
            .put("case_reference", "case1");

        HashMap response = BarTestService.createCaseFeeForPaymentInstruction(caseFeeDetailPayLoad.toString(), USER_TOKEN_FEE_CLERK_Y431, BarTestService.Sites.Y431.name())
            .getBody().as(HashMap.class);
        CASE_FEE_ID = response.get("case_fee_id").toString();

        BarTestService.updateCaseFeeDetails(caseFeeDetailPayLoad.toString(), USER_TOKEN_FEE_CLERK_Y431, BarTestService.Sites.Y431.name(), CASE_FEE_ID)
            .then()
            .statusCode(200);
    }


    @Test
    public void updateCaseFeeDetailWithInvalidSiteId() throws JSONException {

        JSONObject paymentInstructionPayload = new JSONObject()
            .put("payer_name", "John Doe")
            .put("amount", 550)
            .put("status", "D")
            .put("currency", "GBP")
            .put("authorization_code", "123456");

        HashMap createdPayment = BarTestService.createCardPaymentInstruction(paymentInstructionPayload.toString(), USER_TOKEN_FEE_CLERK_Y431, BarTestService.Sites.Y431.name()).getBody().as(HashMap.class);
        PAYMENT_INSTRUCTION_ID = createdPayment.get("id").toString();

        JSONObject caseFeeDetailPayLoad = new JSONObject()
            .put("payment_instruction_id", PAYMENT_INSTRUCTION_ID)
            .put("amount", 550)
            .put("fee_code", "FEE00007")
            .put("fee_description", "description")
            .put("fee_version", "3")
            .put("case_reference", "case1");

        HashMap response = BarTestService.createCaseFeeForPaymentInstruction(caseFeeDetailPayLoad.toString(), USER_TOKEN_FEE_CLERK_Y431, BarTestService.Sites.Y431.name())
            .getBody().as(HashMap.class);
        CASE_FEE_ID = response.get("case_fee_id").toString();

        BarTestService.updateCaseFeeDetails(caseFeeDetailPayLoad.toString(), USER_TOKEN_FEE_CLERK_Y431, BarTestService.Sites.Y610.name(), response.get("case_fee_id").toString())
            .then()
            .statusCode(403);
    }

    @After
    public void tearDownAfterEachTest() {
        if (CASE_FEE_ID != null) {
            BarTestService.deleteCaseFeeDetails(USER_TOKEN_FEE_CLERK_Y431, BarTestService.Sites.Y431.name(), CASE_FEE_ID);
            CASE_FEE_ID = null;
        }

        if (PAYMENT_INSTRUCTION_ID != null) {
            BarTestService.deleteCardPaymentInstruction(USER_TOKEN_FEE_CLERK_Y431, BarTestService.Sites.Y431.name(), PAYMENT_INSTRUCTION_ID);
            PAYMENT_INSTRUCTION_ID = null;
        }
    }

    @AfterClass
    public static void tearDown() {
        if (!users.isEmpty()) {
            // remove test users from site
            users.forEach(userEmail -> BarTestService.removeUserFromSite(USER_TOKEN_DELIVERY_MANAGER, BarTestService.Sites.Y431.name(), userEmail));

            // delete idam test user
            users.forEach(IdamService::deleteUser);
        }
    }
}

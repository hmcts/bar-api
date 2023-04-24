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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringIntegrationSerenityRunner.class)
@ContextConfiguration(classes = TestContextConfiguration.class)
@TestPropertySource("classpath:functional-test.properties")
public class SendToPayhubTest {

    @Autowired
    private IdamService idamService;

    private static String DELIVERY_MANGER_USER_EMAIL;
    private static String USER_TOKEN_DELIVERY_MANAGER_Y431;
    private static String CASE_FEE_ID;

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
    @Ignore("Ignoring Test: PAY-5982")
    public void testSendPaymentToPayhub() throws JSONException {

        // create payment
        Map createdPayment = createPayment(USER_TOKEN_DELIVERY_MANAGER_Y431);

        // attach fee
        Map createdFee = createFee(createdPayment, USER_TOKEN_DELIVERY_MANAGER_Y431);
        CASE_FEE_ID = createdFee.get("case_fee_id").toString();

        // set to TTB
        changeStatus("/cards/", "TTB", USER_TOKEN_DELIVERY_MANAGER_Y431, createdPayment);

        // send to payhub
        Response response = BarTestService.sendAllPaymentInstructionsWithTTBStatusToPayHub(USER_TOKEN_DELIVERY_MANAGER_Y431, BarTestService.Sites.Y431.name());

        response.then().statusCode(200);
        Map resp = response.as(HashMap.class);
        Assert.assertEquals(1, resp.get("success"));
    }

    public void testSendFullRemissionToPayhub() throws JSONException {
        // create full-remission
        Map createdFullRemission = createFullRemission(USER_TOKEN_DELIVERY_MANAGER_Y431);

        // attach fee
        Map createdFee =  createFee(createdFullRemission, USER_TOKEN_DELIVERY_MANAGER_Y431);

        // set to TTB
        changeStatus("/remissions/", "TTB", USER_TOKEN_DELIVERY_MANAGER_Y431, createdFullRemission);

        // send to payhub
        Response response = BarTestService.sendAllPaymentInstructionsWithTTBStatusToPayHub(USER_TOKEN_DELIVERY_MANAGER_Y431, BarTestService.Sites.Y431.name());

        response.then().statusCode(200);
        Map resp = response.as(HashMap.class);
        Assert.assertEquals(1, resp.get("success"));
    }

    private Map createPayment(String token) throws JSONException {
        JSONObject payload = new JSONObject()
            .put("payer_name", "John Doe")
            .put("amount", 55000)
            .put("status", "D")
            .put("currency", "GBP")
            .put("authorization_code", "123456");
        Response response = BarTestService.createCardPaymentInstruction(payload.toString(), token, BarTestService.Sites.Y431.name());
        response.then().statusCode(201);
        return response.as(HashMap.class);
    }

    private Map createFullRemission(String token) throws JSONException {
        JSONObject payload = new JSONObject()
            .put("payer_name", "John Doe")
            .put("status", "D")
            .put("remission_reference", "12345678901");
        Response response = BarTestService.createFullRemission(payload.toString(), token, BarTestService.Sites.Y431.name());
        response.then().statusCode(201);
        return response.as(HashMap.class);
    }

    private Map createFee(Map createdPayment, String token) throws JSONException {
        int amount = (int) createdPayment.get("amount") == 0 ? 55000 : (int) createdPayment.get("amount");
        JSONObject caseFeeDetailPayLoad = new JSONObject()
            .put("payment_instruction_id", createdPayment.get("id"))
            .put("amount", amount)
            .put("fee_code", "FEE00007")
            .put("fee_description", "description")
            .put("fee_version", "3")
            .put("case_reference", "case1");

        Response response = BarTestService.createCaseFeeForPaymentInstruction(caseFeeDetailPayLoad.toString(), token, BarTestService.Sites.Y431.name());
        response.then().statusCode(201);
        return response.as(HashMap.class);

    }

    private void changeStatus(String endpoint, String status, String token, Map createdPayment) throws JSONException {
        JSONObject payload = new JSONObject()
            .put("status", status);

        BarTestService.givenWithAuthHeaders(token, BarTestService.Sites.Y431.name())
            .body(payload.toString())
            .when()
            .put(endpoint + createdPayment.get("id"));
    }


    @After
    public void tearDownAfterEachTest() {
        if (CASE_FEE_ID != null)
        {
            BarTestService.deleteCaseFeeDetails(USER_TOKEN_DELIVERY_MANAGER_Y431, BarTestService.Sites.Y431.name(), CASE_FEE_ID);
            CASE_FEE_ID = null;
        }
    }

    @AfterClass
    public static void tearDown() {
        // remove test users from site
        BarTestService.removeUserFromSite(USER_TOKEN_DELIVERY_MANAGER_Y431, BarTestService.Sites.Y431.name(), DELIVERY_MANGER_USER_EMAIL);

        // delete idam test user
        IdamService.deleteUser(DELIVERY_MANGER_USER_EMAIL);
    }
}

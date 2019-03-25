package uk.gove.hmcts.bar.functional;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.given;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@RunWith(SpringRunner.class)
public class PaymentInstructionCreationTest extends FunctionalTest {

    @Test
    public void testCardPaymentCreationWithCorrectSiteId() throws JSONException {
        JSONObject payload = new JSONObject()
            .put("payer_name", "John Doe")
            .put("amount", 550)
            .put("status", "D")
            .put("currency", "GBP")
            .put("authorization_code", "123456");
        String token = authenticatorClient.authenticate(users.get(Roles.FEE_CLERK), password);
        given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, "application/json")
            .header("Authorization", token)
            .header("SiteId", Sites.Y431.name())
            .body(payload.toString())
            .when()
            .post("/cards")
            .then()
            .statusCode(201);
    }

    @Test
    public void testCardPaymentCreationWithInvalidSiteId() throws JSONException {
        JSONObject payload = new JSONObject()
            .put("payer_name", "John Doe")
            .put("amount", 550)
            .put("status", "D")
            .put("currency", "GBP")
            .put("authorization_code", "123456");
        String token = authenticatorClient.authenticate(users.get(Roles.FEE_CLERK), password);
        given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, "application/json")
            .header("Authorization", token)
            .header("SiteId", Sites.Y610.name())
            .body(payload.toString())
            .when()
            .post("/cards")
            .then()
            .statusCode(403);
    }
}

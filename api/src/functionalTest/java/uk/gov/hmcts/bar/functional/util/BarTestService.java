package uk.gov.hmcts.bar.functional.util;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.rest.SerenityRest;
import org.json.JSONException;
import org.json.JSONObject;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

public class BarTestService {

    public enum Sites {
        Y431, Y610
    }

    public static void addUsersToSite(String deliveryManagerToken, String siteId, String userEmail) {
        givenWithAuthHeaders(deliveryManagerToken, siteId)
            .when()
            .post("/sites/" + siteId + "/users/" + userEmail);
    }

    public static void removeUserFromSite(String deliveryManagerToken, String siteId, String userEmail) {
        givenWithAuthHeaders(deliveryManagerToken, siteId)
            .when()
            .delete("/sites/" + siteId + "/users/" + userEmail)
            .then()
            .statusCode(200);
    }

    public static Response createCardPaymentInstruction(String payload, String token, String siteId) {
        return givenWithAuthHeaders(token, siteId)
            .body(payload)
            .when()
            .post("/cards");
    }

    public static Response updateCardPaymentInstruction(String payload, String token, String siteId, String paymentId) {
        return givenWithAuthHeaders(token, siteId)
            .body(payload)
            .when()
            .put("/cards/{id}", paymentId);

    }

    public static void deleteCardPaymentInstruction(String token, String siteId, String paymentId) {
        givenWithAuthHeaders(token, siteId)
            .when()
            .delete("/payment-instructions/{id}", paymentId)
            .then()
            .statusCode(204);
    }

    public static Response addCaseFee(int amount, String token, String paymentId, String siteId) throws JSONException {
        JSONObject payload = new JSONObject()
            .put("payment_instruction_id", paymentId)
            .put("case_reference", "123asd")
            .put("fee_code", "code01")
            .put("amount", amount)
            .put("fee_description", "dummmy fee")
            .put("fee_version", "v1");

        return givenWithAuthHeaders(token, siteId)
            .body(payload.toString())
            .when()
            .post("/fees");
    }

    public static Response createFullRemission(String payload, String token, String siteId) {
        return givenWithAuthHeaders(token, siteId)
            .body(payload)
            .when()
            .post("/remissions");
    }

    public static Response createCaseFeeForPaymentInstruction(String payload, String token, String siteId) {
        return givenWithAuthHeaders(token, siteId)
            .body(payload)
            .when()
            .post("/fees");
    }

    public static void deleteCaseFeeDetails(String token, String siteId, String caseFeeId) {
        givenWithAuthHeaders(token, siteId)
            .when()
            .delete("/fees/{case-fee-id}", caseFeeId)
            .then()
            .statusCode(200);
    }

    public static Response updateCaseFeeDetails(String payload, String token, String siteId, String caseFeeId) {
        return givenWithAuthHeaders(token, siteId)
            .body(payload)
            .when()
            .put("/fees/{case-fee-id}", caseFeeId);
    }

    public static Response submitPaymentInstruction(String payload, String token, String siteId, String paymentId) {
        return givenWithAuthHeaders(token, siteId)
            .body(payload)
            .when()
            .put("/payment-instructions/{id}", paymentId);
    }

    public static Response sendAllPaymentInstructionsWithTTBStatusToPayHub(String token, String siteId) {
        return givenWithAuthHeaders(token, siteId)
            .when()
            .get("/payment-instructions/send-to-payhub");
    }

    public static RequestSpecification givenWithAuthHeaders(final String userToken, final String siteId) {
        return SerenityRest.given()
            .header(AUTHORIZATION, userToken)
            .header("SiteId", siteId)
            .header(CONTENT_TYPE, "application/json");
    }
}

package uk.gove.hmcts.bar.functional;

import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.given;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@RunWith(SpringRunner.class)
@Slf4j
public class FunctionalTest {

    @Value("${TEST_URL:http://localhost:8080}")
    private String testUrl;

    @Before
    public void setup() {
        RestAssured.baseURI = testUrl;
        log.info("Bar-Api base url is :{}", testUrl);
    }

    @Test
    public void shouldReturnPaymentTypes() {
        given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, "application/json")
            .when()
            .get("/payment-types")
            .then()
            .statusCode(200);
    }
}

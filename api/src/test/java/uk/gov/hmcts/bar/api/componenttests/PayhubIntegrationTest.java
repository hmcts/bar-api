package uk.gov.hmcts.bar.api.componenttests;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import uk.gov.hmcts.bar.api.componenttests.utils.DbTestUtil;

import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PayhubIntegrationTest extends ComponentTestBase {

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule( options().port(23443).notifier(new ConsoleNotifier(true)));

    @Override
    @Before
    public void setUp() throws SQLException {
        super.setUp();
        wireMockRule.stubFor(post(urlPathMatching("/lease"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                .withBody("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjbWMiLCJleHAiOjE1MzMyMzc3NjN9.3iwg2cCa1_G9-TAMupqsQsIVBMWg9ORGir5xZyPhDabk09Ldk0-oQgDQq735TjDQzPI8AxL1PgjtOPDKeKyxfg[akiss@reformMgmtDevBastion02")
            )
        );

        wireMockRule.stubFor(post(urlPathMatching("/payment-records"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                .withBody("OK")
            )
        );
    }

    @Test
    public void testSendPaymentInstrucitonToPayhub() throws Exception {
        DbTestUtil.insertPaymentInstructions(getWebApplicationContext());
        restActionsForDM
            .get("/payment-instructions/send-to-payhub")
            .andExpect(status().isOk())
            .andExpect(body().as(Map.class, map -> {
                Assert.assertEquals(2, map.get("total"));
                Assert.assertEquals(2, map.get("success"));
            }));
    }

    @Test
    public void testSendPaymentInstrucitonToPayhub_withWrongUser() throws Exception {
        DbTestUtil.insertPaymentInstructions(getWebApplicationContext());
        restActions
            .get("/payment-instructions/send-to-payhub")
            .andExpect(status().isForbidden());
    }
}

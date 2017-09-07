package uk.gov.hmcts.bar.api.client;


import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Rule;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class BarClientTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    private BarClient client;

    @Before
    public void setUp() throws Exception {
        client = new BarClient(HttpClients.createMinimal(), "http://localhost:" + wireMockRule.port());
    }
}

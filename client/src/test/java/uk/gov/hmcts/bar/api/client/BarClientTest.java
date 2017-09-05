package uk.gov.hmcts.bar.api.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class BarClientTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());
    private BarClient client;



    @Before
    public void setUp() throws Exception {

        client = new BarClient(
            HttpClients.createMinimal(),
            "http://localhost:" + wireMockRule.port()
        );
        ObjectMapper mapper = new ObjectMapper();

       

    }


}

package uk.gov.hmcts.bar.api.componenttests;

import com.microsoft.applicationinsights.web.internal.WebRequestTrackingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

public class SwaggerPublisher extends ComponentTestBase {

    WebRequestTrackingFilter filter;

    @Autowired
    private WebApplicationContext webAppContext;

    @Before
    public void setup() {
        filter = new WebRequestTrackingFilter();
        filter.init(new MockFilterConfig()); // using a mock that you construct with init params and all
    }

    @After
    public void tearDown() {
        filter = null;
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    public void generateDocs() throws Exception {
        generateDocsForGroup("payment");
        generateDocsForGroup("refdata");
    }

    private void generateDocsForGroup(String groupName) throws Exception {
        byte[] specs = restActions
            .get("/v3/api-docs?group=" + groupName)
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();

        try (OutputStream outputStream = Files.newOutputStream(Paths.get("/tmp/swagger-specs." + groupName + ".json"))) {
            outputStream.write(specs);
        }
    }
}

package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HelloCrudComponentTest extends ComponentTestBase {

    @Test
    public void retrieveOne() throws Exception {
        restActions
            .get("/hello")
            .andExpect(status().isOk())
            .equals("hello every body");
    }
}

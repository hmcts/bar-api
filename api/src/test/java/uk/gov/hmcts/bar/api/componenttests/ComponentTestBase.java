package uk.gov.hmcts.bar.api.componenttests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.bar.api.componenttests.sugar.CustomResultMatcher;
import uk.gov.hmcts.bar.api.componenttests.sugar.RestActions;
import uk.gov.hmcts.bar.api.componenttests.utils.DbTestUtil;
import uk.gov.hmcts.reform.auth.checker.spring.useronly.UserDetails;

import javax.transaction.Transactional;
import java.sql.SQLException;
import java.util.Collections;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = MOCK)
@ActiveProfiles({"embedded", "idam-backdoor"})
@Transactional
public class ComponentTestBase {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    public final UserDetails userDetails =
        new UserDetails("1234", "abc123", Collections.singletonList("bar-post-clerk"));


    public RestActions restActions;

    @Before
    public void setUp() throws SQLException{
        MockMvc mvc = webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        this.restActions = new RestActions(mvc, objectMapper, userDetails);
        DbTestUtil.emptyTable(webApplicationContext, "case_fee_detail");
        DbTestUtil.emptyTable(webApplicationContext, "payment_instruction");
        DbTestUtil.resetAutoIncrementColumns(webApplicationContext, "payment_instruction");
        DbTestUtil.setTestUser(webApplicationContext, userDetails);

    }

    public CustomResultMatcher body() {
        return new CustomResultMatcher(objectMapper);
    }

    public WebApplicationContext getWebApplicationContext() {
        return webApplicationContext;
    }
}


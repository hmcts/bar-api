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

import javax.transaction.Transactional;
import java.sql.SQLException;

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

    /*@Autowired
    protected UserResolverBackdoor userRequestAuthorizer;
*/
    @Autowired
    private WebApplicationContext webApplicationContext;

    RestActions restActions;

    @Before
    public void setUp() throws SQLException{
        MockMvc mvc = webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        this.restActions = new RestActions(mvc,objectMapper);
        DbTestUtil.resetAutoIncrementColumns(webApplicationContext, "payment_instruction");
    }

    CustomResultMatcher body() {
        return new CustomResultMatcher(objectMapper);
    }
}


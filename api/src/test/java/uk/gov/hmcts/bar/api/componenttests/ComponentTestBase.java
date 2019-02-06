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
import uk.gov.hmcts.bar.api.BarServiceApplication;
import uk.gov.hmcts.bar.api.componenttests.sugar.CustomResultMatcher;
import uk.gov.hmcts.bar.api.componenttests.sugar.RestActions;
import uk.gov.hmcts.bar.api.componenttests.utils.DbTestUtil;
import uk.gov.hmcts.bar.multisite.MultisiteConfiguration;
import uk.gov.hmcts.reform.auth.checker.spring.useronly.UserDetails;

import java.sql.SQLException;
import java.util.Collections;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {BarServiceApplication.class, MultisiteConfiguration.class}, webEnvironment = MOCK)
@ActiveProfiles({"embedded", "idam-backdoor"})
public class ComponentTestBase {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    public final UserDetails userDetails =
        new UserDetails("1234", "abc123", Collections.singletonList("bar-post-clerk"));
    public final UserDetails feeClerkUserDetails =
            new UserDetails("fee-clerk", "abc123", Collections.singletonList("bar-fee-clerk"));
    public final UserDetails srFeeClerkUserDetails =
            new UserDetails("sr-fee-clerk", "abc123", Collections.singletonList("bar-senior-clerk"));
    public final UserDetails dmUserDetails =
            new UserDetails("dm-manager", "abc123", Collections.singletonList("bar-delivery-manager"));
    public final UserDetails adminUserDetails =
        new UserDetails("admin", "abc123", Collections.singletonList("super"));


    public RestActions restActions;
    public RestActions restActionsForFeeClerk;
    public RestActions restActionsForSrFeeClerk;
    public RestActions restActionsForDM;
    public RestActions restActionsForAdmin;

    @Before
    public void setUp() throws SQLException{
        MockMvc mvc = webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        this.restActions = new RestActions(mvc, objectMapper, userDetails);
        this.restActionsForFeeClerk = new RestActions(mvc, objectMapper, feeClerkUserDetails);
        this.restActionsForSrFeeClerk = new RestActions(mvc, objectMapper, srFeeClerkUserDetails);
        this.restActionsForDM = new RestActions(mvc, objectMapper, dmUserDetails);
        this.restActionsForAdmin = new RestActions(mvc, objectMapper, adminUserDetails);
        DbTestUtil.emptyTable(webApplicationContext, "payment_instruction_status");
        DbTestUtil.emptyTable(webApplicationContext, "case_fee_detail");
        DbTestUtil.emptyTable(webApplicationContext, "payment_instruction_status");
        DbTestUtil.emptyTable(webApplicationContext, "bar_user");
        DbTestUtil.emptyTable(webApplicationContext, "payment_instruction_payhub_reference");
        DbTestUtil.emptyTable(webApplicationContext, "payment_instruction");
        DbTestUtil.emptyTable(webApplicationContext, "user_site");
        DbTestUtil.emptyTable(webApplicationContext, "site");
        DbTestUtil.resetAutoIncrementColumns(webApplicationContext, "payment_instruction");
        DbTestUtil.addTestUser(webApplicationContext, userDetails);
        DbTestUtil.addTestUser(webApplicationContext, feeClerkUserDetails);
        DbTestUtil.addTestUser(webApplicationContext, srFeeClerkUserDetails);
        DbTestUtil.addTestUser(webApplicationContext, dmUserDetails);
        DbTestUtil.addTestUser(webApplicationContext, adminUserDetails);
    }


    public CustomResultMatcher body() {
        return new CustomResultMatcher(objectMapper);
    }

    public WebApplicationContext getWebApplicationContext() {
        return webApplicationContext;
    }
}


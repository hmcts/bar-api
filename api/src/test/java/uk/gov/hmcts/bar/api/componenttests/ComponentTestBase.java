package uk.gov.hmcts.bar.api.componenttests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.bar.api.BarServiceApplication;
import uk.gov.hmcts.bar.api.auth.SiteValidationFilter;
import uk.gov.hmcts.bar.api.componenttests.sugar.CustomResultMatcher;
import uk.gov.hmcts.bar.api.componenttests.sugar.RestActions;
import uk.gov.hmcts.bar.api.componenttests.utils.DbTestUtil;
import uk.gov.hmcts.bar.api.data.service.BarUserService;
import uk.gov.hmcts.bar.multisite.MultisiteConfiguration;
import uk.gov.hmcts.reform.auth.checker.spring.useronly.UserDetails;

import javax.ws.rs.core.MediaType;
import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {BarServiceApplication.class, MultisiteConfiguration.class}, webEnvironment = MOCK)
@ActiveProfiles({"embedded", "idam-backdoor"})
public class ComponentTestBase {

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule( options().port(23444).notifier(new ConsoleNotifier(true)));

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private BarUserService barUserService;

    public final UserDetails userDetails =
        new UserDetails("1234", "abc123", Collections.singletonList("bar-post-clerk"));
    public final UserDetails feeClerkUserDetails =
            new UserDetails("fee-clerk", "abc123", Collections.singletonList("bar-fee-clerk"));
    public final UserDetails srFeeClerkUserDetails =
            new UserDetails("sr-fee-clerk", "abc123", Collections.singletonList("bar-senior-clerk"));
    public final UserDetails dmUserDetails =
            new UserDetails("dm-manager", "abc123", Collections.singletonList("bar-delivery-manager"));
    public final UserDetails adminUserDetails =
        new UserDetails("admin-site2", "abc123", Collections.singletonList("super"));
    public final UserDetails postClerkUserDetailsSite2 =
        new UserDetails("post-clerk-site2", "abc123", Collections.singletonList("bar-post-clerk"));
    public final UserDetails feeClerkUserDetailsSite2 =
        new UserDetails("fee-clerk-site2", "abc123", Collections.singletonList("bar-fee-clerk"));
    public final UserDetails srFeeClerkUserDetailsSite2 =
        new UserDetails("sr-fee-clerk-site2", "abc123", Collections.singletonList("bar-senior-clerk"));
    public final UserDetails dmUserUserDetailsSite2 =
        new UserDetails("dm-manager-site2", "abc123", Collections.singletonList("bar-delivery-manager"));
    public final UserDetails adminUserDetailsSite2 =
        new UserDetails("admin-site2", "abc123", Collections.singletonList("super"));


    public RestActions restActions;
    public RestActions restActionsForFeeClerk;
    public RestActions restActionsForSrFeeClerk;
    public RestActions restActionsForDM;
    public RestActions restActionsForAdmin;
    public RestActions restActionsPostClerkSite2;
    public RestActions restActionsForFeeClerkSite2;
    public RestActions restActionsForSrFeeClerkSite2;
    public RestActions restActionsForDMSite2;
    public RestActions restActionsForAdminSite2;

    @Before
    public void setUp() throws Exception {
        DefaultMockMvcBuilder mvc = webAppContextSetup(webApplicationContext).apply(springSecurity());
        this.restActions = new RestActions(mvc.addFilter(new SiteValidationFilter(barUserService)).build(), objectMapper, userDetails);
        this.restActionsForFeeClerk = new RestActions(mvc.addFilter(new SiteValidationFilter(barUserService)).build(), objectMapper, feeClerkUserDetails);
        this.restActionsForSrFeeClerk = new RestActions(mvc.addFilter(new SiteValidationFilter(barUserService)).build(), objectMapper, srFeeClerkUserDetails);
        this.restActionsForDM = new RestActions(mvc.addFilter(new SiteValidationFilter(barUserService)).build(), objectMapper, dmUserDetails);
        this.restActionsForAdmin = new RestActions(mvc.addFilter(new SiteValidationFilter(barUserService)).build(), objectMapper, adminUserDetails);
        this.restActionsPostClerkSite2 = new RestActions(mvc.addFilter(new SiteValidationFilter(barUserService)).build(), objectMapper, postClerkUserDetailsSite2);
        this.restActionsForFeeClerkSite2 = new RestActions(mvc.addFilter(new SiteValidationFilter(barUserService)).build(), objectMapper, feeClerkUserDetailsSite2);
        this.restActionsForSrFeeClerkSite2 = new RestActions(mvc.addFilter(new SiteValidationFilter(barUserService)).build(), objectMapper, srFeeClerkUserDetailsSite2);
        this.restActionsForDMSite2 = new RestActions(mvc.addFilter(new SiteValidationFilter(barUserService)).build(), objectMapper, dmUserUserDetailsSite2);
        this.restActionsForAdminSite2 = new RestActions(mvc.addFilter(new SiteValidationFilter(barUserService)).build(), objectMapper, adminUserDetailsSite2);

        DbTestUtil.emptyTable(webApplicationContext, "payment_instruction_status");
        DbTestUtil.emptyTable(webApplicationContext, "case_fee_detail");
        DbTestUtil.emptyTable(webApplicationContext, "payment_instruction_status");
        DbTestUtil.emptyTable(webApplicationContext, "bar_user");
        DbTestUtil.emptyTable(webApplicationContext, "payment_instruction_payhub_reference");
        DbTestUtil.emptyTable(webApplicationContext, "payment_instruction_status");
        DbTestUtil.emptyTable(webApplicationContext, "payment_instruction");
        DbTestUtil.emptyTable(webApplicationContext, "user_site");
        DbTestUtil.emptyTable(webApplicationContext, "site");
        DbTestUtil.resetAutoIncrementColumns(webApplicationContext, "payment_instruction");
        DbTestUtil.addTestUser(webApplicationContext, userDetails);
        DbTestUtil.addTestUser(webApplicationContext, feeClerkUserDetails);
        DbTestUtil.addTestUser(webApplicationContext, srFeeClerkUserDetails);
        DbTestUtil.addTestUser(webApplicationContext, dmUserDetails);
        DbTestUtil.addTestUser(webApplicationContext, adminUserDetails);

        wireMockRule.stubFor(get(urlPathMatching("/sites/(.+)/users/(.+)"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.TEXT_PLAIN)
                .withBody("true")
            )
        );
    }


    public CustomResultMatcher body() {
        return new CustomResultMatcher(objectMapper);
    }

    public WebApplicationContext getWebApplicationContext() {
        return webApplicationContext;
    }

    public ObjectMapper getObjectMapper(){
        return objectMapper;
    }

}


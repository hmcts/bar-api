package uk.gov.hmcts.bar.api.auth;

import uk.gov.hmcts.bar.api.data.model.BarUser;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class MockSiteIdValidationFilter implements Filter {

    private BarUser barUser;

    public MockSiteIdValidationFilter(BarUserDetails barUserDetails) {
        this.barUser = BarUser.createBarUserFromUserDetails(barUserDetails);
        barUser.setSelectedSiteId("Y431");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new BarWrappedHttpRequest((HttpServletRequest) request, barUser), response);
    }
}

package uk.gov.hmcts.bar.api.auth;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.bar.api.data.model.BarUser;
import uk.gov.hmcts.bar.api.data.service.BarUserService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class SiteValidationFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @Mock
    private BarUserService barUserService;

    @Mock
    private BarUser barUser;

    private SiteValidationFilter filter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        filter = new SiteValidationFilter(barUserService);
    }

    @Test
    public void testFilterWhenTheUrlExcluded() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/health");
        filter.doFilter(request, response, chain);
        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    public void testFilterWhenTheUrlShouldBeFilteredHappyPath() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/some/path/to/filter");
        when(request.getHeader("Authorization")).thenReturn("token");
        when(request.getHeader("SiteId")).thenReturn("siteId");
        when(barUserService.getBarUser()).thenReturn(Optional.of(barUser));
        when(barUserService.validateUserAgainstSite(anyString(), anyString(), anyString())).thenReturn(true);
        when(barUser.getEmail()).thenReturn("user@mail.com");

        filter.doFilter(request, response, chain);
        verify(chain, times(1)).doFilter(any(BarWrappedHttpRequest.class), eq(response));
    }

    @Test
    public void testFilterWhenTheUrlShouldBeFilteredButValidationFailed() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/some/path/to/filter");
        when(request.getHeader("Authorization")).thenReturn("token");
        when(request.getHeader("SiteId")).thenReturn("siteId");
        when(barUserService.getBarUser()).thenReturn(Optional.of(barUser));
        when(barUserService.validateUserAgainstSite(anyString(), anyString(), anyString())).thenReturn(false);
        when(barUser.getEmail()).thenReturn("user@mail.com");

        filter.doFilter(request, response, chain);
        verify(response, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN, "Failed to validate user against the given site");
    }
}

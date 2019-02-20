package uk.gov.hmcts.bar.api.data.service;

import org.apache.http.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.HttpParams;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import uk.gov.hmcts.bar.api.auth.BarUserDetails;
import uk.gov.hmcts.bar.api.data.model.BarUser;
import uk.gov.hmcts.bar.api.data.repository.BarUserRepository;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BarUserServiceTest {

    @InjectMocks
    private BarUserService barUserService;

    @Mock
    private BarUserRepository barUserRepository;

    @Mock
    private CloseableHttpClient httpClient;

    private SecurityContext securityContext;
    private BarUser barUser;

    private String siteApi = "http://localhost:23444";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        barUserService = new BarUserService(barUserRepository, httpClient, siteApi);

        Authentication authentication = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return new BarUserDetails("username", "token", new HashSet<>(Arrays.asList("bar-super-user")),
                    "super", "user", "super.user@mail.com");
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return null;
            }
        };

        securityContext = new SecurityContext() {
            @Override
            public Authentication getAuthentication() {
                return authentication;
            }

            @Override
            public void setAuthentication(Authentication authentication) {

            }
        };

        barUser = new BarUser("user1", Collections.emptySet(), "user1@mail.com", "user", "one");
    }

    @Test
    public void whenSecurityContextIsInvalid_shouldReceiveNullAsId() {
        SecurityContextHolder.setContext(new SecurityContextImpl());

        assertNull(barUserService.getCurrentUserId());
    }

    @Test
    public void whenSecurityContextIsValid_shouldGetUserId(){
        SecurityContextHolder.setContext(securityContext);

        assertEquals("username", barUserService.getCurrentUserId());
    }

    @Test
    public void whenUserAlreadyInTheDb_shouldNotCallSave() {
        when(barUserRepository.findBarUserById(Mockito.anyString())).thenReturn(Optional.of(barUser));
        barUserService.saveUser(barUser);
        verify(barUserRepository, times(1)).findBarUserById(anyString());
        verify(barUserRepository, times(0)).save(any(BarUser.class));
    }

    @Test
    public void whenUserIsNotInTheDb_shouldNotCallSave() {
        when(barUserRepository.findBarUserById(Mockito.anyString())).thenReturn(Optional.empty());
        barUserService.saveUser(barUser);
        verify(barUserRepository, times(1)).findBarUserById(anyString());
        verify(barUserRepository, times(1)).save(any(BarUser.class));
    }

    @Test
    public void testValidateUserAgainstSiteWhenValid() throws IOException {
        String email = "user@gmail.com";
        String token = "this_is_a_user_token";
        String siteId = "siteId";
        when(httpClient.execute(any(HttpGet.class))).thenAnswer(invocation -> new SiteHttpResponse(200, "true"));
        assertTrue(barUserService.validateUserAgainstSite(email, token, siteId));
    }

    @Test
    public void testValidateUserAgainstSiteWhenInValid() throws IOException {
        String email = "user@gmail.com";
        String token = "this_is_a_user_token";
        String siteId = "siteId";
        when(httpClient.execute(any(HttpGet.class))).thenAnswer(invocation -> new SiteHttpResponse(200, "false"));
        assertFalse(barUserService.validateUserAgainstSite(email, token, siteId));
    }

    @Test
    public void testValidateUserAgainstSiteWhenAnyParameterIsBlank() throws IOException {
        String email = "user@gmail.com";
        String token = "this_is_a_user_token";
        String siteId = "siteId";
        assertFalse(barUserService.validateUserAgainstSite("", token, siteId));
        assertFalse(barUserService.validateUserAgainstSite(null, token, siteId));
        assertFalse(barUserService.validateUserAgainstSite(" ", token, siteId));

        assertFalse(barUserService.validateUserAgainstSite(email, " ", siteId));
        assertFalse(barUserService.validateUserAgainstSite(email, "", siteId));
        assertFalse(barUserService.validateUserAgainstSite(email, null, siteId));

        assertFalse(barUserService.validateUserAgainstSite(email, token, ""));
        assertFalse(barUserService.validateUserAgainstSite(email, token, " "));
        assertFalse(barUserService.validateUserAgainstSite(email, token, null));
    }

    @Test
    public void checkSiteVaidationRequest() throws IOException {
        String email = "user@gmail.com";
        String token = "this_is_a_user_token";
        String siteId = "siteId";
        when(httpClient.execute(any(HttpGet.class))).thenAnswer(invocation -> {
            HttpGet httpGet = invocation.getArgument(0);
            assertThat(httpGet.getMethod(), Is.is("GET"));
            assertThat(httpGet.getURI().toString(), Is.is("http://localhost:23444/sites/siteId/users/user@gmail.com"));
            assertThat(httpGet.getHeaders("Authorization")[0].getValue(), Is.is("this_is_a_user_token"));
            return new SiteHttpResponse(200, "true");
        });
        barUserService.validateUserAgainstSite(email, token, siteId);
    }

    public static class SiteHttpResponse implements CloseableHttpResponse {

        private String message;
        private int responseCode;

        SiteHttpResponse(int responseCode, String message){
            this.responseCode = responseCode;
            this.message = message;
        }

        @Override
        public void close() throws IOException {
            // Not implemented
        }

        @Override
        public StatusLine getStatusLine() {
            return new BasicStatusLine(
                new ProtocolVersion("http", 1, 1),
                this.responseCode,
                "OK"
            );
        }

        @Override
        public void setStatusLine(StatusLine statusLine) {
            // Not implemented
        }

        @Override
        public void setStatusLine(ProtocolVersion protocolVersion, int i) {
            // Not implemented
        }

        @Override
        public void setStatusLine(ProtocolVersion protocolVersion, int i, String s) {
            // Not implemented
        }

        @Override
        public void setStatusCode(int i) throws IllegalStateException {
            // Not implemented
        }

        @Override
        public void setReasonPhrase(String s) throws IllegalStateException {
            // Not implemented
        }

        @Override
        public HttpEntity getEntity() {
            try {
                return new StringEntity(this.message);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void setEntity(HttpEntity httpEntity) {
            // Not implemented
        }

        @Override
        public Locale getLocale() {
            return null;
        }

        @Override
        public void setLocale(Locale locale) {
            // Not implemented
        }

        @Override
        public ProtocolVersion getProtocolVersion() {
            return null;
        }

        @Override
        public boolean containsHeader(String s) {
            return false;
        }

        @Override
        public Header[] getHeaders(String s) {
            return new Header[0];
        }

        @Override
        public Header getFirstHeader(String s) {
            return null;
        }

        @Override
        public Header getLastHeader(String s) {
            return null;
        }

        @Override
        public Header[] getAllHeaders() {
            return new Header[0];
        }

        @Override
        public void addHeader(Header header) {
            // Not implemented
        }

        @Override
        public void addHeader(String s, String s1) {
            // Not implemented
        }

        @Override
        public void setHeader(Header header) {
            // Not implemented
        }

        @Override
        public void setHeader(String s, String s1) {
            // Not implemented
        }

        @Override
        public void setHeaders(Header[] headers) {
            // Not implemented
        }

        @Override
        public void removeHeader(Header header) {
            // Not implemented
        }

        @Override
        public void removeHeaders(String s) {
            // Not implemented
        }

        @Override
        public HeaderIterator headerIterator() {
            return null;
        }

        @Override
        public HeaderIterator headerIterator(String s) {
            return null;
        }

        @Override
        public HttpParams getParams() {
            return null;
        }

        @Override
        public void setParams(HttpParams httpParams) {
            // Not implemented
        }
    }
}

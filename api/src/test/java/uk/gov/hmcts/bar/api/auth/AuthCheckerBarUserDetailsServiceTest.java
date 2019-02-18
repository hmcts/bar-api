package uk.gov.hmcts.bar.api.auth;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class AuthCheckerBarUserDetailsServiceTest {

    @Mock
    private PreAuthenticatedAuthenticationToken token;

    private BarUserPrincipal principal;

    private AuthCheckerBarUserDetailsService service;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        principal = new BarUserPrincipal("123456", new HashSet<>(Arrays.asList("role1, role2")), "forename", "surename", "fs@mail.com");
        service = new AuthCheckerBarUserDetailsService();
    }

    @Test
    public void testGetUserDetailServiceWhenTokenIsValid() {
        when(token.getPrincipal()).thenReturn(principal);
        when(token.getCredentials()).thenReturn("password");
        UserDetails userDetails = service.loadUserDetails(token);
        assertTrue(userDetails instanceof BarUserDetails);
        BarUserDetails barUserDetails = (BarUserDetails) userDetails;
        assertEquals("fs@mail.com", barUserDetails.getEmail());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUserDetailServiceWhenPasswordIsNull() {
        when(token.getPrincipal()).thenReturn(principal);
        service.loadUserDetails(token);
    }

    @Test(expected = UserAuthenticationException.class)
    public void testGetUserDetailServiceWhenPrinipalNotRecognised() {
        when(token.getPrincipal()).thenReturn(new Object());
        when(token.getCredentials()).thenReturn("password");
        UserDetails userDetails = service.loadUserDetails(token);
        assertTrue(userDetails instanceof BarUserDetails);
        BarUserDetails barUserDetails = (BarUserDetails) userDetails;
        assertEquals("fs@mail.com", barUserDetails.getEmail());
    }
}

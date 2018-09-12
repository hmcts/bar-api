package uk.gov.hmcts.bar.api.data.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import uk.gov.hmcts.bar.api.data.model.BarUser;
import uk.gov.hmcts.bar.api.data.repository.BarUserRepository;
import uk.gov.hmcts.reform.auth.checker.spring.useronly.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class BarUserServiceTest {

    @InjectMocks
    private BarUserService barUserService;

    @Mock
    private BarUserRepository barUserRepository;

    private SecurityContext securityContext;
    private BarUser barUser;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        barUserService = new BarUserService(barUserRepository);

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
                return new UserDetails("username", "token", Arrays.asList("bar-super-user"));
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

        barUser = new BarUser("user1", Collections.emptySet(), "user1@mail.com", "user", "one",PaymentInstructionService.SITE_ID);
    }

    @Test(expected = AccessDeniedException.class)
    public void whenSecurityContextIsInvalid_shouldReceiveAccessDeniedException() {
        SecurityContextHolder.setContext(new SecurityContextImpl());

        barUserService.getCurrentUserId();
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
}

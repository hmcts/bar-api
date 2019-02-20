package uk.gov.hmcts.bar.api.auth;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.bar.api.data.model.BarUser;
import uk.gov.hmcts.bar.api.data.service.BarUserService;
import uk.gov.hmcts.reform.auth.checker.core.user.User;
import uk.gov.hmcts.reform.auth.parser.idam.core.user.token.UserTokenParser;

import java.util.Collections;
import java.util.HashSet;

import static org.mockito.Mockito.*;

public class UserResolverTest {

    @InjectMocks
    private UserResolver userResolverMock;

    @Mock
    private BarUserService barUserServiceMock;

    @Mock
    private User userMock;

    @Mock
    private BarUser barUserMock;

    @Mock
    private UserTokenParser<UserTokenDetails> userTokenParserMock;

    @Mock
    private UserTokenDetails userTokenDetailsMock;


    @Before
    public void setupMock() {
        MockitoAnnotations.initMocks(this);
       barUserMock = new BarUser("id",new HashSet<>(),"testting@testing.com","post","clerk");
       userTokenDetailsMock = new UserTokenDetails("defaultService","testting@testing.com","post","clerk","id", Collections.EMPTY_SET);
    }

    @Test
    public void shouldReturnUserTokenDetails_whenGetTokenDetailsIsCalled(){
        when(userTokenParserMock.parse("BearerToken")).thenReturn(userTokenDetailsMock);
        when(barUserServiceMock.saveUser(barUserMock)).thenReturn(barUserMock);
        userResolverMock.getTokenDetails("BearerToken");
        verify(userTokenParserMock, times(1)).parse("BearerToken");
        verify(barUserServiceMock, times(1)).saveUser(barUserMock);

    }

}

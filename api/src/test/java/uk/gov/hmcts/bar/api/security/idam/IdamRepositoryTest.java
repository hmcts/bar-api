package uk.gov.hmcts.bar.api.security.idam;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.bar.api.componenttests.ComponentTestBase;
import uk.gov.hmcts.reform.idam.client.IdamApi;
import uk.gov.hmcts.reform.idam.client.IdamClient;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;

@RunWith(MockitoJUnitRunner.class)
public class IdamRepositoryTest {

    @Autowired
    IdamRepository idamRepository;

    @InjectMocks
    IdamClient idamClient;

    @Mock
    IdamApi idamApi;

    @Before
    public void setup() {
        Mockito.when(idamApi.retrieveUserInfo(Mockito.anyString())).thenReturn(ComponentTestBase.getUserInfoBasedOnUID_Roles("1111-2222-3333-4444", "bar"));
    }

    @Test
    public void testUserInfo() {
        UserInfo userInfo = idamClient.getUserInfo("1111-2222-3333-4444");
        Assert.assertTrue(userInfo.getRoles().size() == 1);
        Assert.assertTrue(userInfo.getSub().contains("@hmcts.net"));
    }
}

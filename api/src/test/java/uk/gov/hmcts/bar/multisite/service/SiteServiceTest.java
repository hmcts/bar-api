package uk.gov.hmcts.bar.multisite.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.bar.api.data.exceptions.BadRequestException;
import uk.gov.hmcts.bar.multisite.model.Site;
import uk.gov.hmcts.bar.multisite.repository.SiteRepository;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SiteServiceTest {

    private SiteService service;

    @Mock
    private SiteRepository siteRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        service = new SiteService(siteRepository);
    }

    @Test
    public void testGetSitesWithUsers() {
        String siteId = "1";
        Site site = Site.siteWith().id(siteId).description("one").build();
        when(siteRepository.findById(siteId)).thenReturn(Optional.of(site));
        when(siteRepository.findAllEmailsToSite(siteId)).thenReturn(Arrays.asList("a@a.com", "b@b.com", "c@c.com"));
        Site fullSite = service.getSitesWithUsers("1");
        assertEquals(Arrays.asList("a@a.com", "b@b.com", "c@c.com"), fullSite.getEmails());
        assertEquals("one", fullSite.getDescription());
    }

    @Test
    public void testGetSitesWithUsersWithInvalidSiteId() {
        String siteId = "1";
        when(siteRepository.findById(siteId)).thenReturn(Optional.empty());
        try {
            service.getSitesWithUsers("1");
        } catch (BadRequestException e) {
            assertEquals("This site id does not exist: 1", e.getMessage());
        }

        verify(siteRepository, times(1)).findById("1");
        verify(siteRepository, times(0)).findAllEmailsToSite(anyString());
    }

    @Test
    public void testAssignUserToSite() {
        String email = "a@a.com";
        String siteId = "1";
        Site site = Site.siteWith().id(siteId).description("one").build();
        when(siteRepository.findUserInSite(siteId, email)).thenReturn(Optional.empty());
        service.assignUserToSite(site, email);

        verify(siteRepository, times(1)).findUserInSite(siteId, email);
        verify(siteRepository, times(1)).assignUserToSite(site.getId(), email);
    }

    @Test
    public void testAssignUserToSiteWhenAlreadyAssigned() {
        String email = "a@a.com";
        String siteId = "1";
        Site site = Site.siteWith().id(siteId).description("one").build();
        when(siteRepository.findUserInSite(siteId, email)).thenReturn(Optional.of(email));
        try {
            service.assignUserToSite(site, email);
        } catch (BadRequestException e) {
            assertEquals("The user with 'a@a.com' email already assigned to 1", e.getMessage());
        }

        verify(siteRepository, times(1)).findUserInSite(siteId, email);
        verify(siteRepository, times(0)).assignUserToSite(site.getId(), email);
    }

    @Test
    public void validateUserAgainstSite() {
        when(siteRepository.findUserInSite(anyString(), eq("user1@mail.com"))).thenReturn(Optional.of("user1@mail.com"));
        assertEquals(true, service.validateUserAgainstSite("site", "user1@mail.com"));

        when(siteRepository.findUserInSite(anyString(), eq("user2@mail.com"))).thenReturn(Optional.empty());
        assertEquals(false, service.validateUserAgainstSite("site", "user2@mail.com"));
    }
}

package uk.gov.hmcts.bar.multisite.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.hmcts.bar.multisite.model.Site;
import uk.gov.hmcts.bar.multisite.service.SiteService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SiteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SiteService siteService;


    @InjectMocks
    private SiteController siteController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(siteController).build();
    }

    @Test
    public void testGetAllSites() throws Exception {
        when(siteService.getAllSites()).thenReturn(getAllSites());
        this.mockMvc.perform(get("/sites"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].id", is("1")))
            .andExpect(jsonPath("$[1].id", is("2")))
            .andExpect(jsonPath("$[2].id", is("3")));

        verify(siteService, times(1)).getAllSites();
        verifyNoMoreInteractions(siteService);
    }

    @Test
    public void testSaveSite() throws Exception {
        Site site = Site.siteWith().id("1").description("one").build();
        when(siteService.saveSite(site)).thenReturn(site);
        this.mockMvc.perform(post("/sites")
            .content("{ \"id\": \"1\", \"description\": \"one\" }")
            .contentType("application/json"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is("1")))
            .andExpect(jsonPath("$.description", is("one")));

        verify(siteService, times(1)).saveSite(site);
    }


    @Test
    public void testSaveSiteWhenSiteExists() throws Exception {
        Site site = Site.siteWith().id("1").description("one").build();
        when(siteService.saveSite(site)).thenReturn(site);
        when(siteService.findById("1")).thenReturn(Optional.of(site));
        this.mockMvc.perform(post("/sites")
            .content("{ \"id\": \"1\", \"description\": \"one\" }")
            .contentType("application/json"))
            .andExpect(status().isBadRequest());

        verify(siteService, times(0)).saveSite(site);
    }

    @Test
    public void testUpdateSiteWhenSiteDoesNotExists() throws Exception {
        Site site = Site.siteWith().id("1").description("one").build();
        when(siteService.saveSite(site)).thenReturn(site);
        when(siteService.findById("1")).thenReturn(Optional.empty());
        this.mockMvc.perform(put("/sites/1")
            .content("{ \"id\": \"1\", \"description\": \"one\" }")
            .contentType("application/json"))
            .andExpect(status().isBadRequest());

        verify(siteService, times(0)).saveSite(site);
    }

    @Test
    public void testGetSiteWithUsers() throws Exception {
        String id = "1";
        when(siteService.getSitesWithUsers(id)).thenReturn(
            Site.siteWith().id("1").description("one").emails(Arrays.asList("a@a.com", "b@b.com", "c@c.com")).build());
        this.mockMvc.perform(get("/sites/1/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is("1")))
            .andExpect(jsonPath("$.description", is("one")))
            .andExpect(jsonPath("$.emails[0]", is("a@a.com")));

        verify(siteService, times(1)).getSitesWithUsers(id);

    }

    @Test
    public void addUserToSite() throws Exception {
        Site site = Site.siteWith().id("1").description("one").build();
        String email = "a%40ab%2ecom";
        when(siteService.findById("1")).thenReturn(Optional.of(site));
        this.mockMvc.perform(post("/sites/1/users/{email}", email))
            .andExpect(status().isCreated());

        verify(siteService, times(1)).assignUserToSite(site, email);
    }

    @Test
    public void addUserToSiteWhenSiteDoesNotExsists() throws Exception {
        Site site = Site.siteWith().id("1").description("one").build();
        String email = "a%40ab%2ecom";
        when(siteService.findById("1")).thenReturn(Optional.empty());
        this.mockMvc.perform(post("/sites/1/users/{email}", email))
            .andExpect(status().isBadRequest());

        verify(siteService, times(1)).findById("1");
        verify(siteService, times(0)).assignUserToSite(site, email);
    }

    @Test
    public void removeUserFromSite() throws Exception {
        Site site = Site.siteWith().id("1").description("one").build();
        String email = "a%40ab%2ecom";
        when(siteService.findById("1")).thenReturn(Optional.of(site));
        this.mockMvc.perform(delete("/sites/1/users/{email}", email))
            .andExpect(status().isOk());

        verify(siteService, times(1)).deleteUserFromSite(site, email);
    }

    @Test
    public void testRemoveUserWhenSiteWhenSiteDoesNotExsists() throws Exception {
        Site site = Site.siteWith().id("1").description("one").build();
        String email = "a%40ab%2ecom";
        when(siteService.findById("1")).thenReturn(Optional.empty());
        this.mockMvc.perform(delete("/sites/1/users/{email}", email))
            .andExpect(status().isBadRequest());

        verify(siteService, times(1)).findById("1");
        verify(siteService, times(0)).deleteUserFromSite(site, email);
    }

    @Test
    public void testGetSelectedSiteForUser() throws Exception {
        Site site = Site.siteWith().id("1").description("one").build();
        String email = "a%40ab%2ecom";
        when(siteService.getUserSelectedSite(email)).thenReturn(Optional.of(site));
        this.mockMvc.perform(get("/sites/users/{email}/selected", email))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is("1")))
            .andExpect(jsonPath("$.description", is("one")));
    }

    @Test
    public void testGetSelectedSiteIdForUser() throws Exception {
        String email = "a%40ab%2ecom";
        when(siteService.getUserSelectedSiteId(email)).thenReturn(Optional.of("1"));
        this.mockMvc.perform(get("/sites/users/{email}/selected/id", email))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.siteId", is("1")));
    }

    @Test
    public void testGetSelectedSiteForUserWhenNoSelectedSite() throws Exception {
        String email = "a%40ab%2ecom";
        when(siteService.getUserSelectedSite(email)).thenReturn(Optional.empty());
        this.mockMvc.perform(get("/sites/users/{email}/selected", email))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetSelectedSiteIdForUserWhenNoelectedSite() throws Exception {
        String email = "a%40ab%2ecom";
        when(siteService.getUserSelectedSiteId(email)).thenReturn(Optional.empty());
        this.mockMvc.perform(get("/sites/users/{email}/selected/id", email))
            .andExpect(status().isBadRequest());
    }

    private List<Site> getAllSites() {
        return Arrays.asList(
           Site.siteWith().id("1").description("one").build(),
           Site.siteWith().id("2").description("two").build(),
           Site.siteWith().id("3").description("three").build()
        );
    }
}

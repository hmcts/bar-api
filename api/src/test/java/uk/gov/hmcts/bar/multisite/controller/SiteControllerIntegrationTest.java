package uk.gov.hmcts.bar.multisite.controller;

import org.junit.Test;
import uk.gov.hmcts.bar.api.componenttests.ComponentTestBase;
import uk.gov.hmcts.bar.multisite.model.Site;
import uk.gov.hmcts.bar.multisite.model.SiteRequest;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SiteControllerIntegrationTest extends ComponentTestBase {

    @Test
    public void addNewSite() throws Exception {
        restActions
            .post("/sites", Site.siteWith().id("test01").description("test 01").build())
            .andExpect(status().isForbidden());

        restActionsForDM
            .post("/sites", Site.siteWith().id("test01").description("test 01").build())
            .andExpect(status().isCreated())
            .andExpect(body().as(Site.class, site -> {
                assertThat(site.getId()).isEqualTo("test01");
            }));

        // check id really saved
        restActions
            .get("/sites")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, sites -> {
                assertThat(sites.size()).isEqualTo(1);
            }));
    }

    @Test
    public void editExistingSite() throws Exception {
        // Create a site
        restActionsForDM
            .post("/sites", Site.siteWith().id("test01").description("test 01").build())
            .andExpect(status().isCreated())
            .andExpect(body().as(Site.class, site -> {
                assertThat(site.getId()).isEqualTo("test01");
            }));

        // Modify as not admin
        restActions
            .put("/sites/test01", SiteRequest.builder().description("Bromley").build())
            .andExpect(status().isForbidden());
        // Modify as admin
        restActionsForDM
            .put("/sites/test01", SiteRequest.builder().description("Bromley").build())
            .andExpect(status().isOk())
            .andExpect(body().as(Site.class, site -> {
                assertThat(site.getId()).isEqualTo("test01");
            }));

        // Check the changes
        restActions
            .get("/sites")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, sites -> {
                assertThat(((LinkedHashMap)sites.get(0)).get("description")).isEqualTo("Bromley");
            }));
    }

    @Test
    public void addUserToSite() throws Exception {
        // Create a site
        restActionsForDM
            .post("/sites", Site.siteWith().id("test01").description("test 01").build())
            .andExpect(status().isCreated())
            .andExpect(body().as(Site.class, site -> {
                assertThat(site.getId()).isEqualTo("test01");
            }));

        // Assign a user to the created site
        restActionsForDM
            .post("/sites/test01/users/user@hmcts.net", null)
            .andExpect(status().isCreated());

        // Check if the user is assigned
        restActionsForDM
            .get("/sites/test01/users")
            .andExpect(status().isOk())
            .andExpect(body().as(Site.class, site -> {
                assertThat(site.getId()).isEqualTo("test01");
                assertThat(site.getEmails()).isEqualTo(Collections.singletonList("user@hmcts.net"));
            }));
    }

    @Test
    public void removeUserFromSite() throws Exception {
        // Create a site
        restActionsForDM
            .post("/sites", Site.siteWith().id("test01").description("test 01").build())
            .andExpect(status().isCreated())
            .andExpect(body().as(Site.class, site -> {
                assertThat(site.getId()).isEqualTo("test01");
            }));

        // Assign a user to the created site
        restActionsForDM
            .post("/sites/test01/users/user@hmcts.net", null)
            .andExpect(status().isCreated());

        // Check if the user is assigned
        restActionsForDM
            .get("/sites/test01/users")
            .andExpect(status().isOk())
            .andExpect(body().as(Site.class, site -> {
                assertThat(site.getId()).isEqualTo("test01");
                assertThat(site.getEmails().size()).isEqualTo(1);
                assertThat(site.getEmails()).isEqualTo(Collections.singletonList("user@hmcts.net"));
            }));

        // Remove user from site
        restActionsForDM
            .delete("/sites/test01/users/user@hmcts.net")
            .andExpect(status().isOk());

        // Check if the user is removed from site
        restActionsForDM
            .get("/sites/test01/users")
            .andExpect(status().isOk())
            .andExpect(body().as(Site.class, site -> {
                assertThat(site.getId()).isEqualTo("test01");
                assertThat(site.getEmails().size()).isEqualTo(0);
            }));
    }

    @Test
    public void testIfUserAssignedToSite() throws Exception {
        addUserToSite();

        restActions
            .get("/sites/test01/users/user@hmcts.net")
            .andExpect(status().isOk())
            .andExpect(body().as(Boolean.class, resp -> {
                assertThat(resp).isEqualTo(true);
            }));

        restActions
            .get("/sites/test01/users/other_user@hmcts.net")
            .andExpect(status().isOk())
            .andExpect(body().as(Boolean.class, resp -> {
                assertThat(resp).isEqualTo(false);
            }));
    }

    @Test
    public void testCollectUserSites() throws Exception {
        addUserToSite();

        restActionsForDM
            .get("/users/user@hmcts.net/sites")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, sites -> {
                assertThat(sites.size()).isEqualTo(1);
                assertThat(((Map)sites.get(0)).get("id")).isEqualTo("test01");
                assertThat(((Map)sites.get(0)).get("description")).isEqualTo("test 01");
            }));

    }

    @Test
    public void testCollectUserSitesWhenUserNotAssigned() throws Exception {

        restActionsForDM
            .get("/users/user@hmcts.net/sites")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, sites -> {
                assertThat(sites.size()).isEqualTo(0);
            }));

    }
}

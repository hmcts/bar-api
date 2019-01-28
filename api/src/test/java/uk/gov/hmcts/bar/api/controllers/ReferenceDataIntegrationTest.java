package uk.gov.hmcts.bar.api.controllers;

import org.junit.Test;
import uk.gov.hmcts.bar.api.componenttests.ComponentTestBase;
import uk.gov.hmcts.bar.api.data.model.Site;
import uk.gov.hmcts.bar.api.data.model.SiteRequest;

import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReferenceDataIntegrationTest extends ComponentTestBase {

    @Test
    public void retrieveAllAvailableSites() throws Exception {
        restActions
            .get("/sites")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, sites -> {
                assertThat(((LinkedHashMap)sites.get(0)).get("siteId")).isEqualTo("Y431");
            }));

    }

    @Test
    public void addNewSite() throws Exception {
        restActions
            .post("/sites", Site.siteWith().siteId("test01").siteName("test 01").siteNumber("32").build())
            .andExpect(status().isForbidden());

        restActionsForAdmin
            .post("/sites", Site.siteWith().siteId("test01").siteName("test 01").siteNumber("32").build())
            .andExpect(status().isCreated())
            .andExpect(body().as(Site.class, site -> {
                assertThat(site.getSiteId()).isEqualTo("test01");
            }));

        // check id really saved
        restActions
            .get("/sites")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, sites -> {
                assertThat(sites.size()).isEqualTo(2);
            }));
    }

    @Test
    public void editExistingSite() throws Exception {
        restActions
            .put("/sites/Y431", SiteRequest.builder().siteName("Bromley").siteNumber("32").build())
            .andExpect(status().isForbidden());

        restActionsForAdmin
            .put("/sites/Y431", SiteRequest.builder().siteName("Bromley").siteNumber("32").build())
            .andExpect(status().isOk())
            .andExpect(body().as(Site.class, site -> {
                assertThat(site.getSiteId()).isEqualTo("Y431");
            }));

        restActions
            .get("/sites")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, sites -> {
                assertThat(((LinkedHashMap)sites.get(0)).get("siteName")).isEqualTo("Bromley");
            }));

    }
}

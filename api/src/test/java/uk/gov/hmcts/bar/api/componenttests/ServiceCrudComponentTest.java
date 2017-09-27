package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;
import uk.gov.hmcts.bar.api.model.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ServiceCrudComponentTest extends ComponentTestBase {

    @Test
    public void retrieveAllServices() throws Exception {
        ResultActions resultActions = restActions
            .get("/services")
            .andExpect(status().isOk())
            .andExpect(body().asListOf(Service.class, (services) -> {
                assertThat(services).anySatisfy(service -> {
                    assertThat(service.getName()).isEqualTo("Family");
                    assertThat(service.getSubServices()).anySatisfy(subService -> {
                        assertThat(subService.getName()).isEqualTo("Divorce");
                    });
                });
            }));
    };
}

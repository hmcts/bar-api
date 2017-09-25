package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;
import uk.gov.hmcts.bar.api.model.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ServiceCrudComponentTest extends ComponentTestBase {


    @Test
    public void retrieveAll1() throws Exception {
        ResultActions resultActions = restActions
            .get("/services")
            .andExpect(status().isOk())
            .andExpect(body().asListOf(Service.class, (services) -> {
                assertThat(services).anySatisfy(service -> {
                    assertThat(service.getName()).isEqualTo("Civil claims");
                    assertThat(service.getSubServices()).anySatisfy(subService -> {
                        assertThat(subService.getName()).isEqualTo("Bailiffs");

                    });
                });
            }));
    };
}

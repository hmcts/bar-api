package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;
import uk.gov.hmcts.bar.api.model.PaymentType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.bar.api.model.PaymentType.paymentTypeWith;

public class PaymentTypeCrudComponentTest extends ComponentTestBase{

    @Test
    public void retrieveAllPaymentTypes() throws Exception {
        restActions
            .get("/payment_types")
            .andExpect(status().isOk())
            .andExpect(body().asListOf(PaymentType.class, paymentTypes -> {
                assertThat(paymentTypes).contains(
                    paymentTypeWith()
                        .id(1)
                        .name("AllPay")
                        .build()
                );
            }));
    }

}

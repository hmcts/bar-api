package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;
import uk.gov.hmcts.bar.api.model.PaymentType;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PaymentTypeCrudComponentTest extends ComponentTestBase{

    @Test
    public void retrieveAllPaymentTypes() throws Exception {
        restActions
            .get("/payment-types")
            .andExpect(status().isOk())
            .andExpect(body().asListOf(PaymentType.class, paymentTypes -> {
                assertThat(paymentTypes).isEqualTo(
                    new ArrayList<PaymentType>() {{
                        add(new PaymentType(1,"Cheque"));
                        add(new PaymentType(2,"Full Remission"));
                        add(new PaymentType(3,"Card"));
                        add(new PaymentType(4,"Postal Order"));
                        add(new PaymentType(5,"Cash"));
                        add(new PaymentType(6,"AllPay"));
                    }});
            }));
    }

}

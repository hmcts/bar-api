package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;
import uk.gov.hmcts.bar.api.data.model.PaymentType;

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
                assertThat(paymentTypes).hasSameElementsAs(
                    new ArrayList<PaymentType>() {{
                        add(new PaymentType("cheques","Cheque"));
                        add(new PaymentType("card","Card"));
                        add(new PaymentType("postal-orders","Postal Order"));
                        add(new PaymentType("cash","Cash"));
                        add(new PaymentType("allpay","AllPay"));
                    }});
            }));
    }

}

package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;
import uk.gov.hmcts.bar.api.data.model.PaymentType;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PaymentTypeCrudComponentTest extends ComponentTestBase {

    @Test
    public void retrieveAllPaymentTypes() throws Exception {
        restActions
            .get("/payment-types")
            .andExpect(status().isOk())
            .andExpect(body().asListOf(PaymentType.class, paymentTypes -> {
                assertThat(paymentTypes).hasSameElementsAs(
                    new ArrayList<PaymentType>() {{
                        add(new PaymentType("CHEQUE","Cheque"));
                        add(new PaymentType("CARD","Card"));
                        add(new PaymentType("POSTAL_ORDER","Postal Order"));
                        add(new PaymentType("CASH","Cash"));
                        add(new PaymentType("ALLPAY","AllPay"));
                    }
                    });
            }));
    }

}

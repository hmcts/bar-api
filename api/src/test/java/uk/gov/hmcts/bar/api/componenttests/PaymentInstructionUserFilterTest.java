package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Assert;
import org.junit.Test;
import uk.gov.hmcts.bar.api.componenttests.utils.DbTestUtil;
import uk.gov.hmcts.bar.api.data.model.CardPaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PaymentInstructionUserFilterTest extends ComponentTestBase {


    @Test
    public void getAllPaymentInstructions() throws Exception {
        DbTestUtil.insertPaymentInstructions(getWebApplicationContext());
        restActions
            .get("/payment-instructions")
            .andExpect(status().isOk())
            .andExpect(body().asListOf(CardPaymentInstruction.class, paymentInstructions -> {
                Assert.assertTrue(paymentInstructions.size() == 2);
            }));

    }

    @Test
    public void findAllPaymentInstructionsByUser() throws Exception {
        DbTestUtil.insertPaymentInstructions(getWebApplicationContext());
        restActions
            .get("/user/1234/payment-instructions")
            .andExpect(status().isOk())
            .andExpect(body().asListOf(CardPaymentInstruction.class, paymentInstructions -> {
                Assert.assertTrue(paymentInstructions.size() == 1);
            }));

    }
}

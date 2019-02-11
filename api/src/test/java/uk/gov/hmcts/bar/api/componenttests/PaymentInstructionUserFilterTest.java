package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;
import uk.gov.hmcts.bar.api.componenttests.utils.DbTestUtil;
import uk.gov.hmcts.bar.api.data.model.CardPaymentInstruction;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PaymentInstructionUserFilterTest extends ComponentTestBase {


    @Test
    public void whenNoUsersInPath_thenGetAllPaymentInstructions() throws Exception {
        DbTestUtil.insertBGCNumber(getWebApplicationContext());
        DbTestUtil.insertPaymentInstructions(getWebApplicationContext());
        restActions
            .get("/payment-instructions")
            .andExpect(status().isOk())
            .andExpect(body().asListOf(CardPaymentInstruction.class, paymentInstructions -> {
                assertTrue(paymentInstructions.size() == 5);
            }));

    }

    @Test
    public void whenUsersInPath_thenFilterPaymentInstructionResultsByUser() throws Exception {
        DbTestUtil.insertBGCNumber(getWebApplicationContext());
        DbTestUtil.insertPaymentInstructions(getWebApplicationContext());
        restActions
            .get("/users/1234/payment-instructions")
            .andExpect(status().isOk())
            .andExpect(body().asListOf(CardPaymentInstruction.class, paymentInstructions -> {
                assertTrue(paymentInstructions.size() == 1);
            }));
    }

    @Test
    public void testGettingPaymentInstructionsForUserByAction() throws Exception {
        DbTestUtil.insertBGCNumber(getWebApplicationContext());
        DbTestUtil.insertPaymentInstructions(getWebApplicationContext());

        restActions
            .get("/users/1234/payment-instructions?action=Process")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, item -> {
                assertTrue(item.size() == 1);
            }));

        restActions
            .get("/users/4321/payment-instructions?action=Process")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, item -> {
                assertTrue(item.size() == 2);
            }));
    }

    @Test
    public void testGettingPaymentInstructionsForUserByActionAndBgcNumber() throws Exception {
        DbTestUtil.insertBGCNumber(getWebApplicationContext());
        DbTestUtil.insertPaymentInstructions(getWebApplicationContext());

        restActions
            .get("/users/4321/payment-instructions?action=Process&bgcNumber=isNull")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, item -> {
                assertTrue(item.size() == 1);
            }));

        restActions
            .get("/users/4321/payment-instructions?action=Process&bgcNumber=123456")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, item -> {
                assertTrue(item.size() == 1);
            }));
    }
}

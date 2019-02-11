package uk.gov.hmcts.bar.api.controllers;

import org.junit.Assert;
import org.junit.Test;
import uk.gov.hmcts.bar.api.componenttests.ComponentTestBase;
import uk.gov.hmcts.bar.api.componenttests.utils.DbTestUtil;
import uk.gov.hmcts.bar.api.data.model.CardPaymentInstruction;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PaymentInstructionSearchTest extends ComponentTestBase {

    @Test
    public void searchForPaymentByPaymentType() throws Exception {
        DbTestUtil.insertPaymentInstructions(getWebApplicationContext());

        restActions
            .get("/payment-instructions?paymentType=CACHE")
            .andExpect(status().isOk())
            .andExpect(body().asListOf(CardPaymentInstruction.class, paymentInstructions -> {
                Assert.assertTrue(paymentInstructions.size() == 0);
            }));

        restActions
            .get("/payment-instructions?paymentType=CARD")
            .andExpect(status().isOk())
            .andExpect(body().asListOf(CardPaymentInstruction.class, paymentInstructions -> {
                Assert.assertTrue(paymentInstructions.size() == 2);
            }));
    }

    @Test
    public void searchForPaymentByPaymentTypeMultiple() throws Exception {
        DbTestUtil.insertPaymentInstructions(getWebApplicationContext());

        restActions
            .get("/payment-instructions?paymentType=CARD,CASH")
            .andExpect(status().isOk())
            .andExpect(body().asListOf(CardPaymentInstruction.class, paymentInstructions -> {
                Assert.assertTrue(paymentInstructions.size() == 3);
            }));
    }

    @Test
    public void searchForPaymentByCaseReference() throws Exception {
        DbTestUtil.insertPaymentInstructions(getWebApplicationContext());
        restActions
            .get("/payment-instructions?caseReference=123")
            .andExpect(status().isOk())
            .andExpect(body().asListOf(CardPaymentInstruction.class, paymentInstructions -> {
                Assert.assertTrue(paymentInstructions.size() == 1);
            }));
    }

    @Test
    public void searchForPaymentByPaymentReferenceAndPaymentType() throws Exception {
        DbTestUtil.insertPaymentInstructions(getWebApplicationContext());

        restActions
            .get("/payment-instructions?caseReference=123&paymentType=cache")
            .andExpect(status().isOk())
            .andExpect(body().asListOf(CardPaymentInstruction.class, paymentInstructions -> {
                Assert.assertTrue(paymentInstructions.size() == 0);
            }));

        restActions
            .get("/payment-instructions?caseReference=123&paymentType=CARD")
            .andExpect(status().isOk())
            .andExpect(body().asListOf(CardPaymentInstruction.class, paymentInstructions -> {
                Assert.assertTrue(paymentInstructions.size() == 1);
            }));
    }

    @Test
    public void searchPaymentByCaseReferenceOrPaymentReference_WhenBothCanBeFound() throws Exception {
        DbTestUtil.insertPaymentInstructions(getWebApplicationContext());

        restActions
            .get("/payment-instructions?caseReference=123&allPayInstructionId=123")
            .andExpect(status().isOk())
            .andExpect(body().asListOf(CardPaymentInstruction.class, paymentInstructions -> {
                Assert.assertTrue(paymentInstructions.size() == 2);
            }));
    }

    @Test
    public void searchPaymentByCaseReferenceOrPaymentReference_WhenOnlyOneCanBeFound() throws Exception {
        DbTestUtil.insertPaymentInstructions(getWebApplicationContext());

        restActions
            .get("/payment-instructions?caseReference=1234&allPayInstructionId=1234")
            .andExpect(status().isOk())
            .andExpect(body().asListOf(CardPaymentInstruction.class, paymentInstructions -> {
                Assert.assertTrue(paymentInstructions.size() == 1);
            }));
    }

    @Test
    public void searchPaymentByAuthorizationCode() throws Exception {
        DbTestUtil.insertPaymentInstructions(getWebApplicationContext());

        restActions
            .get("/payment-instructions?caseReference=1234&authorizationCode=a1234B")
            .andExpect(status().isOk())
            .andExpect(body().asListOf(CardPaymentInstruction.class, paymentInstructions -> {
                Assert.assertTrue(paymentInstructions.size() == 1);
            }));
    }

    @Test
    public void searchForRejectedItemsByUserId() throws Exception {
        DbTestUtil.insertPaymentInstructions(getWebApplicationContext());

        restActions
            .get("/users/1234/payment-instructions?status=RDM&oldStatus=A")
            .andExpect(status().isOk())
            .andExpect(body().asListOf(CardPaymentInstruction.class, paymentInstructions -> {
                Assert.assertTrue(paymentInstructions.size() == 1);
            }));
    }

}

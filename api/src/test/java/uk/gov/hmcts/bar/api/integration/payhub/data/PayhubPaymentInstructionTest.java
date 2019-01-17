package uk.gov.hmcts.bar.api.integration.payhub.data;

import org.junit.Assert;
import org.junit.Test;
import uk.gov.hmcts.bar.api.data.TestUtils;
import uk.gov.hmcts.bar.api.data.model.PaymentType;

public class PayhubPaymentInstructionTest {

    @Test
    public void testGetCardExternalReference() {
        PayhubPaymentInstruction paymentInstruction = TestUtils.createSamplePayhuPaymentInstruction(10050, new int [][] {{5000, 0, 0}, {5000, 0, 0}});
        paymentInstruction.setAuthorizationCode("123456");
        Assert.assertEquals("123456", paymentInstruction.getExternalReference());
    }

    @Test
    public void testGetChequeExternalReference() {
        PayhubPaymentInstruction paymentInstruction = TestUtils.createSamplePayhuPaymentInstruction(10050, new int [][] {{5000, 0, 0}, {5000, 0, 0}});
        paymentInstruction.setChequeNumber("D");
        Assert.assertEquals("D", paymentInstruction.getExternalReference());
    }

    @Test
    public void testGetReference() {
        PayhubPaymentInstruction paymentInstruction = TestUtils.createSamplePayhuPaymentInstruction(10050, new int [][] {{5000, 0, 0}, {5000, 0, 0}});
        paymentInstruction.setChequeNumber("D");
        paymentInstruction.setSiteId("Y431");
        Assert.assertEquals("Y431-20180813null", paymentInstruction.getReference());
    }

    @Test
    public void testGetAmount() {
        PayhubPaymentInstruction paymentInstruction = TestUtils.createSamplePayhuPaymentInstruction(33, new int [][] {{5000, 0, 0}, {5000, 0, 0}});
        paymentInstruction.setChequeNumber("D");
        paymentInstruction.setSiteId("Y431");
        paymentInstruction.setPaymentType(PaymentType.paymentTypeWith().id("CARD").name("Cards").build());
        Assert.assertEquals("0.33", paymentInstruction.getAmountAsDecimal().toString());

        paymentInstruction.setAmount(10000);
        Assert.assertEquals("100.00", paymentInstruction.getAmountAsDecimal().toString());
    }
}

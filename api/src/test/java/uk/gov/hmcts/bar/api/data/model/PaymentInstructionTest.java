package uk.gov.hmcts.bar.api.data.model;

import org.junit.Assert;
import org.junit.Test;
import uk.gov.hmcts.bar.api.data.TestUtils;

public class PaymentInstructionTest {

    @Test
    public void testGetCardExternalReference() {
        PaymentInstruction paymentInstruction = TestUtils.createSamplePaymentInstruction("card",10050, new int [][] {{5000, 0, 0}, {5000, 0, 0}});
        Assert.assertEquals("123456", paymentInstruction.getExternalReference());
    }

    @Test
    public void testGetChequeExternalReference() {
        PaymentInstruction paymentInstruction = TestUtils.createSamplePaymentInstruction("cheque",10050, new int [][] {{5000, 0, 0}, {5000, 0, 0}});
        Assert.assertEquals("D", paymentInstruction.getExternalReference());
    }
}

package uk.gov.hmcts.bar.api.integration.payhub.data;

import org.junit.Assert;
import org.junit.Test;
import uk.gov.hmcts.bar.api.data.TestUtils;

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
        Assert.assertEquals("Y431-201808130", paymentInstruction.getReference());
    }
}

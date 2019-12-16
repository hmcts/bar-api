package uk.gov.hmcts.bar.api.data.model;

import org.junit.Test;
import uk.gov.hmcts.bar.api.data.TestUtils;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class PaymentInstructionFlattenDataTest {

    /*@Test
    public void testFlatteningCardPayment(){
        PaymentInstruction paymentInstruction = TestUtils.createSamplePaymentInstruction("CARD",10050, new int [][] {{5000, 0, 0}, {5000, 0, 0}});
        List<PaymentInstructionReportLine> flattenData = paymentInstruction.flattenPaymentInstruction();
        assertEquals(null, flattenData.get(0).getDailyId());
        assertEquals(10050, flattenData.get(0).getCardAmount().intValue());
        flattenData.forEach(line -> {
            assertEquals(null, line.getCheckAmount());
            assertEquals(null, line.getPostalOrderAmount());
            assertEquals(null, line.getCashAmount());
        });
    }*/

    /*@Test
    public void testFlatteningCashPayment(){
        PaymentInstruction paymentInstruction = TestUtils.createSamplePaymentInstruction("CASH",10063, new int [][] {{5000, 0, 0}, {5000, 0, 0}});
        List<PaymentInstructionReportLine> flattenData = paymentInstruction.flattenPaymentInstruction();

        assertEquals(10063, flattenData.get(0).getCashAmount().intValue());
        flattenData.forEach(line -> {
            assertEquals(null, line.getCheckAmount());
            assertEquals(null, line.getPostalOrderAmount());
            assertEquals(null, line.getCardAmount());
        });
    }*/

    /*@Test
    public void testTypedEmptyPaymentInstructionWithoutCaseRef(){
        CashPaymentInstruction paymentInstruction = new CashPaymentInstruction();
        List<PaymentInstructionReportLine> flattenData = paymentInstruction.flattenPaymentInstruction();
        assertEquals(null, flattenData.get(0).getCashAmount());
        assertEquals(null, flattenData.get(0).getDailyId());
    }*/

    /*@Test
    public void testTypedEmptyPaymentInstruction(){
        CashPaymentInstruction paymentInstruction = CashPaymentInstruction.cashPaymentInstructionWith()
            .payerName("John Doe")
            .amount(10000)
            .build();
        List<PaymentInstructionReportLine> flattenData = paymentInstruction.flattenPaymentInstruction();
        assertEquals(10000, flattenData.get(0).getCashAmount().intValue());
        assertEquals(null, flattenData.get(0).getDailyId());
        assertEquals("John Doe", flattenData.get(0).getName());
    }*/

}

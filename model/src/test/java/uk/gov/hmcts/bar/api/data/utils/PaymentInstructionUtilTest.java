package uk.gov.hmcts.bar.api.data.utils;

import org.junit.Test;
import uk.gov.hmcts.bar.api.data.TestUtils;
import uk.gov.hmcts.bar.api.data.exceptions.PaymentInstructionConverterException;
import uk.gov.hmcts.bar.api.data.model.CaseReference;
import uk.gov.hmcts.bar.api.data.model.CashPaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class PaymentInstructionUtilTest {

    @Test
    public void testFlatteningCardPayment(){
        PaymentInstruction paymentInstruction = TestUtils.createSamplePaymentInstruction("card",10000, new int [][] {{5000, 0, 0}, {5000, 0, 0}});
        List<String[]> flattenData = PaymentInstructionUtil.flattenPaymentInstruction(paymentInstruction);
        assertEquals(13, flattenData.get(0).length);
        assertEquals("0", flattenData.get(0)[0]);
        assertEquals("10000", flattenData.get(0)[6]);
        flattenData.forEach(strings -> {
            assertEquals(null, strings[3]);
            assertEquals(null, strings[4]);
            assertEquals(null, strings[5]);
        });
    }

    @Test
    public void testFlatteningCashPayment(){
        PaymentInstruction paymentInstruction = TestUtils.createSamplePaymentInstruction("cash",10000, new int [][] {{5000, 0, 0}, {5000, 0, 0}});
        List<String[]> flattenData = PaymentInstructionUtil.flattenPaymentInstruction(paymentInstruction);

        assertEquals("10000", flattenData.get(0)[5]);
        flattenData.forEach(strings -> {
            assertEquals(null, strings[3]);
            assertEquals(null, strings[4]);
            assertEquals(null, strings[6]);
        });
    }

    @Test(expected = PaymentInstructionConverterException.class)
    public void testEmptyPaymentInstruction(){
        PaymentInstruction paymentInstruction = new PaymentInstruction();
        PaymentInstructionUtil.flattenPaymentInstruction(paymentInstruction);
    }

    @Test
    public void testTypedEmptyPaymentInstructionWithoutCaseRef(){
        CashPaymentInstruction paymentInstruction = new CashPaymentInstruction();
        List<String[]> flattenData = PaymentInstructionUtil.flattenPaymentInstruction(paymentInstruction);
        assertEquals(null, flattenData.get(0)[5]);
        assertEquals("0", flattenData.get(0)[0]);
    }

    @Test
    public void testTypedEmptyPaymentInstruction(){
        CashPaymentInstruction paymentInstruction = CashPaymentInstruction.cashPaymentInstructionWith()
            .payerName("John Doe")
            .amount(10000)
            .build();
        List<String[]> flattenData = PaymentInstructionUtil.flattenPaymentInstruction(paymentInstruction);
        assertEquals("10000", flattenData.get(0)[5]);
        assertEquals("0", flattenData.get(0)[0]);
        assertEquals("John Doe", flattenData.get(0)[2]);
    }

    @Test
    public void testTypedPaymentInstructionWithEmptyCasereference(){
        CashPaymentInstruction paymentInstruction = new CashPaymentInstruction();
        CaseReference caseReference = CaseReference.caseReferenceWith().caseReference("12345").build();
        paymentInstruction.setCaseReferences(Arrays.asList(caseReference));
        List<String[]> flattenData = PaymentInstructionUtil.flattenPaymentInstruction(paymentInstruction);
        assertEquals(null, flattenData.get(0)[5]);
        assertEquals("0", flattenData.get(0)[0]);
        assertEquals("12345", flattenData.get(0)[9]);
    }
}

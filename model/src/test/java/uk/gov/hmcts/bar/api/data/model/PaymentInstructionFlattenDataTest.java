package uk.gov.hmcts.bar.api.data.model;

import org.junit.Test;
import uk.gov.hmcts.bar.api.data.TestUtils;
import uk.gov.hmcts.bar.api.data.model.CaseReference;
import uk.gov.hmcts.bar.api.data.model.CashPaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionReportLine;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class PaymentInstructionFlattenDataTest {

    @Test
    public void testFlatteningCardPayment(){
        PaymentInstruction paymentInstruction = TestUtils.createSamplePaymentInstruction("card",10050, new int [][] {{5000, 0, 0}, {5000, 0, 0}});
        List<PaymentInstructionReportLine> flattenData = paymentInstruction.flattenPaymentInstruction();
        assertEquals(0, flattenData.get(0).getDailyId().intValue());
        assertEquals(10050, flattenData.get(0).getCardAmount().intValue());
        flattenData.forEach(line -> {
            assertEquals(null, line.getCheckAmount());
            assertEquals(null, line.getPostalOrderAmount());
            assertEquals(null, line.getCashAmount());
        });
    }

    @Test
    public void testFlatteningCashPayment(){
        PaymentInstruction paymentInstruction = TestUtils.createSamplePaymentInstruction("cash",10063, new int [][] {{5000, 0, 0}, {5000, 0, 0}});
        List<PaymentInstructionReportLine> flattenData = paymentInstruction.flattenPaymentInstruction();

        assertEquals(10063, flattenData.get(0).getCashAmount().intValue());
        flattenData.forEach(line -> {
            assertEquals(null, line.getCheckAmount());
            assertEquals(null, line.getPostalOrderAmount());
            assertEquals(null, line.getCardAmount());
        });
    }

    @Test
    public void testTypedEmptyPaymentInstructionWithoutCaseRef(){
        CashPaymentInstruction paymentInstruction = new CashPaymentInstruction();
        List<PaymentInstructionReportLine> flattenData = paymentInstruction.flattenPaymentInstruction();
        assertEquals(null, flattenData.get(0).getCashAmount());
        assertEquals(0, flattenData.get(0).getDailyId().intValue());
    }

    @Test
    public void testTypedEmptyPaymentInstruction(){
        CashPaymentInstruction paymentInstruction = CashPaymentInstruction.cashPaymentInstructionWith()
            .payerName("John Doe")
            .amount(10000)
            .build();
        List<PaymentInstructionReportLine> flattenData = paymentInstruction.flattenPaymentInstruction();
        assertEquals(10000, flattenData.get(0).getCashAmount().intValue());
        assertEquals(0, flattenData.get(0).getDailyId().intValue());
        assertEquals("John Doe", flattenData.get(0).getName());
    }

    @Test
    public void testTypedPaymentInstructionWithEmptyCasereference(){
        CashPaymentInstruction paymentInstruction = new CashPaymentInstruction();
        CaseReference caseReference = CaseReference.caseReferenceWith().caseReference("12345").build();
        paymentInstruction.setCaseReferences(Arrays.asList(caseReference));
        List<PaymentInstructionReportLine> flattenData = paymentInstruction.flattenPaymentInstruction();
        assertEquals(null, flattenData.get(0).getCashAmount());
        assertEquals(0, flattenData.get(0).getDailyId().intValue());
        assertEquals("12345", flattenData.get(0).getCaseRef());
    }
}

package uk.gov.hmcts.bar.api.data.utils;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import uk.gov.hmcts.bar.api.data.model.AllPayPaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionRequest;

public class UtilTest {

	@Test
	public void whenPaymentInstructionWithNullPropertyValuesPassedIn_shouldReturnAllNullPropertyNames() {
		PaymentInstructionRequest pir = PaymentInstructionRequest.paymentInstructionRequestWith().payerName("Ravi")
				.amount(200).allPayTransactionId("748373").status("D").build();
		List<String> nullPropertyNames = Arrays.asList(Util.getNullPropertyNames(pir));
		assertTrue(nullPropertyNames.contains("chequeNumber") && nullPropertyNames.contains("postalOrderNumber"));
	}

	@Test
	public void whenPaymentIntructionListPassed_shouldReturnSameListWithCorrectStatusDisplayForDraft() {
		List<PaymentInstruction> piListModified = null;
		List<PaymentInstruction> piList = new ArrayList<PaymentInstruction>();
		PaymentInstruction pi = new AllPayPaymentInstruction();
		pi.setStatus("D");
		piList.add(pi);
		piListModified = Util.updateStatusDisplayValue(piList);
		assertTrue(piListModified.get(0).getStatus().equals("Draft"));
	}

	@Test
	public void whenPaymentIntructionListPassed_shouldReturnSameListWithCorrectStatusDisplayForPending() {
		List<PaymentInstruction> piListModified = null;
		List<PaymentInstruction> piList = new ArrayList<PaymentInstruction>();
		PaymentInstruction pi = new AllPayPaymentInstruction();
		pi.setStatus("P");
		piList.add(pi);
		piListModified = Util.updateStatusDisplayValue(piList);
		assertTrue(piListModified.get(0).getStatus().equals("Pending"));
	}
}

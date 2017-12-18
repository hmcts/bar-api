package uk.gov.hmcts.bar.api.controllers.helper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import uk.gov.hmcts.bar.api.data.model.AllPayPaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;

public class PaymentInstructionControllerHelperTest {

	@Test
    public void whenPaymentIntructionListPassed_shouldReturnSameListWithCorrectStatusDisplayForDraft() {
    		List<PaymentInstruction> piListModified = null;
    		List<PaymentInstruction> piList = new ArrayList<PaymentInstruction>();
    		PaymentInstruction pi = new AllPayPaymentInstruction();
    		pi.setStatus("D");
    		piList.add(pi);
    		piListModified = PaymentInstructionControllerHelper.updateStatusDisplayValue(piList);
    		assertThat(piListModified.get(0).getStatus()).isEqualTo("Draft");
    }
	
	@Test
    public void whenPaymentIntructionListPassed_shouldReturnSameListWithCorrectStatusDisplayForPending() {
    		List<PaymentInstruction> piListModified = null;
    		List<PaymentInstruction> piList = new ArrayList<PaymentInstruction>();
    		PaymentInstruction pi = new AllPayPaymentInstruction();
    		pi.setStatus("P");
    		piList.add(pi);
    		piListModified = PaymentInstructionControllerHelper.updateStatusDisplayValue(piList);
    		assertThat(piListModified.get(0).getStatus()).isEqualTo("Pending");
    }
}

package uk.gov.hmcts.bar.api.data.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import uk.gov.hmcts.bar.api.data.model.PaymentInstructionRequest;

public class UtilTest {

	@Test
	public void whenPaymentInstructionWithNullPropertyValuesPassedIn_shouldReturnAllNullPropertyNames() {
		PaymentInstructionRequest pir = PaymentInstructionRequest.paymentInstructionRequestWith().payerName("Ravi")
				.amount(200).allPayTransactionId("748373").status("D").build();
		List<String> nullPropertyNames = Arrays.asList(Util.getNullPropertyNames(pir));
		assertThat(nullPropertyNames.contains("chequeNumber") && nullPropertyNames.contains("postalOrderNumber"));
	}
}

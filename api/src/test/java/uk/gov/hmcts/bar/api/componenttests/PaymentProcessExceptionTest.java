package uk.gov.hmcts.bar.api.componenttests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import uk.gov.hmcts.bar.api.data.exceptions.PaymentProcessException;

public class PaymentProcessExceptionTest {

	@Test
	public void whenPaymentProcessExceptionCalled_shouldHaveCorrectErrorMessage() {
		PaymentProcessException ppe = new PaymentProcessException("Allocate all amount");
		assertThat(ppe.getMessage()).isEqualTo("Allocate all amount");
	}
}

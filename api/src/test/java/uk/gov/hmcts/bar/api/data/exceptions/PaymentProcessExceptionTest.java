package uk.gov.hmcts.bar.api.data.exceptions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.gov.hmcts.bar.api.data.exceptions.PaymentProcessException;

public class PaymentProcessExceptionTest {

    @Test
    public void whenPaymentProcessExceptionCalled_shouldHaveCorrectErrorMessage() {
        PaymentProcessException ppe = new PaymentProcessException("Allocate all amount");
        assertThat(ppe.getMessage()).isEqualTo("Allocate all amount");
        assertThat(ppe.getErrorMessage()).isEqualTo("Allocate all amount");
        assertThat(ppe.toString()).contains("Allocate all amount");
        PaymentProcessException ppeBuilder = PaymentProcessException.paymentProcessExceptionWith()
                .errorMessage("Allocate all amount").build();
        assertTrue(ppe.equals(ppeBuilder));
    }
}


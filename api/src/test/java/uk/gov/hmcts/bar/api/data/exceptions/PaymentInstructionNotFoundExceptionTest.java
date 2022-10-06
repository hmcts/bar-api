package uk.gov.hmcts.bar.api.data.exceptions;


import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentInstructionNotFoundExceptionTest {

    @Test
    public void whenPaymentInstructionNotFound_shouldReturnErrorMessage() {
        PaymentInstructionNotFoundException paymentInstructionNotFoundException = new PaymentInstructionNotFoundException(222);
        assertThat(paymentInstructionNotFoundException.getMessage()).isEqualTo("payment instruction: id = 222");
    }
}

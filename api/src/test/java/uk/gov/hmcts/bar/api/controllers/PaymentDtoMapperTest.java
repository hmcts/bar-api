package uk.gov.hmcts.bar.api.controllers;

import org.junit.Test;
import uk.gov.hmcts.bar.api.contract.PaymentDto;
import uk.gov.hmcts.bar.api.controllers.payment.PaymentDtoMapper;
import uk.gov.hmcts.bar.api.model.Payment;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentDtoMapperTest {

    private final PaymentDtoMapper paymentDtoMapper = new PaymentDtoMapper();

    @Test
    public void convertsToPaymentDto() {
        assertThat(paymentDtoMapper.toPaymentDto(
            Payment.paymentWith()
                .payeeName("Mr Tony Dowds")
                .caseReference("case1")
                .paymentChannel("bacs")
                .paymentDate(LocalDateTime.parse("2017-09-14T10:11:30"))
                .amount(500)
                .build()
        )
        ).isEqualTo(
            PaymentDto.paymentDtoWith()
                .payeeName("Mr Tony Dowds")
                .caseReference("case1")
                .paymentChannel("bacs")
                .paymentDate("2017-09-14T10:11:30")
                .amount(500)
                .build());
    }

    @Test
    public void convertsToPayment() {
        assertThat(paymentDtoMapper.toPayment(
            PaymentDto.paymentDtoWith()
                .payeeName("Mr Tony Dowds")
                .caseReference("case1")
                .paymentChannel("bacs")
                .paymentDate("2017-09-14T10:11:30")
                .amount(500)
                .build()
            )
        ).isEqualTo(
            Payment.paymentWith()
                .payeeName("Mr Tony Dowds")
                .caseReference("case1")
                .paymentChannel("bacs")
                .paymentDate(LocalDateTime.parse("2017-09-14T10:11:30"))
                .amount(500)
                .build());
    }

}

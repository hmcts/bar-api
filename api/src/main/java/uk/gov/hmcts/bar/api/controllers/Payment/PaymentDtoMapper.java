package uk.gov.hmcts.bar.api.controllers.Payment;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.bar.api.contract.PaymentDto;
import uk.gov.hmcts.bar.api.model.Payment;

import java.time.LocalDateTime;


@Component
public class PaymentDtoMapper {

    public PaymentDto toPaymentDto(Payment payment) {

        return PaymentDto.paymentDtoWith()
            .payeeName(payment.getPayeeName())
            .caseReference(payment.getCaseReference())
            .paymentChannel(payment.getPaymentChannel())
            .paymentDate(String.valueOf(payment.getPaymentDate()))
            .amount(payment.getAmount())
            .build();
    }



    public Payment toPayment(PaymentDto dto) {
        return Payment.paymentWith()
            .payeeName(dto.getPayeeName())
            .caseReference(dto.getCaseReference())
            .paymentChannel(dto.getPaymentChannel())
            .paymentDate(LocalDateTime.parse(dto.getPaymentDate()))
            .amount(dto.getAmount())
            .build();
    }



}


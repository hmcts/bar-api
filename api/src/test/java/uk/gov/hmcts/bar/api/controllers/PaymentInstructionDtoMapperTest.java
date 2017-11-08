package uk.gov.hmcts.bar.api.controllers;

import org.junit.Test;
import uk.gov.hmcts.bar.api.contract.CashPaymentInstructionDto;
import uk.gov.hmcts.bar.api.contract.ChequePaymentInstructionDto;
import uk.gov.hmcts.bar.api.contract.PostalOrderPaymentInstructionDto;
import uk.gov.hmcts.bar.api.controllers.payment.PaymentInstructionDtoMapper;
import uk.gov.hmcts.bar.api.model.CashPaymentInstruction;
import uk.gov.hmcts.bar.api.model.ChequePaymentInstruction;
import uk.gov.hmcts.bar.api.model.PostalOrderPaymentInstruction;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentInstructionDtoMapperTest {

    private final PaymentInstructionDtoMapper paymentInstructionDtoMapper = new PaymentInstructionDtoMapper();

    @Test
    public void givenChequePaymentInstruction_convertToChequePaymentInstructionDto() {
        assertThat(paymentInstructionDtoMapper.toPaymentInstructionDto(
           ChequePaymentInstruction.chequePaymentInstructionWith()
                .payerName("Mr Payer Payer")
                .sortCode("000000")
                .instrumentNumber("000000")
                .accountNumber("00000000")
                .currency("GBP")
                .amount(200)
                .build()
            )
        ).isEqualTo(
            ChequePaymentInstructionDto.chequePaymentInstructionDtoWith()
                .payerName("Mr Payer Payer")
                .sortCode("000000")
                .instrumentNumber("000000")
                .accountNumber("00000000")
                .currency("GBP")
                .amount(200)
                .build());
    }

    @Test
    public void givenCashPaymentInstruction_convertToCashPaymentInstructionDto() {
        assertThat(paymentInstructionDtoMapper.toPaymentInstructionDto(
            CashPaymentInstruction.cashPaymentInstructionWith()
                .payerName("Mr Payer Payer")
                .currency("GBP")
                .amount(200)
                .build()
            )
        ).isEqualTo(
            CashPaymentInstructionDto.cashPaymentInstructionDtoWith()
                .payerName("Mr Payer Payer")
                .currency("GBP")
                .amount(200)
                .build());
    }

    @Test
    public void givenPostalOrderPaymentInstruction_convertToPostalOrderPaymentInstructionDto() {
        assertThat(paymentInstructionDtoMapper.toPaymentInstructionDto(
            PostalOrderPaymentInstruction.postalOrderPaymentInstructionWith()
                .payerName("Mr Payer Payer")
                .currency("GBP")
                .amount(200)
                .instrumentNumber("000000")
                .build()
            )
        ).isEqualTo(
            PostalOrderPaymentInstructionDto.postalOrderPaymentInstructionDtoWith()
                .payerName("Mr Payer Payer")
                .currency("GBP")
                .amount(200)
                .instrumentNumber("000000")
                .build());
    }


}

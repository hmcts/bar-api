package uk.gov.hmcts.bar.api.controllers.payment;


import org.springframework.stereotype.Component;
import uk.gov.hmcts.bar.api.contract.*;
import uk.gov.hmcts.bar.api.model.*;

@Component
public class PaymentInstructionDtoMapper {

    public PaymentInstructionDto toPaymentInstructionDto(PaymentInstruction paymentInstruction) {
        if (paymentInstruction instanceof ChequePaymentInstruction) {
            return new ChequePaymentInstructionDto(paymentInstruction.getPayerName(), paymentInstruction.getAmount(), paymentInstruction.getCurrency(),((ChequePaymentInstruction) paymentInstruction).getSortCode(),
                ((ChequePaymentInstruction) paymentInstruction).getAccountNumber(), ((ChequePaymentInstruction) paymentInstruction).getInstrumentNumber()
            );
        } else if (paymentInstruction instanceof CashPaymentInstruction) {
            return new CashPaymentInstructionDto(paymentInstruction.getPayerName(), paymentInstruction.getAmount(), paymentInstruction.getCurrency());
        } else if (paymentInstruction instanceof PostalOrderPaymentInstruction) {
            return new PostalOrderPaymentInstructionDto(paymentInstruction.getPayerName(), paymentInstruction.getAmount(), paymentInstruction.getCurrency(),((PostalOrderPaymentInstruction) paymentInstruction).getInstrumentNumber());
        }else if (paymentInstruction instanceof AllPayPaymentInstruction) {
            return new AllPayPaymentInstructionDto(paymentInstruction.getPayerName(), paymentInstruction.getAmount(), paymentInstruction.getCurrency(), ((AllPayPaymentInstruction) paymentInstruction).getAllPayTransactionId());
        } else {
            throw new IllegalArgumentException("Unknown payment instruction type: " + paymentInstruction.getClass());
        }
    }

    public PaymentInstruction toPaymentInstruction(PaymentInstructionDto paymentInstructionDto) {
        if (paymentInstructionDto instanceof ChequePaymentInstructionDto) {
            return new ChequePaymentInstruction(paymentInstructionDto.getPayerName(), paymentInstructionDto.getAmount(), paymentInstructionDto.getCurrency(), ((ChequePaymentInstructionDto) paymentInstructionDto).getSortCode(),
                ((ChequePaymentInstructionDto) paymentInstructionDto).getAccountNumber(), ((ChequePaymentInstructionDto) paymentInstructionDto).getInstrumentNumber()
            );
        } else if (paymentInstructionDto instanceof CashPaymentInstructionDto) {
            return new CashPaymentInstruction(paymentInstructionDto.getPayerName(), paymentInstructionDto.getAmount(), paymentInstructionDto.getCurrency());
        } else if (paymentInstructionDto instanceof PostalOrderPaymentInstructionDto) {
            return new PostalOrderPaymentInstruction(paymentInstructionDto.getPayerName(), paymentInstructionDto.getAmount(), paymentInstructionDto.getCurrency(),((PostalOrderPaymentInstructionDto) paymentInstructionDto).getInstrumentNumber());
        } else if (paymentInstructionDto instanceof AllPayPaymentInstructionDto) {
            return new AllPayPaymentInstruction(paymentInstructionDto.getPayerName(), paymentInstructionDto.getAmount(), paymentInstructionDto.getCurrency(), ((AllPayPaymentInstructionDto) paymentInstructionDto).getAllPayTransactionId());
        } else {
            throw new IllegalArgumentException("Unknown payment instruction type: " + paymentInstructionDto.getClass());
        }
    }

}

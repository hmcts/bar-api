package uk.gov.hmcts.bar.api.controllers.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.bar.api.contract.CashPaymentInstructionDto;
import uk.gov.hmcts.bar.api.contract.ChequePaymentInstructionDto;
import uk.gov.hmcts.bar.api.contract.PaymentInstructionDto;
import uk.gov.hmcts.bar.api.contract.PostalOrderPaymentInstructionDto;
import uk.gov.hmcts.bar.api.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.model.PaymentInstructionService;

import javax.validation.Valid;
@RestController
@Validated
public class PaymentInstructionController {

    private final PaymentInstructionService paymentInstructionService;
    private final PaymentInstructionDtoMapper paymentInstructionDtoMapper;

    @Autowired
    public PaymentInstructionController(PaymentInstructionService paymentInstructionService, PaymentInstructionDtoMapper paymentInstructionDtoMapper) {
        this.paymentInstructionService = paymentInstructionService;
        this.paymentInstructionDtoMapper = paymentInstructionDtoMapper;
    }


    @PostMapping("/cheques")
    public PaymentInstructionDto saveChequeInstruction(@Valid @RequestBody ChequePaymentInstructionDto chequePaymentInstructionDto) {
        PaymentInstruction paymentInstruction = paymentInstructionService.savePaymentInstruction(paymentInstructionDtoMapper.toPaymentInstruction(chequePaymentInstructionDto));
        return paymentInstructionDtoMapper.toPaymentInstructionDto(paymentInstruction);
    }


    @PostMapping("/cash")
    public PaymentInstructionDto savecashInstruction(@Valid @RequestBody CashPaymentInstructionDto cashPaymentInstructionDto) {
        PaymentInstruction paymentInstruction = paymentInstructionService.savePaymentInstruction(paymentInstructionDtoMapper.toPaymentInstruction(cashPaymentInstructionDto));
        return paymentInstructionDtoMapper.toPaymentInstructionDto(paymentInstruction);
    }


    @PostMapping("/postal-orders")
    public PaymentInstructionDto savecashInstruction(@Valid @RequestBody PostalOrderPaymentInstructionDto postalOrderPaymentInstructionDto) {
        PaymentInstruction paymentInstruction = paymentInstructionService.savePaymentInstruction(paymentInstructionDtoMapper.toPaymentInstruction(postalOrderPaymentInstructionDto));
        return paymentInstructionDtoMapper.toPaymentInstructionDto(paymentInstruction);
    }



}



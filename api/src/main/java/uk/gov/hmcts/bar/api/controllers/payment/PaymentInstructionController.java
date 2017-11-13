package uk.gov.hmcts.bar.api.controllers.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.bar.api.contract.*;
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
        PaymentInstruction paymentInstruction = paymentInstructionService.createPaymentInstruction(paymentInstructionDtoMapper.toPaymentInstruction(chequePaymentInstructionDto));
        return paymentInstructionDtoMapper.toPaymentInstructionDto(paymentInstruction);
    }

    @PostMapping("/cash")
    public PaymentInstructionDto saveCashInstruction(@Valid @RequestBody CashPaymentInstructionDto cashPaymentInstructionDto) {
        PaymentInstruction paymentInstruction = paymentInstructionService.createPaymentInstruction(paymentInstructionDtoMapper.toPaymentInstruction(cashPaymentInstructionDto));
        return paymentInstructionDtoMapper.toPaymentInstructionDto(paymentInstruction);
    }

    @PostMapping("/postal-orders")
    public PaymentInstructionDto savePostalOrderInstruction(@Valid @RequestBody PostalOrderPaymentInstructionDto postalOrderPaymentInstructionDto) {
        PaymentInstruction paymentInstruction = paymentInstructionService.createPaymentInstruction(paymentInstructionDtoMapper.toPaymentInstruction(postalOrderPaymentInstructionDto));
        return paymentInstructionDtoMapper.toPaymentInstructionDto(paymentInstruction);
    }

    @PostMapping("/allpay")
    public PaymentInstructionDto saveAllPayInstruction(@Valid @RequestBody AllPayPaymentInstructionDto allPayPaymentInstructionDto) {
        PaymentInstruction paymentInstruction = paymentInstructionService.createPaymentInstruction(paymentInstructionDtoMapper.toPaymentInstruction(allPayPaymentInstructionDto));
        return paymentInstructionDtoMapper.toPaymentInstructionDto(paymentInstruction);
    }



}



package uk.gov.hmcts.bar.api.controllers.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.bar.api.data.model.*;
import uk.gov.hmcts.bar.api.data.service.PaymentInstructionService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
public class PaymentInstructionController {

    private final PaymentInstructionService paymentInstructionService;

    @Autowired
    public PaymentInstructionController(PaymentInstructionService paymentInstructionService) {
        this.paymentInstructionService = paymentInstructionService;

    }
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/payment-instructions")
    public List<PaymentInstruction> getPaymentInstructions() {
        return paymentInstructionService.getAllPaymentInstructions();
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/cheques")
    public PaymentInstruction saveChequeInstruction(@Valid @RequestBody ChequePaymentInstruction chequePaymentInstruction) {
        return paymentInstructionService.createPaymentInstruction(chequePaymentInstruction);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/cash")
    public PaymentInstruction saveCashInstruction(@Valid @RequestBody CashPaymentInstruction cashPaymentInstruction) {
        return  paymentInstructionService.createPaymentInstruction(cashPaymentInstruction);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/postal-orders")
    public PaymentInstruction savePostalOrderInstruction(@Valid @RequestBody PostalOrderPaymentInstruction postalOrderPaymentInstruction) {
       return  paymentInstructionService.createPaymentInstruction(postalOrderPaymentInstruction);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/allpay")
    public PaymentInstruction saveAllPayInstruction(@Valid @RequestBody AllPayPaymentInstruction allPayPaymentInstruction) {
         return  paymentInstructionService.createPaymentInstruction(allPayPaymentInstruction);
    }

}



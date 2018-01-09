package uk.gov.hmcts.bar.api.controllers.payment;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.bar.api.data.model.*;
import uk.gov.hmcts.bar.api.data.service.PaymentInstructionService;
import uk.gov.hmcts.bar.api.data.utils.Util;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static uk.gov.hmcts.bar.api.data.model.AllPayPaymentInstruction.allPayPaymentInstructionWith;
import static uk.gov.hmcts.bar.api.data.model.CashPaymentInstruction.cashPaymentInstructionWith;
import static uk.gov.hmcts.bar.api.data.model.ChequePaymentInstruction.chequePaymentInstructionWith;
import static uk.gov.hmcts.bar.api.data.model.PostalOrderPaymentInstruction.postalOrderPaymentInstructionWith;

@RestController

@Validated
public class PaymentInstructionController {

    private final PaymentInstructionService paymentInstructionService;

    @Autowired
    public PaymentInstructionController(PaymentInstructionService paymentInstructionService) {
        this.paymentInstructionService = paymentInstructionService;

    }

    @ApiOperation(value = "Get all current payment instructions", notes = "Get all current payment instructions for a given site.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Return all current payment instructions"),
        @ApiResponse(code = 404, message = "Payment instructions not found"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/payment-instructions")
    public List<PaymentInstruction> getPaymentInstructions(
        @RequestParam(name = "status", required = false) String status,
        @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "ddMMyyyy") LocalDate startDate,
        @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "ddMMyyyy") LocalDate endDate,
        @RequestParam(name = "payerName", required = false) String payerName,
        @Valid @Pattern(regexp = "^\\d{6,6}$", message = "invalid cheque number") @RequestParam(name = "chequeNumber", required = false) String chequeNumber,
        @Valid @Pattern(regexp = "^\\d{6,6}$", message = "invalid postal order number") @RequestParam(name = "postalOrderNumber", required = false) String postalOrderNumber,
        @RequestParam(name = "dailySequenceId", required = false) Integer dailySequenceId,
        @Valid @Pattern(regexp = "^\\d{1,20}$", message = "invalid all pay transaction id") @RequestParam(name = "allPayInstructionId", required = false) String allPayInstructionId,
        @RequestParam(name = "paymentType", required = false) String paymentType) {

        PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto = PaymentInstructionSearchCriteriaDto
            .paymentInstructionSearchCriteriaDto().status(status)
            .startDate(startDate == null ? null : startDate.atStartOfDay())
            .endDate(endDate == null ? null : endDate.atTime(LocalTime.now())).payerName(payerName)
            .chequeNumber(chequeNumber).postalOrderNumer(postalOrderNumber).dailySequenceId(dailySequenceId)
            .allPayInstructionId(allPayInstructionId).paymentType(paymentType).build();
        return Util.updateStatusDisplayValue(paymentInstructionService.getAllPaymentInstructions(paymentInstructionSearchCriteriaDto));
    }

    @ApiOperation(value = "Get the payment instruction", notes = "Get the payment instruction for the given id.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Return payment instruction"),
        @ApiResponse(code = 404, message = "Payment instruction not found"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/payment-instructions/{id}")
    public ResponseEntity<PaymentInstruction> getPaymentInstruction(@PathVariable("id") Integer id) {
        PaymentInstruction paymentInstruction = paymentInstructionService.getPaymentInstruction(id);
        if (paymentInstruction == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(paymentInstruction, HttpStatus.OK);
    }

    @ApiOperation(value = "Delete payment instruction", notes = "Delete payment instruction with the given id.")
    @ApiResponses(value = {@ApiResponse(code = 204, message = "Payment instruction deleted"),
        @ApiResponse(code = 404, message = "Payment instruction not found"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/payment-instructions/{id}")
    public void deletePaymentInstruction(@PathVariable("id") Integer id) {
        paymentInstructionService.deletePaymentInstruction(id);
    }

    @ApiOperation(value = "Create cheque payment instruction", notes = "Create cheque payment instruction with the given values.")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Cheque payment instruction created"),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/cheques")
    public PaymentInstruction saveChequeInstruction(
        @Valid @RequestBody Cheque cheque) {
        ChequePaymentInstruction chequePaymentInstruction = chequePaymentInstructionWith()
            .payerName(cheque.getPayerName())
            .amount(cheque.getAmount())
            .currency(cheque.getCurrency())
            .chequeNumber(cheque.getChequeNumber()).build();
        return paymentInstructionService.createPaymentInstruction(chequePaymentInstruction);
    }

    @ApiOperation(value = "Update cheque payment instruction", notes = "Update cheque payment instruction with the given values.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Postal Order payment instruction updated"),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/cheques/{id}")
    public ResponseEntity<Void> updateChequeInstruction(@PathVariable("id") Integer id , @ApiParam(value="Cheque request",required=true) @Valid @RequestBody Cheque cheque) {
        paymentInstructionService.updatePaymentInstruction(id,cheque);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @ApiOperation(value = "Create cash payment instruction", notes = "Create cash payment instruction with the given values.")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Cash payment instruction created"),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/cash")
    public PaymentInstruction saveCashInstruction(@ApiParam(value="Cash request",required=true) @Valid @RequestBody Cash cash) {
        CashPaymentInstruction cashPaymentInstruction = cashPaymentInstructionWith()
            .payerName(cash.getPayerName())
            .amount(cash.getAmount())
            .currency(cash.getCurrency()).build();
        return paymentInstructionService.createPaymentInstruction(cashPaymentInstruction);
    }


    @ApiOperation(value = "Update cash payment instruction", notes = "Update cash payment instruction with the given values.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Cash payment instruction updated"),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/cash/{id}")
    public ResponseEntity<Void> updateCashInstruction(@PathVariable("id") Integer id , @ApiParam(value="Cash request",required=true) @Valid @RequestBody Cash cash) {
        paymentInstructionService.updatePaymentInstruction(id,cash);
        return new ResponseEntity<>(HttpStatus.OK);
    }




    @ApiOperation(value = "Create poatal order payment instruction", notes = "Create postal order payment instruction with the given values.")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Postal order payment instruction created"),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/postal-orders")
    public PaymentInstruction savePostalOrderInstruction(
        @ApiParam(value="Postal Order request",required=true) @Valid @RequestBody PostalOrder postalOrder) {
        PostalOrderPaymentInstruction postalOrderPaymentInstruction = postalOrderPaymentInstructionWith()
            .payerName(postalOrder.getPayerName())
            .amount(postalOrder.getAmount())
            .currency(postalOrder.getCurrency())
            .postalOrderNumber(postalOrder.getPostalOrderNumber()).build();
        return paymentInstructionService.createPaymentInstruction(postalOrderPaymentInstruction);
    }

    @ApiOperation(value = "Update postal order payment instruction", notes = "Update postal order payment instruction with the given values.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Postal Order payment instruction updated"),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/postal-orders/{id}")
    public ResponseEntity<Void> updatePostalOrderInstruction(@PathVariable("id") Integer id , @ApiParam(value="Postal order request",required=true) @Valid @RequestBody PostalOrder postalOrder) {
        paymentInstructionService.updatePaymentInstruction(id,postalOrder);
        return new ResponseEntity<>(HttpStatus.OK);
    }



    @ApiOperation(value = "Create allpay payment instruction", notes = "Create allpay payment instruction with the given values.")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "AllPay payment instruction created"),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/allpay")
    public PaymentInstruction saveAllPayInstruction(
        @ApiParam(value="All Pay request", required=true) @Valid @RequestBody AllPay allPay) {
        AllPayPaymentInstruction allPayPaymentInstruction = allPayPaymentInstructionWith()
            .payerName(allPay.getPayerName())
            .amount(allPay.getAmount())
            .currency(allPay.getCurrency())
            .allPayTransactionId(allPay.getAllPayTransactionId()).build();
        return paymentInstructionService.createPaymentInstruction(allPayPaymentInstruction);
    }

    @ApiOperation(value = "Update allpay payment instruction", notes = "Update allpay payment instruction with the given values.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Allpay payment instruction updated"),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/allpay/{id}")
    public ResponseEntity<Void> updateAllPayInstruction(@PathVariable("id") Integer id , @ApiParam(value="Allpay request",required=true) @Valid @RequestBody AllPay allpay) {
        paymentInstructionService.updatePaymentInstruction(id,allpay);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @ApiOperation(value = "Submit current payment instructions by post clerk", notes = "Submit current payment instructions by a post clerk.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Submit current payment instructions by post clerk"),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/payment-instructions/{id}")
    public ResponseEntity<PaymentInstruction> submitPaymentInstructionsByPostClerk(@PathVariable("id") Integer id,
                                                                                   @RequestBody PaymentInstructionUpdateRequest paymentInstructionUpdateRequest) {
        if (null == paymentInstructionUpdateRequest) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        PaymentInstruction submittedPaymentInstruction = paymentInstructionService.submitPaymentInstruction(id, paymentInstructionUpdateRequest);
        return new ResponseEntity<>(submittedPaymentInstruction, HttpStatus.OK);
    }

    @ApiOperation(value = "Create case reference for a payment instruction", notes = "Create case reference for a payment instruction.")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Case reference for a payment instruction created"),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/payment-instructions/{id}/cases")
    public PaymentInstruction saveCaseReference(
        @PathVariable("id") Integer id, @RequestBody CaseReferenceRequest caseReferenceRequest) {
        return paymentInstructionService.createCaseReference(id, caseReferenceRequest);
    }

}

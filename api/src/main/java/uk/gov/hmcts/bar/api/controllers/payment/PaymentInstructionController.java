package uk.gov.hmcts.bar.api.controllers.payment;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.bar.api.controllers.helper.PaymentInstructionControllerHelper;
import uk.gov.hmcts.bar.api.data.model.*;
import uk.gov.hmcts.bar.api.data.service.PaymentInstructionService;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

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
        @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "ddMMyyyy") Date startDate,
        @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "ddMMyyyy") Date endDate) {

        return PaymentInstructionControllerHelper.updateStatusDisplayValue(
            paymentInstructionService.getAllPaymentInstructions(status, startDate, endDate));
    }

    @ApiOperation(value = "Get the payment instruction", notes = "Get the payment instruction for the given id.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Return payment instruction"),
        @ApiResponse(code = 404, message = "Payment instruction not found"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/payment-instructions/{id}")
    public ResponseEntity<?> getPaymentInstruction(@PathVariable("id") Integer id) {
        PaymentInstruction paymentInstruction = paymentInstructionService.getPaymentInstruction(id);
        if (paymentInstruction == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<PaymentInstruction>(paymentInstruction, HttpStatus.OK);
    }

    @ApiOperation(value = "Delete payment instruction", notes = "Delete payment instruction with the given id.")
    @ApiResponses(value = {@ApiResponse(code = 204, message = "Payment instruction deleted"),
        @ApiResponse(code = 404, message = "Payment instruction not found"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/payment-instructions/{id}")
    public ResponseEntity<?> deletePaymentInstruction(@PathVariable("id") Integer id) {
    		paymentInstructionService.deletePaymentInstruction(id);
        return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "Create cheque payment instruction", notes = "Create cheque payment instruction with the given values.")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Cheque payment instruction created"),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/cheques")
    public PaymentInstruction saveChequeInstruction(
        @Valid @RequestBody ChequePaymentInstruction chequePaymentInstruction) {
        return paymentInstructionService.createPaymentInstruction(chequePaymentInstruction);
    }

    @ApiOperation(value = "Create cash payment instruction", notes = "Create cash payment instruction with the given values.")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Cash payment instruction created"),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/cash")
    public PaymentInstruction saveCashInstruction(@Valid @RequestBody CashPaymentInstruction cashPaymentInstruction) {
        return paymentInstructionService.createPaymentInstruction(cashPaymentInstruction);
    }

    @ApiOperation(value = "Create poatal order payment instruction", notes = "Create postal order payment instruction with the given values.")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Postal order payment instruction created"),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/postal-orders")
    public PaymentInstruction savePostalOrderInstruction(
        @Valid @RequestBody PostalOrderPaymentInstruction postalOrderPaymentInstruction) {
        return paymentInstructionService.createPaymentInstruction(postalOrderPaymentInstruction);
    }

    @ApiOperation(value = "Create allpay payment instruction", notes = "Create allpay payment instruction with the given values.")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "AllPay payment instruction created"),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/allpay")
    public PaymentInstruction saveAllPayInstruction(
        @Valid @RequestBody AllPayPaymentInstruction allPayPaymentInstruction) {
        return paymentInstructionService.createPaymentInstruction(allPayPaymentInstruction);
    }


    @ApiOperation(value = "Submit current payment instructions by post clerk", notes = "Submit current payment instructions by a post clerk.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Submit current payment instructions by post clerk"),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/payment-instructions/{id}")
    public ResponseEntity<?> submitPaymentInstructionsByPostClerk(@PathVariable("id") Integer id,
                                                                  @RequestBody PaymentInstructionRequest paymentInstructionRequest) {
        if (null == paymentInstructionRequest) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        PaymentInstruction updatedPaymentInstruction = paymentInstructionService.updatePaymentInstruction(id, paymentInstructionRequest);
        return new ResponseEntity<PaymentInstruction>(updatedPaymentInstruction, HttpStatus.OK);
    }

    @ApiOperation(value = "Create case reference for a payment instruction", notes = "Create case reference for a payment instruction.")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Case reference for a payment instruction created"),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/payment-instructions/{id}/cases")
    public PaymentInstruction saveCaseReference(
        @PathVariable("id") Integer id, @RequestBody CaseReferenceRequest caseReferenceRequest) {
        return paymentInstructionService.createCaseReference(id,caseReferenceRequest);
    }


}

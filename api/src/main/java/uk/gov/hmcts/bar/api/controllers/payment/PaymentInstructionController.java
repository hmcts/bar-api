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
import uk.gov.hmcts.bar.api.data.service.CaseFeeDetailService;
import uk.gov.hmcts.bar.api.data.service.PaymentInstructionService;
import uk.gov.hmcts.bar.api.data.service.UnallocatedAmountService;
import uk.gov.hmcts.bar.api.data.utils.Util;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.bar.api.data.model.AllPayPaymentInstruction.allPayPaymentInstructionWith;
import static uk.gov.hmcts.bar.api.data.model.CardPaymentInstruction.cardPaymentInstructionWith;
import static uk.gov.hmcts.bar.api.data.model.CashPaymentInstruction.cashPaymentInstructionWith;
import static uk.gov.hmcts.bar.api.data.model.ChequePaymentInstruction.chequePaymentInstructionWith;
import static uk.gov.hmcts.bar.api.data.model.PostalOrderPaymentInstruction.postalOrderPaymentInstructionWith;

@RestController

@Validated
public class PaymentInstructionController {

    private final PaymentInstructionService paymentInstructionService;

    private final CaseFeeDetailService caseFeeDetailService;

    private final UnallocatedAmountService unallocatedAmountService;

    @Autowired
    public PaymentInstructionController(PaymentInstructionService paymentInstructionService,
                                        CaseFeeDetailService caseFeeDetailService,
                                        UnallocatedAmountService unallocatedAmountService) {
        this.paymentInstructionService = paymentInstructionService;
        this.caseFeeDetailService = caseFeeDetailService;
        this.unallocatedAmountService = unallocatedAmountService;
    }

    @ApiOperation(value = "Get all current payment instructions", notes = "Get all current payment instructions for a given site.",
        produces = "application/json, text/csv")
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
			@RequestParam(name = "chequeNumber", required = false) String chequeNumber,
			@RequestParam(name = "postalOrderNumber", required = false) String postalOrderNumber,
			@RequestParam(name = "dailySequenceId", required = false) Integer dailySequenceId,
			@RequestParam(name = "allPayInstructionId", required = false) String allPayInstructionId,
			@RequestParam(name = "caseReference", required = false) String caseReference,
			@RequestParam(name = "paymentType", required = false) String paymentType,
			@RequestParam(name = "action", required = false) String action) {

		List<PaymentInstruction> paymentInstructionList = null;

		if (caseReference != null) {
			paymentInstructionList = paymentInstructionService.getAllPaymentInstructionsByCaseReference(caseReference);
		} else {
			PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto = PaymentInstructionSearchCriteriaDto
					.paymentInstructionSearchCriteriaDto().status(status)
					.startDate(startDate == null ? null : startDate.atStartOfDay())
					.endDate(endDate == null ? null : endDate.atTime(LocalTime.now())).payerName(payerName)
					.chequeNumber(chequeNumber).postalOrderNumer(postalOrderNumber).dailySequenceId(dailySequenceId)
					.allPayInstructionId(allPayInstructionId).paymentType(paymentType).action(action).build();

			paymentInstructionList = paymentInstructionService
					.getAllPaymentInstructions(paymentInstructionSearchCriteriaDto);
		}
		return Util.updateStatusAndActionDisplayValue(paymentInstructionList);
	}

    @ApiOperation(value = "Get all current payment instructions", notes = "Get all current payment instructions for a given site.",
        produces = "application/json, text/csv")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Return all current payment instructions for a given user"),
        @ApiResponse(code = 404, message = "Payment instructions not found"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/user/{idamId}/payment-instructions")
    public List<PaymentInstruction> getPaymentInstructionsByIdamId(
        @PathVariable("idamId") String idamId,
        @RequestParam(name = "status", required = false) String status,
        @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "ddMMyyyy") LocalDate startDate,
        @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "ddMMyyyy") LocalDate endDate,
        @RequestParam(name = "payerName", required = false) String payerName,
        @RequestParam(name = "chequeNumber", required = false) String chequeNumber,
        @RequestParam(name = "postalOrderNumber", required = false) String postalOrderNumber,
        @RequestParam(name = "dailySequenceId", required = false) Integer dailySequenceId,
        @RequestParam(name = "allPayInstructionId", required = false) String allPayInstructionId,
        @RequestParam(name = "caseReference", required = false) String caseReference,
        @RequestParam(name = "paymentType", required = false) String paymentType,
        @RequestParam(name = "action", required = false) String action) {

        // TODO: The filter by IdamId should be moved to criteria
        List<PaymentInstruction> paymentInstructions = getPaymentInstructions(status, startDate, endDate, payerName, chequeNumber,
            postalOrderNumber, dailySequenceId, allPayInstructionId, caseReference, paymentType, action);
        return paymentInstructions.stream().filter(paymentInstruction -> paymentInstruction.getBarUser().getIdamId().equals(idamId)).collect(Collectors.toList());
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

    @ApiOperation(value = "Create card payment instruction", notes = "Create card payment instruction with the given values.")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Card payment instruction created"),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/cards")
    public PaymentInstruction saveCardInstruction(
        @Valid @RequestBody Card card) {
        CardPaymentInstruction cardPaymentInstruction = cardPaymentInstructionWith()
            .payerName(card.getPayerName())
            .amount(card.getAmount())
            .currency(card.getCurrency())
            .status(card.getStatus())
            .authorizationCode(card.getAuthorizationCode())
            .build();
        return paymentInstructionService.createPaymentInstruction(cardPaymentInstruction);
    }

    @ApiOperation(value = "Update card payment instruction", notes = "Update card payment instruction with the given values.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Card payment instruction updated"),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/cards/{id}")
    public ResponseEntity<Void> updateCardInstruction(@PathVariable("id") Integer id , @ApiParam(value="Card request",required=true) @Valid @RequestBody Card card) {
        paymentInstructionService.updatePaymentInstruction(id,card);
        return new ResponseEntity<>(HttpStatus.OK);
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
            .status(cheque.getStatus())
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
            .status(cash.getStatus())
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
            .status(postalOrder.getStatus())
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
            .status(allPay.getStatus())
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
    public CaseReference saveCaseReference(
        @PathVariable("id") Integer id, @RequestBody CaseReferenceRequest caseReferenceRequest) {
        return paymentInstructionService.createCaseReference(id, caseReferenceRequest);
    }

    @ApiOperation(value = "Create case fee detail for a payment instruction", notes = "Create case fee detail for a payment instruction.")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Case fee detail for a payment instruction created"),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/payment-instructions/{id}/fees")
    public CaseFeeDetail saveCaseFeeDetail(@RequestBody CaseFeeDetailRequest caseFeeDetailRequest) {
        return caseFeeDetailService.saveCaseFeeDetail(caseFeeDetailRequest);
    }

	@ApiOperation(value = "Update fee details", notes = "Update fee details with the given values.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Fee details updated"),
			@ApiResponse(code = 400, message = "Bad request"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@ResponseStatus(HttpStatus.OK)
	@PutMapping("/fees/{feeId}")
	public CaseFeeDetail updateFeeDetail(@PathVariable("feeId") Integer feeId,
			@RequestBody CaseFeeDetailRequest caseFeeDetailRequest) {
		return caseFeeDetailService.updateCaseFeeDetail(feeId, caseFeeDetailRequest);
    }

    @ApiOperation(value = "Delete fee details", notes = "Delete fee details with the given values.")
    @ApiResponses(value = { @ApiResponse(code = 204, message = "Fee details deleted"),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 500, message = "Internal server error") })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/fees/{feeId}")
    public void deleteFeeDetail(@PathVariable("feeId") Integer feeId) {
        caseFeeDetailService.deleteCaseFeeDetail(feeId);
    }

    @ApiOperation(value = "Get the payment instruction", notes = "Get the payment instruction's unallocated amount for the given id.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Return payment unallocated amount"),
        @ApiResponse(code = 404, message = "Payment instruction not found"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/payment-instructions/{id}/unallocated")
    public int getUnallocatedPayment(@PathVariable("id") Integer paymentId){
        return unallocatedAmountService.calculateUnallocatedAmount(paymentId);
    }

}

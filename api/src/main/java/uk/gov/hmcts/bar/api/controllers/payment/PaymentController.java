package uk.gov.hmcts.bar.api.controllers.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.bar.api.contract.ErrorDto;
import uk.gov.hmcts.bar.api.contract.PaymentDto;
import uk.gov.hmcts.bar.api.contract.PaymentUpdateDto;
import uk.gov.hmcts.bar.api.contract.SearchDto;
import uk.gov.hmcts.bar.api.model.Payment;
import uk.gov.hmcts.bar.api.model.PaymentService;
import uk.gov.hmcts.bar.api.model.PaymentType;
import uk.gov.hmcts.bar.api.model.exceptions.PaymentNotFoundException;

import javax.validation.Valid;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;


@RestController
@Validated
public class PaymentController {

    private final PaymentDtoMapper paymentDtoMapper;
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentDtoMapper paymentDtoMapper, PaymentService paymentService) {
        this.paymentDtoMapper = paymentDtoMapper;
        this.paymentService = paymentService;
    }

    @GetMapping("/payments")
    public List<PaymentDto> getPayments(@RequestBody SearchDto searchDto){
          return paymentService.getPayments(searchDto.getUserId(),searchDto.getFromDate(),searchDto.getToDate())
              .stream().map(paymentDtoMapper::toPaymentDto).collect(toList());
    }

    @GetMapping("/payment-types")
    public List<PaymentType> getPaymentTypes(){
        return paymentService.findAllPaymentTypes();
    }

    @PostMapping("/payments")
    public PaymentDto savePayment(@Valid @RequestBody PaymentUpdateDto paymentUpdateDto){
        Payment payment = paymentService.savePayment(paymentDtoMapper.toPayment(paymentUpdateDto));
        return paymentDtoMapper.toPaymentDto(payment);
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity paymentNotFound() {
        return new ResponseEntity<>(new ErrorDto("payment: not found ."), BAD_REQUEST);
    }

}


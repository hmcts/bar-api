package uk.gov.hmcts.bar.api.controllers.Payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.bar.api.contract.ErrorDto;
import uk.gov.hmcts.bar.api.contract.PaymentDto;
import uk.gov.hmcts.bar.api.contract.SearchDto;
import uk.gov.hmcts.bar.api.model.Payment;
import uk.gov.hmcts.bar.api.model.PaymentRepository;
import uk.gov.hmcts.bar.api.model.exceptions.PaymentNotFoundException;

import javax.validation.Valid;
import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;


@RestController
@Validated
public class PaymentController {

    private final PaymentDtoMapper paymentDtoMapper;
    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentController(PaymentDtoMapper paymentDtoMapper, PaymentRepository paymentRepository) {
        this.paymentDtoMapper = paymentDtoMapper;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/payments")
    public List<PaymentDto> getPayments(@RequestBody SearchDto searchDto) throws ParseException {
          List<PaymentDto> payments = paymentRepository.findByPaymentDateBetween(searchDto.getFromDate().truncatedTo(ChronoUnit.DAYS),searchDto.getToDate()).stream().map(paymentDtoMapper::toPaymentDto).collect(toList());
          return payments;
    }


    @PostMapping("/payments")
    public PaymentDto createPayment(@Valid @RequestBody PaymentDto paymentDto){
        Payment payment = paymentRepository.save(paymentDtoMapper.toPayment(paymentDto));
        return paymentDtoMapper.toPaymentDto(payment);
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity paymentNotFound() {
        return new ResponseEntity<>(new ErrorDto("Payment: not found ."), BAD_REQUEST);
    }


}


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
import uk.gov.hmcts.bar.api.model.PaymentRepository;
import uk.gov.hmcts.bar.api.model.PaymentType;
import uk.gov.hmcts.bar.api.model.PaymentTypeRepository;
import uk.gov.hmcts.bar.api.model.exceptions.PaymentNotFoundException;

import javax.validation.Valid;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;


@RestController
@Validated
public class PaymentController {

    private final PaymentDtoMapper paymentDtoMapper;
    private final PaymentRepository paymentRepository;
    private final PaymentTypeRepository paymentTypeRepository;

    @Autowired
    public PaymentController(PaymentDtoMapper paymentDtoMapper, PaymentRepository paymentRepository, PaymentTypeRepository paymentTypeRepository) {
        this.paymentDtoMapper = paymentDtoMapper;
        this.paymentRepository = paymentRepository;
        this.paymentTypeRepository = paymentTypeRepository;
    }

    @GetMapping("/payments")
    public List<PaymentDto> getPayments(@RequestBody SearchDto searchDto){
          return paymentRepository.
              findByCreatedByUserIdAndPaymentDateBetween(searchDto.getUserId(),searchDto.getFromDate().truncatedTo(ChronoUnit.DAYS),searchDto.getToDate())
              .stream().map(paymentDtoMapper::toPaymentDto).collect(toList());
    }

    @GetMapping("/paymentTypes")
    public List<PaymentType> getPaymentTypes(){
       return paymentTypeRepository.findAll();
    }

    @PostMapping("/payments")
    public PaymentDto recordPayment(@Valid @RequestBody PaymentUpdateDto paymentUpdateDto){
        Payment payment = paymentRepository.save(paymentDtoMapper.toPayment(paymentUpdateDto));
        return paymentDtoMapper.toPaymentDto(payment);
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity paymentNotFound() {
        return new ResponseEntity<>(new ErrorDto("payment: not found ."), BAD_REQUEST);
    }


}


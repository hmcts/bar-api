package uk.gov.hmcts.bar.api.controllers.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.bar.api.contract.PaymentDto;
import uk.gov.hmcts.bar.api.contract.PaymentDto.CaseDto;
import uk.gov.hmcts.bar.api.contract.PaymentUpdateDto;
import uk.gov.hmcts.bar.api.controllers.service.ServiceDtoMapper;
import uk.gov.hmcts.bar.api.model.Case;
import uk.gov.hmcts.bar.api.model.Payment;
import uk.gov.hmcts.bar.api.model.SubServiceRepository;

import java.time.LocalDateTime;

import static java.util.stream.Collectors.toList;


@Component
public class PaymentDtoMapper {

    private final ServiceDtoMapper serviceDtoMapper;
    private final SubServiceRepository subServiceRepository;

    @Autowired
    public PaymentDtoMapper(ServiceDtoMapper serviceDtoMapper, SubServiceRepository subServiceRepository) {
        this.serviceDtoMapper = serviceDtoMapper;
        this.subServiceRepository = subServiceRepository;

    }

    public PaymentDto toPaymentDto(Payment payment) {

        return PaymentDto.paymentDtoWith()
            .amount(payment.getAmount())
            .sortCode(payment.getSortCode())
            .accountNumber(payment.getAccountNumber())
            .chequeNumber(payment.getChequeNumber())
            .counterCode(payment.getCounterCode())
            .currencyType(payment.getCurrencyType())
            .eventType(payment.getEventType())
            .feeCode(payment.getFeeCode())
            .paymentReceiptType(payment.getPaymentReceiptType())
            .payeeName(payment.getPayeeName())
            .paymentType(payment.getPaymentType())
            .paymentDate(String.valueOf(payment.getPaymentDate()))
            .updateDate(String.valueOf(payment.getUpdateDate()))
            .createdByUserId(payment.getCreatedByUserId())
            .updatedByUserId(payment.getUpdatedByUserId())
            .cases(payment.getCases().stream().map(this::toCaseDto).collect(toList()))
            .build();
    }

    private CaseDto toCaseDto(Case caseObject){
        return PaymentDto.CaseDto.caseDtoWith()
            .reference(caseObject.getReference())
            .jurisdiction1(caseObject.getJurisdiction1())
            .jurisdiction2(caseObject.getJurisdiction2())
            .subService(serviceDtoMapper.toSubServiceDto(caseObject.getSubService()))
            .build();
    }



    public Payment toPayment(PaymentDto dto) {
        return Payment.paymentWith()
            .amount(dto.getAmount())
            .sortCode(dto.getSortCode())
            .accountNumber(dto.getAccountNumber())
            .chequeNumber(dto.getChequeNumber())
            .counterCode(dto.getCounterCode())
            .currencyType(dto.getCurrencyType())
            .eventType(dto.getEventType())
            .feeCode(dto.getFeeCode())
            .paymentReceiptType(dto.getPaymentReceiptType())
            .payeeName(dto.getPayeeName())
            .paymentType(dto.getPaymentType())
            .paymentDate(LocalDateTime.parse(dto.getPaymentDate()))
            .updateDate(dto.getUpdateDate()!=null ? LocalDateTime.parse(dto.getUpdateDate()): null)
            .createdByUserId(dto.getCreatedByUserId())
            .updatedByUserId(dto.getUpdatedByUserId())
            .cases(dto.getCases().stream().map(this::toCase).collect(toList()))
            .build();
    }



    private Case toCase(CaseDto caseDto){
        return Case.caseWith()
            .reference(caseDto.getReference())
            .jurisdiction1(caseDto.getJurisdiction1())
            .jurisdiction2(caseDto.getJurisdiction2())
            .subService(serviceDtoMapper.toSubService(caseDto.getSubService()))
            .build();
    }


    public Payment toPayment(PaymentUpdateDto dto) {
        return Payment.paymentWith()
            .amount(dto.getAmount())
            .sortCode(dto.getSortCode())
            .accountNumber(dto.getAccountNumber())
            .chequeNumber(dto.getChequeNumber())
            .counterCode(dto.getCounterCode())
            .currencyType(dto.getCurrencyType())
            .eventType(dto.getEventType())
            .feeCode(dto.getFeeCode())
            .paymentReceiptType(dto.getPaymentReceiptType())
            .payeeName(dto.getPayeeName())
            .paymentType(dto.getPaymentType())
            .paymentDate(LocalDateTime.parse(dto.getPaymentDate()))
            .updateDate(dto.getUpdateDate()!=null ? LocalDateTime.parse(dto.getUpdateDate()): null)
            .createdByUserId(dto.getCreatedByUserId())
            .updatedByUserId(dto.getUpdatedByUserId())
            .cases(dto.getCases().stream().map(this::toCase).collect(toList()))
            .build();
    }

    private Case toCase(PaymentUpdateDto.CaseUpdateDto caseUpdateDto){
        return Case.caseWith()
            .reference(caseUpdateDto.getReference())
            .jurisdiction1(caseUpdateDto.getJurisdiction1())
            .jurisdiction2(caseUpdateDto.getJurisdiction2())
            .subService(subServiceRepository.findOne(Integer.parseInt(caseUpdateDto.getSubServiceId())))
            .build();
    }





}


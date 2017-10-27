package uk.gov.hmcts.bar.api.controllers.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.bar.api.contract.PaymentDto;
import uk.gov.hmcts.bar.api.contract.PaymentDto.CaseDto;
import uk.gov.hmcts.bar.api.contract.PaymentDto.PaymentTypeDto;
import uk.gov.hmcts.bar.api.contract.PaymentUpdateDto;
import uk.gov.hmcts.bar.api.controllers.service.ServiceDtoMapper;
import uk.gov.hmcts.bar.api.model.*;

import java.time.LocalDateTime;

import static java.util.stream.Collectors.toList;


@Component
public class PaymentDtoMapper {

    private final ServiceDtoMapper serviceDtoMapper;
    private final SubServiceRepository subServiceRepository;
    private final PaymentService paymentService;

    @Autowired
    public PaymentDtoMapper(ServiceDtoMapper serviceDtoMapper, SubServiceRepository subServiceRepository, PaymentService paymentService) {
        this.serviceDtoMapper = serviceDtoMapper;
        this.subServiceRepository = subServiceRepository;
        this.paymentService = paymentService;
    }

    public PaymentDto toPaymentDto(Payment payment) {

        return PaymentDto.paymentDtoWith()
            .amount(payment.getAmount())
            .sortCode(payment.getSortCode())
            .accountNumber(payment.getAccountNumber())
            .chequeNumber(payment.getChequeNumber())
            .counterCode(payment.getCounterCode())
            .currency(payment.getCurrency())
            .eventType(payment.getEventType())
            .feeCode(payment.getFeeCode())
            .paymentReceiptType(payment.getPaymentReceiptType())
            .payeeName(payment.getPayeeName())
            .paymentType(toPaymentTypeDto(payment.getPaymentType()))
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
            .currency(dto.getCurrency())
            .eventType(dto.getEventType())
            .feeCode(dto.getFeeCode())
            .paymentReceiptType(dto.getPaymentReceiptType())
            .payeeName(dto.getPayeeName())
            .paymentType(toPaymentType(dto.getPaymentType()))
            .paymentDate(LocalDateTime.now())
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
            .currency(dto.getCurrency())
            .eventType(dto.getEventType())
            .feeCode(dto.getFeeCode())
            .paymentReceiptType(dto.getPaymentReceiptType())
            .payeeName(dto.getPayeeName())
            .paymentType(toPaymentType(dto.getPaymentTypeId()))
            .paymentDate(LocalDateTime.now())
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
            .subService(subServiceRepository.findOne(caseUpdateDto.getSubServiceId()))
            .build();
    }

    public PaymentType toPaymentType(PaymentTypeDto paymentTypeDto){
        return PaymentType.paymentTypeWith()
            .id(paymentTypeDto.getId())
            .name(paymentTypeDto.getName())
            .build();
    }

    public PaymentTypeDto toPaymentTypeDto(PaymentType paymentType){
        return PaymentTypeDto.paymentTypeDtoWith()
            .id(paymentType.getId())
            .name(paymentType.getName())
            .build();

    }


    public PaymentType toPaymentType(Integer paymentTypeId){
        return paymentService.findPaymentType(paymentTypeId);

    }

}


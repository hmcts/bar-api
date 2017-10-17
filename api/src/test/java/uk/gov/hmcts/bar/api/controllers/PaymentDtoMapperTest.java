package uk.gov.hmcts.bar.api.controllers;

import org.junit.Test;
import uk.gov.hmcts.bar.api.contract.PaymentDto;
import uk.gov.hmcts.bar.api.contract.PaymentDto.CaseDto;
import uk.gov.hmcts.bar.api.contract.ServiceDto;
import uk.gov.hmcts.bar.api.controllers.payment.PaymentDtoMapper;
import uk.gov.hmcts.bar.api.controllers.service.ServiceDtoMapper;
import uk.gov.hmcts.bar.api.model.Case;
import uk.gov.hmcts.bar.api.model.Payment;
import uk.gov.hmcts.bar.api.model.SubService;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentDtoMapperTest {
    private final PaymentDtoMapper paymentDtoMapper = new PaymentDtoMapper(new ServiceDtoMapper(){
        @Override
        public ServiceDto.SubServiceDto toSubServiceDto(SubService subService){return MAPPED_SUB_SERVICE_DTO;}
    },null);
    private static final SubService ANY_SUB_SERVICE = SubService.subServiceWith().name("County").build();
    private static final ServiceDto.SubServiceDto MAPPED_SUB_SERVICE_DTO = new ServiceDto.SubServiceDto(1, "County");
    @Test
    public void givenPayment_convertedToPaymentDto() {
        assertThat(paymentDtoMapper.toPaymentDto(
            Payment.paymentWith()
                .payeeName("Mr Tony Dowds")
                .sortCode("000000")
                .chequeNumber("000000")
                .accountNumber("00000000")
                .paymentReceiptType("post")
                .paymentType(1)
                .feeCode("X0001")
                .eventType("someevent")
                .counterCode("somecounter")
                .currency("GBP")
                .paymentDate(LocalDateTime.parse("2017-09-14T10:11:30"))
                .updateDate(LocalDateTime.parse("2017-09-14T10:11:30"))
                .amount(500)
                .createdByUserId("user01")
                .updatedByUserId("user01")
                .cases(Arrays.asList(Case.caseWith().jurisdiction1("one").jurisdiction2("two").reference("case_1").subService(ANY_SUB_SERVICE).build()))
                .build()
        )
        ).isEqualToComparingOnlyGivenFields(
            PaymentDto.paymentDtoWith()
                .payeeName("Mr Tony Dowds")
                .sortCode("000000")
                .chequeNumber("000000")
                .accountNumber("00000000")
                .paymentReceiptType("post")
                .paymentType(1)
                .feeCode("X0001")
                .eventType("someevent")
                .counterCode("somecounter")
                .currency("GBP")
                .updateDate("2017-09-14T10:11:30")
                .amount(500)
                .createdByUserId("user01")
                .updatedByUserId("user01")
                .cases(Arrays.asList(CaseDto.caseDtoWith().jurisdiction1("one").jurisdiction2("two").reference("case_1").subService(MAPPED_SUB_SERVICE_DTO).build()))
                .build());
    }


    @Test
    public void givenPaymentDto_convertedToPayment() {
        assertThat(paymentDtoMapper.toPayment(
            PaymentDto.paymentDtoWith()
                .payeeName("Mr Tony Dowds")
                .sortCode("000000")
                .chequeNumber("000000")
                .accountNumber("00000000")
                .paymentReceiptType("post")
                .paymentType(1)
                .feeCode("X0001")
                .eventType("someevent")
                .counterCode("somecounter")
                .currency("GBP")
                .updateDate("2017-09-14T10:11:30")
                .amount(500)
                .createdByUserId("user01")
                .updatedByUserId("user01")
                .cases(Arrays.asList(PaymentDto.CaseDto.caseDtoWith().jurisdiction1("one").jurisdiction2("two").reference("case_1").subService(MAPPED_SUB_SERVICE_DTO).build()))
                .build()
            )
        ).isEqualToComparingOnlyGivenFields(
            Payment.paymentWith()
                .payeeName("Mr Tony Dowds")
                .sortCode("000000")
                .chequeNumber("000000")
                .accountNumber("00000000")
                .paymentReceiptType("post")
                .paymentType(1)
                .feeCode("X0001")
                .eventType("someevent")
                .counterCode("somecounter")
                .currency("GBP")
                .paymentDate(LocalDateTime.parse("2017-09-14T10:11:30"))
                .updateDate(LocalDateTime.parse("2017-09-14T10:11:30"))
                .amount(500)
                .createdByUserId("user01")
                .updatedByUserId("user01")
                .cases(Arrays.asList(Case.caseWith().jurisdiction1("one").jurisdiction2("two").reference("case_1").subService(ANY_SUB_SERVICE).build()))
                .build());
    }
}

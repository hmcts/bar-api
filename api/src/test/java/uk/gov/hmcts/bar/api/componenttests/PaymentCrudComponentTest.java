package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;
import uk.gov.hmcts.bar.api.contract.PaymentDto;
import uk.gov.hmcts.bar.api.contract.PaymentUpdateDto;
import uk.gov.hmcts.bar.api.contract.ServiceDto;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.bar.api.contract.PaymentDto.paymentDtoWith;
import static uk.gov.hmcts.bar.api.contract.PaymentUpdateDto.CaseUpdateDto.caseUpdateDtoWith;
import static uk.gov.hmcts.bar.api.contract.PaymentUpdateDto.paymentUpdateDtoWith;
public class PaymentCrudComponentTest extends ComponentTestBase {

    @Test
    public void givenCashAmount_createPostPayment() throws Exception {
        PaymentUpdateDto.PaymentUpdateDtoBuilder proposedPayment = paymentUpdateDtoWith()
            .payeeName("Mr Payer Payer")
            .amount(500)
            .paymentReceiptType("post")
            .paymentTypeId(1)
            .feeCode("X0001")
            .eventType("someevent")
            .currency("GBP")
            .updateDate("2017-10-10T00:00:00")
            .updatedByUserId("user01")
            .cases(Arrays.asList(
                caseUpdateDtoWith().jurisdiction1("one").jurisdiction2("two").reference("case1").subServiceId(1).build())
            )
            .createdByUserId("user01");

        restActions
            .withUser("admin")
            .post("/payments", proposedPayment.build())
            .andExpect(status().isOk())
            .andExpect(body().as(PaymentDto.class, paymentDto -> {
                assertThat(paymentDto).isEqualToComparingOnlyGivenFields(
                    paymentDtoWith()
                        .payeeName("Mr Payer Payer")
                        .amount(500)
                        .paymentReceiptType("post")
                        .paymentType(PaymentDto.PaymentTypeDto.paymentTypeDtoWith().id(1).name("Cheque").build())
                        .feeCode("X0001")
                        .eventType("someevent")
                        .currency("GBP")
                        .updateDate("2017-10-10T00:00:00")
                        .updatedByUserId("user01")
                        .cases(Arrays.asList(
                            PaymentDto.CaseDto.caseDtoWith().jurisdiction1("one").jurisdiction2("two").reference("case1").subService(ServiceDto.SubServiceDto.subServiceDtoWith().name("County").build()).build()
                        ))
                        .createdByUserId("user01"));
            }));
    }


    @Test
    public void givenChequeAmount_createPostPayment() throws Exception {
        PaymentUpdateDto.PaymentUpdateDtoBuilder proposedPayment = paymentUpdateDtoWith()
            .payeeName("Mr Payer Payer")
            .amount(500)
            .paymentReceiptType("post")
            .chequeNumber("000000")
            .sortCode("000000")
            .accountNumber("00000000")
            .paymentTypeId(1)
            .feeCode("X0001")
            .eventType("someevent")
            .currency("GBP")
            .updateDate("2017-10-10T00:00:00")
            .updatedByUserId("user01")
            .cases(Arrays.asList(
                caseUpdateDtoWith().jurisdiction1("one").jurisdiction2("two").reference("case1").subServiceId(1).build())
            )
            .createdByUserId("user01");

        restActions
            .withUser("admin")
            .post("/payments", proposedPayment.build())
            .andExpect(status().isOk())
            .andExpect(body().as(PaymentDto.class, paymentDto -> {
                assertThat(paymentDto).isEqualToComparingOnlyGivenFields(
                    paymentDtoWith()
                        .payeeName("Mr Payer Payer")
                        .amount(500)
                        .chequeNumber("000000")
                        .sortCode("000000")
                        .accountNumber("00000000")
                        .paymentReceiptType("post")
                        .paymentType(PaymentDto.PaymentTypeDto.paymentTypeDtoWith().id(1).name("Cheque").build())
                        .feeCode("X0001")
                        .eventType("someevent")
                        .currency("GBP")
                        .updateDate("2017-10-10T00:00:00")
                        .updatedByUserId("user01")
                        .cases(Arrays.asList(
                            PaymentDto.CaseDto.caseDtoWith().jurisdiction1("one").jurisdiction2("two").reference("case1").subService(ServiceDto.SubServiceDto.subServiceDtoWith().name("County").build()).build()
                        ))
                        .createdByUserId("user01"));
            }));
    }

    @Test
    public void givenCashAmount_createCounterPayment() throws Exception {
        PaymentUpdateDto.PaymentUpdateDtoBuilder proposedPayment = paymentUpdateDtoWith()
            .payeeName("Mr Payer Payer")
            .amount(500)
            .paymentReceiptType("counter")
            .counterCode("C001")
            .paymentTypeId(1)
            .feeCode("X0001")
            .eventType("someevent")
            .currency("GBP")
            .updateDate("2017-10-10T00:00:00")
            .updatedByUserId("user01")
            .cases(Arrays.asList(
                caseUpdateDtoWith().jurisdiction1("one").jurisdiction2("two").reference("case1").subServiceId(1).build())
            )
            .createdByUserId("user01");

        restActions
            .withUser("admin")
            .post("/payments", proposedPayment.build())
            .andExpect(status().isOk())
            .andExpect(body().as(PaymentDto.class, paymentDto -> {
                assertThat(paymentDto).isEqualToComparingOnlyGivenFields(
                    paymentDtoWith()
                        .payeeName("Mr Payer Payer")
                        .amount(500)
                        .paymentReceiptType("counter")
                        .counterCode("C001")
                        .paymentType(PaymentDto.PaymentTypeDto.paymentTypeDtoWith().id(1).name("Cheque").build())
                        .feeCode("X0001")
                        .eventType("someevent")
                        .currency("GBP")
                        .cases(Arrays.asList(
                            PaymentDto.CaseDto.caseDtoWith().jurisdiction1("one").jurisdiction2("two").reference("case1").subService(ServiceDto.SubServiceDto.subServiceDtoWith().name("County").build()).build()
                        ))
                        .createdByUserId("user01"));
            }));
    }

    @Test
    public void givenChequeAmount_createCounterPayment() throws Exception {
        PaymentUpdateDto.PaymentUpdateDtoBuilder proposedPayment = paymentUpdateDtoWith()
            .payeeName("Mr Payer Payer")
            .amount(500)
            .chequeNumber("000000")
            .sortCode("000000")
            .accountNumber("00000000")
            .paymentReceiptType("counter")
            .counterCode("C001")
            .paymentTypeId(1)
            .feeCode("X0001")
            .eventType("someevent")
            .currency("GBP")
            .updateDate("2017-10-10T00:00:00")
            .updatedByUserId("user01")
            .cases(Arrays.asList(
                caseUpdateDtoWith().jurisdiction1("one").jurisdiction2("two").reference("case1").subServiceId(1).build())
            )
            .createdByUserId("user01");

        restActions
            .withUser("admin")
            .post("/payments", proposedPayment.build())
            .andExpect(status().isOk())
            .andExpect(body().as(PaymentDto.class, paymentDto -> {
                assertThat(paymentDto).isEqualToComparingOnlyGivenFields(
                    paymentDtoWith()
                        .payeeName("Mr Payer Payer")
                        .amount(500)
                        .chequeNumber("000000")
                        .sortCode("000000")
                        .accountNumber("00000000")
                        .paymentReceiptType("counter")
                        .counterCode("C001")
                        .paymentType(PaymentDto.PaymentTypeDto.paymentTypeDtoWith().id(1).name("Cheque").build())
                        .feeCode("X0001")
                        .eventType("someevent")
                        .currency("GBP")
                        .updateDate("2017-10-10T00:00:00")
                        .updatedByUserId("user01")
                        .cases(Arrays.asList(
                            PaymentDto.CaseDto.caseDtoWith().jurisdiction1("one").jurisdiction2("two").reference("case1").subService(ServiceDto.SubServiceDto.subServiceDtoWith().name("County").build()).build()
                        ))
                        .createdByUserId("user01"));
            }));

    }


    @Test
    public void givenCashAmountZero_createPostPayment() throws Exception {
        PaymentUpdateDto.PaymentUpdateDtoBuilder proposedPayment = paymentUpdateDtoWith()
            .payeeName("Mr Payer Payer")
            .amount(0)
            .paymentReceiptType("post")
            .paymentTypeId(2)
            .feeCode("X0001")
            .eventType("someevent")
            .currency("GBP")
            .updateDate("2017-10-10T00:00:00")
            .updatedByUserId("user01")
            .cases(Arrays.asList(
                caseUpdateDtoWith().jurisdiction1("one").jurisdiction2("two").reference("case1").subServiceId(1).build())
            )
            .createdByUserId("user01");

        restActions
            .withUser("admin")
            .post("/payments", proposedPayment.build())
            .andExpect(status().isOk())
            .andExpect(body().as(PaymentDto.class, paymentDto -> {
                assertThat(paymentDto).isEqualToComparingOnlyGivenFields(
                    paymentDtoWith()
                    .payeeName("Mr Payer Payer")
                    .amount(0)
                    .paymentReceiptType("post")
                    .paymentType(PaymentDto.PaymentTypeDto.paymentTypeDtoWith().id(2).name("Cash").build())
                    .feeCode("X0001")
                    .eventType("someevent")
                    .currency("GBP")
                    .updateDate("2017-10-10T00:00:00")
                    .updatedByUserId("user01")
                    .cases(Arrays.asList(
                        PaymentDto.CaseDto.caseDtoWith().jurisdiction1("one").jurisdiction2("two").reference("case1").subService(ServiceDto.SubServiceDto.subServiceDtoWith().name("County").build()).build()
                    ))
                    .createdByUserId("user01"));

            }));
    }

}



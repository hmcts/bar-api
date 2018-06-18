package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;
import uk.gov.hmcts.bar.api.data.enums.PaymentActionEnum;
import uk.gov.hmcts.bar.api.data.model.AllPay;
import uk.gov.hmcts.bar.api.data.model.AllPayPaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.CaseFeeDetailRequest;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionUpdateRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.bar.api.data.model.AllPay.allPayPaymentInstructionRequestWith;
import static uk.gov.hmcts.bar.api.data.model.AllPayPaymentInstruction.allPayPaymentInstructionWith;
import static uk.gov.hmcts.bar.api.data.model.PaymentInstructionUpdateRequest.paymentInstructionUpdateRequestWith;

public class AllPayInstructionCrudComponentTest extends ComponentTestBase {


    @Test
    public void whenAllPayPaymentInstructionDetails_thenCreateAllPayPaymentInstruction() throws Exception {
        AllPay proposedAllPayPaymentInstructionRequest = allPayPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .status("D")
            .allPayTransactionId("12345").build();

        restActions
            .post("/allpay", proposedAllPayPaymentInstructionRequest)
            .andExpect(status().isCreated())
            .andExpect(body().as(AllPayPaymentInstruction.class, allPayPaymentInstruction-> {
                assertThat(allPayPaymentInstruction).isEqualToComparingOnlyGivenFields(
                    allPayPaymentInstructionWith()
                        .payerName("Mr Payer Payer")
                        .amount(500)
                        .currency("GBP").status("D").allPayTransactionId("12345").build());
            }));
    }

    @Test
    public void whenAllPayPaymentInstructionWithInvalidAllPayTransactionId_thenReturn400() throws Exception {
        AllPay proposedAllPayPaymentInstructionRequest = allPayPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .allPayTransactionId("abcd").build();

        restActions
            .post("/allpay", proposedAllPayPaymentInstructionRequest)
            .andExpect(status().isBadRequest());
    }


    @Test
    public void whenAllPayPaymentInstructionWithInvalidCurrency_thenReturn400() throws Exception {
        AllPay proposedAllPayPaymentInstructionRequest = allPayPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("XXX")
            .allPayTransactionId("12345").build();

        restActions
            .post("/allpay", proposedAllPayPaymentInstructionRequest)
            .andExpect(status().isBadRequest())
        ;
    }


    @Test
    public void givenAllPayPaymentInstructionDetails_retrieveThem() throws Exception {
        AllPay proposedAllPayPaymentInstructionRequest = allPayPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .status("D")
            .allPayTransactionId("12345").build();

        AllPayPaymentInstruction retrievedAllPayPaymentInstruction = allPayPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .status("D")
            .allPayTransactionId("12345").build();


        restActions
            .post("/allpay", proposedAllPayPaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .get("/payment-instructions")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, (allPayList) -> {
                assertThat(allPayList.get(0).equals(retrievedAllPayPaymentInstruction));
            }));

    }

    @Test
    public void givenAllPayPaymentInstructionDetails_retrieveOneOfThem() throws Exception {
        AllPay proposedAllPayPaymentInstructionRequest = allPayPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer").amount(500).currency("GBP").status("D").allPayTransactionId("12345").build();

        restActions.post("/allpay", proposedAllPayPaymentInstructionRequest).andExpect(status().isCreated());

        restActions.get("/payment-instructions/1").andExpect(status().isOk())
            .andExpect(body().as(AllPayPaymentInstruction.class, (pi) -> {
                assertThat(pi.getAmount() == 500);
            }));
    }

    @Test
    public void givenAllPayPaymentInstructionDetails_retrieveOneOfThemWithWrongId() throws Exception {
        AllPay proposedAllPayPaymentInstructionRequest = allPayPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer").amount(500).currency("GBP").allPayTransactionId("12345").status("D").build();

        restActions.post("/allpay", proposedAllPayPaymentInstructionRequest).andExpect(status().isCreated());

        restActions.get("/payment-instructions/2").andExpect(status().isNotFound());
    }

    @Test
    public void whenAllPayPaymentInstructionIsDeleted_expectStatus_204() throws Exception {
        AllPay proposedAllPayPaymentInstructionRequest = allPayPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .status("D")
            .allPayTransactionId("12345").build();

        restActions
            .post("/allpay",  proposedAllPayPaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .delete("/payment-instructions/1")
            .andExpect(status().isNoContent());


    }

    @Test
    public void whenNonExistingAllPayPaymentInstructionIsDeleted_expectStatus_204() throws Exception {
        AllPay proposedAllPayPaymentInstructionRequest = allPayPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .status("D")
            .allPayTransactionId("12345").build();

        restActions
            .post("/allpay",  proposedAllPayPaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .delete("/payment-instructions/1000")
            .andExpect(status().isNotFound());

    }


    @Test
    public void whenAllPayPaymentInstructionIsSubmittedByPostClerk_expectStatus_200() throws Exception {
        AllPay proposedAllPayPaymentInstructionRequest = allPayPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .status("D")
            .allPayTransactionId("12345").build();

        PaymentInstructionUpdateRequest request= paymentInstructionUpdateRequestWith()
            .status("P").build();

        restActions
            .post("/allpay",  proposedAllPayPaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .put("/payment-instructions/1",request)
            .andExpect(status().isOk());
    }

    @Test
    public void whenNonExistingAllPayPaymentInstructionIsSubmittedByPostClerk_expectStatus_404() throws Exception {
        AllPay proposedAllPayPaymentInstructionRequest = allPayPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .status("D")
            .allPayTransactionId("12345").build();

        PaymentInstructionUpdateRequest request= paymentInstructionUpdateRequestWith()
            .status("P").build();

        restActions
            .post("/allpay",  proposedAllPayPaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .put("/payment-instructions/1000",request)
            .andExpect(status().isNotFound());
    }


    @Test
    public void whenCaseFeeDetailForAllPayPaymentInstructionIsCreated_expectStatus_201() throws Exception {
        AllPay proposedAllPayPaymentInstructionRequest = allPayPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .status("D")
            .allPayTransactionId("12345").build();

        CaseFeeDetailRequest caseFeeDetailRequest = CaseFeeDetailRequest.caseFeeDetailRequestWith()
            .caseReference("case102")
            .feeCode("X001")
            .amount(200)
            .feeVersion("1")
            .build();

        restActions
            .post("/allpay",  proposedAllPayPaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .post("/fees",caseFeeDetailRequest)
            .andExpect(status().isCreated());


    }

    @Test
    public void whenInvalidCaseReferenceForAllPayPaymentInstructionIsCreated_expectStatus_400() throws Exception {
        AllPay proposedAllPayPaymentInstructionRequest = allPayPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .status("D")
            .allPayTransactionId("12345").build();

        CaseFeeDetailRequest caseFeeDetailRequest = CaseFeeDetailRequest.caseFeeDetailRequestWith()
            .caseReference("<><<>><>")
            .build();

        restActions
            .post("/allpay",  proposedAllPayPaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .post("/fees",caseFeeDetailRequest)
            .andExpect(status().isBadRequest());


    }

    @Test
    public void whenSearchAllPayPaymentInstructionByPayerName_expectStatus_200() throws Exception {
        AllPay proposedAllPayPaymentInstructionRequest = allPayPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .status("D")
            .allPayTransactionId("12345").build();

        restActions
            .post("/allpay",  proposedAllPayPaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .get("/payment-instructions?payerName=Mr Payer Payer")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, allPayPaymentInstructionList-> {
                assertThat(allPayPaymentInstructionList.get(0)).isEqualToComparingOnlyGivenFields(
                    allPayPaymentInstructionWith()
                        .payerName("Mr Payer Payer")
                        .amount(500)
                        .status("D")
                        .currency("GBP").allPayTransactionId("12345").build());
            }));
    }


    @Test
    public void whenSearchNonExistingAllPayPaymentInstructionByPayerName_expectStatus_200AndEmptyList() throws Exception {
        AllPay proposedAllPayPaymentInstructionRequest = allPayPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .status("D")
            .allPayTransactionId("12345").build();

        restActions
            .post("/allpay",  proposedAllPayPaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .get("/payment-instructions?payerName=NonExisting")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, allPayPaymentInstructionList-> assertTrue(allPayPaymentInstructionList.isEmpty())));

    }


    @Test
    public void whenAllPayPaymentInstructionIsUpdated_expectStatus_200() throws Exception {
        AllPay proposedAllPayPaymentInstructionRequest = allPayPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .status("D")
            .allPayTransactionId("12345").build();


        AllPay updatedAllPayPaymentInstructionRequest = allPayPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(6000)
            .currency("GBP")
            .status("D")
            .allPayTransactionId("12345").build();


        restActions
            .post("/allpay",  proposedAllPayPaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .put("/allpay/1",updatedAllPayPaymentInstructionRequest)
            .andExpect(status().isOk());

    }
    @Test
    public void whenNonExistingAllPayPaymentInstructionIsUpdated_expectStatus_404() throws Exception {
        AllPay proposedAllPayPaymentInstructionRequest = allPayPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .status("D")
            .allPayTransactionId("12345").build();


        AllPay updatedAllPayPaymentInstructionRequest = allPayPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(6000)
            .currency("GBP")
            .status("D")
            .allPayTransactionId("12345").build();


        restActions
            .post("/allpay",  proposedAllPayPaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .put("/allpay/1000",updatedAllPayPaymentInstructionRequest)
            .andExpect(status().isNotFound());

    }

    @Test
    public void updatePaymentInstructionAction() throws Exception {

        Integer[] savedId = null;

        AllPay proposedAllPayPaymentInstructionRequest = allPayPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .status("D")
            .allPayTransactionId("12345").build();

        restActions
            .post("/allpay",  proposedAllPayPaymentInstructionRequest)
            .andExpect(status().isCreated());

        PaymentInstructionUpdateRequest updatedActionToProcessRequest = PaymentInstructionUpdateRequest.paymentInstructionUpdateRequestWith()
            .status("P")
            .action(PaymentActionEnum.PROCESS.displayValue()).build();

        restActions
            .put("/payment-instructions/1", updatedActionToProcessRequest)
            .andExpect(status().isOk());

        PaymentInstructionUpdateRequest updatedActionReturnRequest = PaymentInstructionUpdateRequest.paymentInstructionUpdateRequestWith()
            .status("P")
            .action(PaymentActionEnum.RETURN.displayValue()).build();

        restActions
            .put("/payment-instructions/1", updatedActionReturnRequest)
            .andExpect(status().isBadRequest());

        PaymentInstructionUpdateRequest updatedActionSuspenseDefRequest = PaymentInstructionUpdateRequest.paymentInstructionUpdateRequestWith()
            .status("P")
            .action(PaymentActionEnum.SUSPENSE_DEFICIENCY.displayValue()).build();

        restActions
            .put("/payment-instructions/1", updatedActionSuspenseDefRequest)
            .andExpect(status().isOk());
    }

}

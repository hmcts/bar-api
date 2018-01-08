package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;
import uk.gov.hmcts.bar.api.data.model.CaseReference;
import uk.gov.hmcts.bar.api.data.model.Cheque;
import uk.gov.hmcts.bar.api.data.model.ChequePaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionUpdateRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.bar.api.data.model.CaseReference.caseReferenceWith;
import static uk.gov.hmcts.bar.api.data.model.Cheque.chequePaymentInstructionRequestWith;
import static uk.gov.hmcts.bar.api.data.model.ChequePaymentInstruction.chequePaymentInstructionWith;
import static uk.gov.hmcts.bar.api.data.model.PaymentInstructionUpdateRequest.paymentInstructionUpdateRequestWith;
public class ChequeInstructionCrudComponentTest extends ComponentTestBase {


    @Test
    public void whenChequeInstructionDetails_thenCreateChequePaymentInstruction() throws Exception {
        Cheque proposedChequePaymentInstructionRequest =chequePaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .chequeNumber("000000").build();

        restActions
            .post("/cheques", proposedChequePaymentInstructionRequest)
            .andExpect(status().isCreated())
            .andExpect(body().as(ChequePaymentInstruction.class, chequePaymentInstruction -> {
                assertThat(chequePaymentInstruction).isEqualToComparingOnlyGivenFields(
                    chequePaymentInstructionWith()
                        .payerName("Mr Payer Payer")
                        .amount(500)
                        .currency("GBP")
                        .chequeNumber("000000"));
            }));
    }

    @Test
    public void whenChequeInstructionWithInvalidChequeNumber_thenReturn400() throws Exception {
        Cheque proposedChequePaymentInstructionRequest = chequePaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .chequeNumber("xxxxxx").build();

        restActions
            .post("/cheques", proposedChequePaymentInstructionRequest)
            .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void whenChequeInstructionWithInvalidCurrency_thenReturn400() throws Exception {
        Cheque proposedChequePaymentInstructionRequest =chequePaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("xxx")
            .chequeNumber("000000").build();

        restActions
            .post("/cheques", proposedChequePaymentInstructionRequest)
            .andExpect(status().isBadRequest())
        ;
    }



    @Test
    public void givenChequePaymentInstructionDetails_retrieveThem() throws Exception {
        Cheque proposedChequePaymentInstructionRequest =chequePaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .chequeNumber("000000").build();

        ChequePaymentInstruction retrievedChequePaymentInstruction =chequePaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .chequeNumber("000000").build();


        restActions
            .post("/cheques",  proposedChequePaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .get("/payment-instructions")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, (chequesList) -> {
                assertThat(chequesList.get(0).equals(retrievedChequePaymentInstruction));
            }));

    }

	@Test
	public void givenChequePaymentInstructionDetails_retrieveOneOfThem() throws Exception {
		Cheque proposedChequePaymentInstructionRequest = chequePaymentInstructionRequestWith()
				.payerName("Mr Payer Payer").amount(500).currency("GBP").chequeNumber("000000").build();

		restActions.post("/cheques", proposedChequePaymentInstructionRequest).andExpect(status().isCreated());

		restActions.get("/payment-instructions/1").andExpect(status().isOk())
				.andExpect(body().as(ChequePaymentInstruction.class, (pi) -> {
					assertThat(pi.getAmount() == 500);
				}));
	}

	@Test
	public void givenChequePaymentInstructionDetails_retrieveOneOfThemWithWrongId() throws Exception {
		Cheque proposedChequePaymentInstructionRequest = chequePaymentInstructionRequestWith()
				.payerName("Mr Payer Payer").amount(500).currency("GBP").chequeNumber("000000").build();

		restActions.post("/cheques", proposedChequePaymentInstructionRequest).andExpect(status().isCreated());

		restActions.get("/payment-instructions/2").andExpect(status().isNotFound());
	}

    @Test
    public void whenChequePaymentInstructionIsDeleted_expectStatus_204() throws Exception {
        Cheque proposedChequePaymentInstructionRequest = chequePaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .chequeNumber("000000").build();

        restActions
            .post("/cheques",  proposedChequePaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .delete("/payment-instructions/1")
            .andExpect(status().isNoContent());


    }

    @Test
    public void whenNonExistingChequePaymentInstructionIsDeleted_expectStatus_204() throws Exception {
        Cheque proposedChequePaymentInstructionRequest =chequePaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .chequeNumber("000000").build();

        restActions
            .post("/cheques",  proposedChequePaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .delete("/payment-instructions/1000")
            .andExpect(status().isNotFound());


    }


    @Test
    public void whenChequePaymentInstructionIsSubmitted_expectStatus_200() throws Exception {
        Cheque proposedChequePaymentInstructionRequest =chequePaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .chequeNumber("000000").build();

        PaymentInstructionUpdateRequest statusUpdateRequest= paymentInstructionUpdateRequestWith()
            .status("P").build();

        restActions
            .post("/cheques",  proposedChequePaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .patch("/payment-instructions/1",statusUpdateRequest)
            .andExpect(status().isOk());


    }

    @Test
    public void whenNonExistingChequePaymentInstructionIsUpdated_expectStatus_404() throws Exception {
        Cheque proposedChequePaymentInstructionRequest =chequePaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .chequeNumber("000000").build();

        PaymentInstructionUpdateRequest statusUpdateRequest= paymentInstructionUpdateRequestWith()
            .status("P").build();

        restActions
            .post("/cheques",  proposedChequePaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .patch("/payment-instructions/1000", statusUpdateRequest)
            .andExpect(status().isNotFound());


    }

    @Test
    public void whenCaseReferenceForAChequePaymentInstructionIsCreated_expectStatus_201() throws Exception {
        Cheque proposedChequePaymentInstructionRequest = chequePaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .chequeNumber("000000").build();

        CaseReference caseReference = caseReferenceWith()
            .caseReference("case102")
            .build();

        restActions
            .post("/cheques",  proposedChequePaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .post("/payment-instructions/1/cases",caseReference)
            .andExpect(status().isCreated());


    }


    @Test
    public void whenInvalidCaseReferenceForAChequePaymentInstructionIsCreated_expectStatus_201() throws Exception {
        Cheque proposedChequePaymentInstructionRequest =chequePaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .chequeNumber("000000").build();

        CaseReference caseReference = caseReferenceWith()
            .caseReference("?????^^^^11")
            .build();

        restActions
            .post("/cheques",  proposedChequePaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .post("/payment-instructions/1/cases",caseReference)
            .andExpect(status().isBadRequest());


    }

    @Test
    public void whenSearchChequePaymentInstructionByPayerName_expectStatus_200() throws Exception {
        Cheque proposedChequePaymentInstructionRequest = chequePaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .chequeNumber("000000").build();

        restActions
            .post("/cheques", proposedChequePaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .get("/payment-instructions?payerName=Mr Payer Payer")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, chequePaymentInstructionList -> {
            assertThat(chequePaymentInstructionList.get(0)).isEqualToComparingOnlyGivenFields(
                chequePaymentInstructionWith()
                    .payerName("Mr Updated Payer")
                    .amount(600)
                    .currency("GBP")
                    .chequeNumber("000000"));
        }));
    }




    @Test
    public void whenSearchNonExistingChequePaymentInstructionByPayerName_expectStatus_200AndEmptyList() throws Exception {
        Cheque proposedChequePaymentInstructionRequest = chequePaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .chequeNumber("000000").build();

        restActions
            .post("/cheques", proposedChequePaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .get("/payment-instructions?payerName=NonExisting")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, chequePaymentInstructionList-> assertTrue(chequePaymentInstructionList.isEmpty())));
    }

    @Test
    public void whenSearchChequePaymentInstructionByChequeNumber_expectStatus_200() throws Exception {
        Cheque proposedChequePaymentInstructionRequest = chequePaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .chequeNumber("000000").build();

        restActions
            .post("/cheques", proposedChequePaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .get("/payment-instructions?chequeNumber=000000")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, chequePaymentInstructionList -> {
                assertThat(chequePaymentInstructionList.get(0)).isEqualToComparingOnlyGivenFields(
                    chequePaymentInstructionWith()
                        .payerName("Mr Updated Payer")
                        .amount(600)
                        .currency("GBP")
                        .chequeNumber("000000"));
            }));
    }


    @Test
    public void whenSearchNonExistingChequePaymentInstructionByChequeNumber_expectStatus_200AndEmptyList() throws Exception {
        Cheque proposedChequePaymentInstructionRequest = chequePaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .chequeNumber("000000").build();

        restActions
            .post("/cheques", proposedChequePaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .get("/payment-instructions?chequeNumber=111111")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, chequePaymentInstructionList-> assertTrue(chequePaymentInstructionList.isEmpty())));
    }

    @Test
    public void whenSearchChequePaymentInstructionWithInvalidChequeNumber_expectStatus_400() throws Exception {
        Cheque proposedChequePaymentInstructionRequest = chequePaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .chequeNumber("000000").build();

        restActions
            .post("/cheques", proposedChequePaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .get("/payment-instructions?chequeNumber=invalid")
            .andExpect(status().isBadRequest());

    }


}



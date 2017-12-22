package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;
import uk.gov.hmcts.bar.api.data.model.AllPayPaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.CaseReference;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.bar.api.data.model.AllPayPaymentInstruction.allPayPaymentInstructionWith;
import static uk.gov.hmcts.bar.api.data.model.CaseReference.caseReferenceWith;
import static uk.gov.hmcts.bar.api.data.model.PaymentInstructionRequest.paymentInstructionRequestWith;

public class AllPayInstructionCrudComponentTest extends ComponentTestBase {


    @Test
    public void whenAllPayPaymentInstructionDetails_thenCreateAllPayPaymentInstruction() throws Exception {
        AllPayPaymentInstruction proposedAllPayPaymentInstruction = allPayPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .allPayTransactionId("12345").build();

        restActions
            .post("/allpay", proposedAllPayPaymentInstruction)
            .andExpect(status().isCreated())
            .andExpect(body().as(AllPayPaymentInstruction.class, allPayPaymentInstruction-> {
                assertThat(allPayPaymentInstruction).isEqualToComparingOnlyGivenFields(
                    allPayPaymentInstructionWith()
                        .payerName("Mr Payer Payer")
                        .amount(500)
                        .currency("GBP").allPayTransactionId("12345").build());
            }));
    }

    @Test
    public void whenAllPayPaymentInstructionWithInvalidAllPayTransactionId_thenReturn400() throws Exception {
        AllPayPaymentInstruction proposedAllPayPaymentInstruction = allPayPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .allPayTransactionId("abcd").build();

        restActions
            .post("/allpay", proposedAllPayPaymentInstruction)
            .andExpect(status().isBadRequest());
    }


    @Test
    public void whenAllPayPaymentInstructionWithInvalidCurrency_thenReturn400() throws Exception {
        AllPayPaymentInstruction proposedAllPayPaymentInstruction = allPayPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("XXX")
            .allPayTransactionId("12345").build();

        restActions
            .post("/allpay", proposedAllPayPaymentInstruction)
            .andExpect(status().isBadRequest())
        ;
    }


    @Test
    public void givenAllPayPaymentInstructionDetails_retrieveThem() throws Exception {
        AllPayPaymentInstruction proposedAllPayPaymentInstruction = allPayPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .allPayTransactionId("12345").build();

        restActions
            .post("/allpay", proposedAllPayPaymentInstruction)
            .andExpect(status().isCreated());

        restActions
            .get("/payment-instructions")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, (allPayList) -> {
                assertThat(allPayList.get(0).equals(proposedAllPayPaymentInstruction));
            }));

    }

	@Test
	public void givenAllPayPaymentInstructionDetails_retrieveOneOfThem() throws Exception {
		AllPayPaymentInstruction proposedAllPayPaymentInstruction = allPayPaymentInstructionWith()
				.payerName("Mr Payer Payer").amount(500).currency("GBP").allPayTransactionId("12345").build();

		restActions.post("/allpay", proposedAllPayPaymentInstruction).andExpect(status().isCreated());

		restActions.get("/payment-instructions/1").andExpect(status().isOk())
				.andExpect(body().as(AllPayPaymentInstruction.class, (pi) -> {
					assertThat(pi.getAmount() == 500);
				}));
	}

	@Test
	public void givenAllPayPaymentInstructionDetails_retrieveOneOfThemWithWrongId() throws Exception {
		AllPayPaymentInstruction proposedAllPayPaymentInstruction = allPayPaymentInstructionWith()
				.payerName("Mr Payer Payer").amount(500).currency("GBP").allPayTransactionId("12345").build();

		restActions.post("/allpay", proposedAllPayPaymentInstruction).andExpect(status().isCreated());

		restActions.get("/payment-instructions/2").andExpect(status().isNotFound());
	}

    @Test
    public void whenAllPayPaymentInstructionIsDeleted_expectStatus_204() throws Exception {
        AllPayPaymentInstruction proposedAllPayPaymentInstruction = allPayPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .allPayTransactionId("12345").build();

        restActions
            .post("/allpay",  proposedAllPayPaymentInstruction)
            .andExpect(status().isCreated());

        restActions
            .delete("/payment-instructions/1")
            .andExpect(status().isNoContent());


    }

    @Test
    public void whenNonExistingAllPayPaymentInstructionIsDeleted_expectStatus_204() throws Exception {
        AllPayPaymentInstruction proposedAllPayPaymentInstruction = allPayPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .allPayTransactionId("12345").build();

        restActions
            .post("/allpay",  proposedAllPayPaymentInstruction)
            .andExpect(status().isCreated());


        restActions
            .delete("/payment-instructions/1000")
            .andExpect(status().isNotFound());

    }


    @Test
    public void whenAllPayPaymentInstructionIsSubmitted_expectStatus_200() throws Exception {
        AllPayPaymentInstruction proposedAllPayPaymentInstruction = allPayPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .allPayTransactionId("12345").build();

        PaymentInstructionRequest request= paymentInstructionRequestWith()
            .status("P").build();

        restActions
            .post("/allpay",  proposedAllPayPaymentInstruction)
            .andExpect(status().isCreated());


        restActions
            .patch("/payment-instructions/1",request)
            .andExpect(status().isOk());
    }

    @Test
    public void whenNonExistingAllPayPaymentInstructionIsUpdated_expectStatus_404() throws Exception {
        AllPayPaymentInstruction proposedAllPayPaymentInstruction = allPayPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .allPayTransactionId("12345").build();

        PaymentInstructionRequest request= paymentInstructionRequestWith()
            .status("P").build();

        restActions
            .post("/allpay",  proposedAllPayPaymentInstruction)
            .andExpect(status().isCreated());


        restActions
            .patch("/payment-instructions/1000",request)
            .andExpect(status().isNotFound());
    }


    @Test
    public void whenCaseReferenceForAllPayPaymentInstructionIsCreated_expectStatus_201() throws Exception {
        AllPayPaymentInstruction proposedAllPayPaymentInstruction = allPayPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .allPayTransactionId("12345").build();

        CaseReference caseReference = caseReferenceWith()
            .caseReference("case102")
            .build();

        restActions
            .post("/allpay",  proposedAllPayPaymentInstruction)
            .andExpect(status().isCreated());


        restActions
            .post("/payment-instructions/1/cases",caseReference)
            .andExpect(status().isCreated());


    }

    @Test
    public void whenInvalidCaseReferenceForAllPayPaymentInstructionIsCreated_expectStatus_400() throws Exception {
        AllPayPaymentInstruction proposedAllPayPaymentInstruction = allPayPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .allPayTransactionId("12345").build();

        CaseReference caseReference = caseReferenceWith()
            .caseReference("<><><><>")
            .build();

        restActions
            .post("/allpay",  proposedAllPayPaymentInstruction)
            .andExpect(status().isCreated());


        restActions
            .post("/payment-instructions/1/cases",caseReference)
            .andExpect(status().isBadRequest());


    }

    @Test
    public void whenAllPayPaymentInstructionIsEdited_expectStatus_200() throws Exception {
        AllPayPaymentInstruction proposedAllPayPaymentInstruction = allPayPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .allPayTransactionId("12345").build();

        AllPayPaymentInstruction updatedAllPayPaymentInstruction = allPayPaymentInstructionWith()
            .payerName("Mr Updated Payer")
            .amount(600)
            .currency("GBP")
            .allPayTransactionId("123456").build();

        restActions
            .post("/allpay",  proposedAllPayPaymentInstruction)
            .andExpect(status().isCreated());


        restActions
            .patch("/payment-instructions/1",updatedAllPayPaymentInstruction)
            .andExpect(status().isOk())
            .andExpect(body().as(AllPayPaymentInstruction.class, allPayPaymentInstruction-> {
                assertThat(allPayPaymentInstruction).isEqualToComparingOnlyGivenFields(
                    allPayPaymentInstructionWith()
                        .payerName("Mr Updated Payer")
                        .amount(600)
                        .currency("GBP").allPayTransactionId("123456").build());
            }));
    }

    @Test
    public void whenSearchAllPayPaymentInstructionByPayerName_expectStatus_200() throws Exception {
        AllPayPaymentInstruction proposedAllPayPaymentInstruction = allPayPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .allPayTransactionId("12345").build();

        restActions
            .post("/allpay",  proposedAllPayPaymentInstruction)
            .andExpect(status().isCreated());


        restActions
            .get("/payment-instructions?payerName=Mr Payer Payer")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, allPayPaymentInstructionList-> {
            assertThat(allPayPaymentInstructionList.get(0)).isEqualToComparingOnlyGivenFields(
                allPayPaymentInstructionWith()
                    .payerName("Mr Payer Payer")
                    .amount(500)
                    .currency("GBP").allPayTransactionId("12345").build());
        }));
    }


    @Test
    public void whenSearchNonExistingAllPayPaymentInstructionByPayerName_expectStatus_200AndEmptyList() throws Exception {
        AllPayPaymentInstruction proposedAllPayPaymentInstruction = allPayPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .allPayTransactionId("12345").build();

        restActions
            .post("/allpay",  proposedAllPayPaymentInstruction)
            .andExpect(status().isCreated());


        restActions
            .get("/payment-instructions?payerName=NonExisting")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, allPayPaymentInstructionList-> assertTrue(allPayPaymentInstructionList.isEmpty())));

    }



}


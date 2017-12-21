package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;
import uk.gov.hmcts.bar.api.data.model.CaseReference;
import uk.gov.hmcts.bar.api.data.model.CashPaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.bar.api.data.model.CaseReference.caseReferenceWith;
import static uk.gov.hmcts.bar.api.data.model.CashPaymentInstruction.cashPaymentInstructionWith;
import static uk.gov.hmcts.bar.api.data.model.PaymentInstructionRequest.paymentInstructionRequestWith;

public class CashInstructionCrudComponentTest extends ComponentTestBase {


    @Test
    public void whenCashPaymentInstructionDetails_thenCreateCashPaymentInstruction() throws Exception {
        CashPaymentInstruction.CashPaymentInstructionBuilder  proposedCashPaymentInstruction =cashPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP");

        restActions
            .post("/cash", proposedCashPaymentInstruction.build())
            .andExpect(status().isCreated())
            .andExpect(body().as(CashPaymentInstruction.class, cashPaymentInstructionDto -> {
                assertThat(cashPaymentInstructionDto).isEqualToComparingOnlyGivenFields(
                    cashPaymentInstructionWith()
                        .payerName("Mr Payer Payer")
                        .amount(500)
                        .currency("GBP"));
            }));

        /*restActions
            .delete("/payment-instructions/0")
            .andExpect(status().isNoContent());*/
    }

    @Test
    public void whenCashPaymentInstructionWithInvalidCurrency_thenReturn400() throws Exception {
        CashPaymentInstruction.CashPaymentInstructionBuilder  proposedCashPaymentInstruction =cashPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("XXX");

        restActions
            .post("/cash", proposedCashPaymentInstruction.build())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void givenCashPaymentInstructionDetails_retrieveThem() throws Exception {
        CashPaymentInstruction.CashPaymentInstructionBuilder  proposedCashPaymentInstruction =cashPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP");

        restActions
            .post("/cash",  proposedCashPaymentInstruction.build())
            .andExpect(status().isCreated());

        restActions
            .get("/payment-instructions")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, (cashList) -> {
                assertThat(cashList.get(0).equals( proposedCashPaymentInstruction.build()));
            }));


    }

	@Test
	public void givenCashPaymentInstructionDetails_retrieveOneOfThem() throws Exception {
		CashPaymentInstruction.CashPaymentInstructionBuilder proposedCashPaymentInstruction = cashPaymentInstructionWith()
				.payerName("Mr Payer Payer").amount(500).currency("GBP");

		restActions.post("/cash", proposedCashPaymentInstruction.build()).andExpect(status().isCreated());

		restActions.get("/payment-instructions/1").andExpect(status().isOk())
				.andExpect(body().as(CashPaymentInstruction.class, (pi) -> {
					assertThat(pi.getAmount() == 500);
				}));
	}

	@Test
	public void givenCashPaymentInstructionDetails_retrieveOneOfThemWithWrongId() throws Exception {
		CashPaymentInstruction.CashPaymentInstructionBuilder proposedCashPaymentInstruction = cashPaymentInstructionWith()
				.payerName("Mr Payer Payer").amount(500).currency("GBP");

		restActions.post("/cash", proposedCashPaymentInstruction.build()).andExpect(status().isCreated());

		restActions.get("/payment-instructions/2").andExpect(status().isNotFound());
	}

    @Test
    public void whenCashPaymentInstructionIsDeleted_expectStatus_204() throws Exception {
        CashPaymentInstruction.CashPaymentInstructionBuilder  proposedCashPaymentInstruction =cashPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP");

        restActions
            .post("/cash",  proposedCashPaymentInstruction.build())
            .andExpect(status().isCreated());


        restActions
            .delete("/payment-instructions/1")
            .andExpect(status().isNoContent());


    }


    @Test
    public void whenNonExistingCashPaymentInstructionIsDeleted_expectStatus_204() throws Exception {
        CashPaymentInstruction.CashPaymentInstructionBuilder  proposedCashPaymentInstruction =cashPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP");

        restActions
            .post("/cash",  proposedCashPaymentInstruction.build())
            .andExpect(status().isCreated());


        restActions
            .delete("/payment-instructions/1000")
            .andExpect(status().isNotFound());


    }


    @Test
    public void whenCashPaymentInstructionIsUpdated_expectStatus_200() throws Exception {
        CashPaymentInstruction.CashPaymentInstructionBuilder  proposedCashPaymentInstruction =cashPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP");


        PaymentInstructionRequest request= paymentInstructionRequestWith()
            .status("P").build();

        restActions
            .post("/cash",  proposedCashPaymentInstruction.build())
            .andExpect(status().isCreated());


        restActions
            .patch("/payment-instructions/1",request)
            .andExpect(status().isOk());


    }


    @Test
    public void whenNonExistingCashPaymentInstructionIsUpdated_expectStatus_404() throws Exception {
        CashPaymentInstruction.CashPaymentInstructionBuilder  proposedCashPaymentInstruction =cashPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP");


        PaymentInstructionRequest request= paymentInstructionRequestWith()
            .status("P").build();

        restActions
            .post("/cash",  proposedCashPaymentInstruction.build())
            .andExpect(status().isCreated());


        restActions
            .patch("/payment-instructions/1000",request)
            .andExpect(status().isNotFound());


    }

    @Test
    public void whenCaseReferenceForACashPaymentInstructionIsCreated_expectStatus_201() throws Exception {
        CashPaymentInstruction.CashPaymentInstructionBuilder  proposedCashPaymentInstruction =cashPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP");

        CaseReference caseReference = caseReferenceWith()
            .caseReference("case102")
            .build();

        restActions
            .post("/cash",  proposedCashPaymentInstruction.build())
            .andExpect(status().isCreated());


        restActions
            .post("/payment-instructions/1/cases",caseReference)
            .andExpect(status().isCreated());


    }
    @Test
    public void whenInvalidCaseReferenceForACashPaymentInstructionIsCreated_expectStatus_201() throws Exception {
        CashPaymentInstruction.CashPaymentInstructionBuilder  proposedCashPaymentInstruction =cashPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP");

        CaseReference caseReference = caseReferenceWith()
            .caseReference("????????")
            .build();

        restActions
            .post("/cash",  proposedCashPaymentInstruction.build())
            .andExpect(status().isCreated());


        restActions
            .post("/payment-instructions/1/cases",caseReference)
            .andExpect(status().isBadRequest());


    }



}



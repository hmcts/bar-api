package uk.gov.hmcts.bar.api.componenttests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.bar.api.data.model.ChequePaymentInstruction.chequePaymentInstructionWith;

import java.util.List;

import org.junit.Test;

import uk.gov.hmcts.bar.api.data.model.ChequePaymentInstruction;


public class ChequeInstructionCrudComponentTest extends ComponentTestBase {


    @Test
    public void whenChequeInstructionDetails_thenCreateChequePaymentInstruction() throws Exception {
        ChequePaymentInstruction.ChequePaymentInstructionBuilder  proposedChequePaymentInstruction =chequePaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .chequeNumber("000000");

        restActions
            .post("/cheques", proposedChequePaymentInstruction.build())
            .andExpect(status().isCreated())
            .andExpect(body().as(ChequePaymentInstruction.class, chequeItemDto -> {
                assertThat(chequeItemDto).isEqualToComparingOnlyGivenFields(
                    chequePaymentInstructionWith()
                        .payerName("Mr Payer Payer")
                        .amount(500)
                        .currency("GBP")
                        .chequeNumber("000000"));
            }));
    }

    @Test
    public void whenChequeInstructionWithInvalidChequeNumber_thenReturn400() throws Exception {
        ChequePaymentInstruction.ChequePaymentInstructionBuilder  proposedChequePaymentInstruction =chequePaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .chequeNumber("xxxxxx");

        restActions
            .post("/cheques", proposedChequePaymentInstruction.build())
            .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void whenChequeInstructionWithInvalidCurrency_thenReturn400() throws Exception {
        ChequePaymentInstruction.ChequePaymentInstructionBuilder  proposedChequePaymentInstruction =chequePaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("xxx")
            .chequeNumber("000000");

        restActions
            .post("/cheques", proposedChequePaymentInstruction.build())
            .andExpect(status().isBadRequest())
        ;
    }



    @Test
    public void givenChequePaymentInstructionDetails_retrieveThem() throws Exception {
        ChequePaymentInstruction.ChequePaymentInstructionBuilder  proposedChequePaymentInstruction =chequePaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .chequeNumber("000000");

        restActions
            .post("/cheques",  proposedChequePaymentInstruction.build())
            .andExpect(status().isCreated());

        restActions
            .get("/payment-instructions")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, (chequesList) -> {
                assertThat(chequesList.get(0).equals(proposedChequePaymentInstruction.build()));
            }));

    }
    
	@Test
	public void givenChequePaymentInstructionDetails_retrieveOneOfThem() throws Exception {
		ChequePaymentInstruction.ChequePaymentInstructionBuilder proposedChequePaymentInstruction = chequePaymentInstructionWith()
				.payerName("Mr Payer Payer").amount(500).currency("GBP").chequeNumber("000000");

		restActions.post("/cheques", proposedChequePaymentInstruction.build()).andExpect(status().isCreated());

		restActions.get("/payment-instructions/1").andExpect(status().isOk())
				.andExpect(body().as(ChequePaymentInstruction.class, (pi) -> {
					assertThat(pi.getAmount() == 500);
				}));
	}
	
	@Test
	public void givenChequePaymentInstructionDetails_retrieveOneOfThemWithWrongId() throws Exception {
		ChequePaymentInstruction.ChequePaymentInstructionBuilder proposedChequePaymentInstruction = chequePaymentInstructionWith()
				.payerName("Mr Payer Payer").amount(500).currency("GBP").chequeNumber("000000");

		restActions.post("/cheques", proposedChequePaymentInstruction.build()).andExpect(status().isCreated());

		restActions.get("/payment-instructions/2").andExpect(status().isNotFound());
	}

    @Test
    public void whenChequePaymentInstructionIsDeleted_expectStatus_204() throws Exception {
        ChequePaymentInstruction.ChequePaymentInstructionBuilder  proposedChequePaymentInstruction =chequePaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .chequeNumber("000000");

        restActions
            .post("/cheques",  proposedChequePaymentInstruction.build())
            .andExpect(status().isCreated());

        restActions
            .delete("/payment-instructions/1")
            .andExpect(status().isNoContent());


    }

    @Test
    public void whenNonExistingChequePaymentInstructionIsDeleted_expectStatus_204() throws Exception {
        ChequePaymentInstruction.ChequePaymentInstructionBuilder  proposedChequePaymentInstruction =chequePaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .chequeNumber("000000");

        restActions
            .post("/cheques",  proposedChequePaymentInstruction.build())
            .andExpect(status().isCreated());

        restActions
            .delete("/payment-instructions/1000")
            .andExpect(status().isNoContent());


    }


}



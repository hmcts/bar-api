package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionRequest;
import uk.gov.hmcts.bar.api.data.model.PostalOrderPaymentInstruction;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.bar.api.data.model.PaymentInstructionRequest.paymentInstructionRequestWith;
import static uk.gov.hmcts.bar.api.data.model.PostalOrderPaymentInstruction.postalOrderPaymentInstructionWith;

public class PostalOrderCrudComponentTest extends ComponentTestBase {

    @Test
    public void whenPostalOrderPaymentInstructionDetails_thenCreatePostalOrderPaymentInstruction() throws Exception {
        PostalOrderPaymentInstruction.PostalOrderPaymentInstructionBuilder  proposedPostalOrderPaymentInstruction =postalOrderPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .postalOrderNumber("000000");

        restActions
            .post("/postal-orders", proposedPostalOrderPaymentInstruction.build())
            .andExpect(status().isCreated())
            .andExpect(body().as(PostalOrderPaymentInstruction.class, postalOrderPaymentInstructionDto -> {
                assertThat(postalOrderPaymentInstructionDto).isEqualToComparingOnlyGivenFields(
                    postalOrderPaymentInstructionWith()
                        .payerName("Mr Payer Payer")
                        .amount(500)
                        .currency("GBP")
                        .postalOrderNumber("000000"));
            }));
    }

    @Test
    public void whenPostalOrderInstructionWithInvalidCurrency_thenReturn400() throws Exception {
        PostalOrderPaymentInstruction.PostalOrderPaymentInstructionBuilder  proposedPostalOrderPaymentInstruction =postalOrderPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("xxx")
            .postalOrderNumber("000000");

        restActions
            .post("/postal-orders", proposedPostalOrderPaymentInstruction.build())
            .andExpect(status().isBadRequest())
            ;
    }



    @Test
    public void whenPostalOrderInstructionWithInvalidPostalOrderNumber_thenReturn400() throws Exception {
        PostalOrderPaymentInstruction.PostalOrderPaymentInstructionBuilder  proposedPostalOrderPaymentInstruction =postalOrderPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .postalOrderNumber("xxxxxx");

        restActions
            .post("/postal-orders", proposedPostalOrderPaymentInstruction.build())
            .andExpect(status().isBadRequest())
        ;
    }


    @Test
    public void givenPostalOrderPaymentInstructionDetails_retrieveThem() throws Exception {
        PostalOrderPaymentInstruction.PostalOrderPaymentInstructionBuilder  proposedPostalOrderPaymentInstruction =postalOrderPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .postalOrderNumber("000000");

        restActions
            .post("/postal-orders",  proposedPostalOrderPaymentInstruction.build())
            .andExpect(status().isCreated());

        restActions
            .get("/payment-instructions")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, (postalOrdersList) -> {
                assertThat(postalOrdersList.get(0).equals(proposedPostalOrderPaymentInstruction.build()));
            }));

    }

	@Test
	public void givenPostalOrderPaymentInstructionDetails_retrieveOneOfThem() throws Exception {
		PostalOrderPaymentInstruction.PostalOrderPaymentInstructionBuilder proposedPostalOrderPaymentInstruction = postalOrderPaymentInstructionWith()
				.payerName("Mr Payer Payer").amount(500).currency("GBP").postalOrderNumber("000000");

		restActions.post("/postal-orders", proposedPostalOrderPaymentInstruction.build())
				.andExpect(status().isCreated());

		restActions.get("/payment-instructions/1").andExpect(status().isOk())
				.andExpect(body().as(PostalOrderPaymentInstruction.class, (pi) -> {
					assertThat(pi.getAmount() == 500);
				}));
	}

	@Test
	public void givenPostalOrderPaymentInstructionDetails_retrieveOneOfThemWithWrongId() throws Exception {
		PostalOrderPaymentInstruction.PostalOrderPaymentInstructionBuilder proposedPostalOrderPaymentInstruction = postalOrderPaymentInstructionWith()
				.payerName("Mr Payer Payer").amount(500).currency("GBP").postalOrderNumber("000000");

		restActions.post("/postal-orders", proposedPostalOrderPaymentInstruction.build())
				.andExpect(status().isCreated());

		restActions.get("/payment-instructions/2").andExpect(status().isNotFound());
	}

    @Test
    public void whenPostalOrderPaymentInstructionIsDeleted_expectStatus_204() throws Exception {
        PostalOrderPaymentInstruction.PostalOrderPaymentInstructionBuilder  proposedPostalOrderPaymentInstruction =postalOrderPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .postalOrderNumber("000000");

        restActions
            .post("/postal-orders",  proposedPostalOrderPaymentInstruction.build())
            .andExpect(status().isCreated());

        restActions
            .delete("/payment-instructions/1")
            .andExpect(status().isNoContent());


    }

    @Test
    public void whenNonExistingPostalOrderPaymentInstructionIsDeleted_expectStatus_204() throws Exception {
        PostalOrderPaymentInstruction.PostalOrderPaymentInstructionBuilder  proposedPostalOrderPaymentInstruction =postalOrderPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .postalOrderNumber("000000");

        restActions
            .post("/postal-orders",  proposedPostalOrderPaymentInstruction.build())
            .andExpect(status().isCreated());

        restActions
            .delete("/payment-instructions/1000")
            .andExpect(status().isNotFound());


    }

    @Test
    public void whenPostalOrderPaymentInstructionIsUpdated_expectStatus_200() throws Exception {
        PostalOrderPaymentInstruction.PostalOrderPaymentInstructionBuilder  proposedPostalOrderPaymentInstruction =postalOrderPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .postalOrderNumber("000000");


        PaymentInstructionRequest request= paymentInstructionRequestWith()
            .status("P").build();

        restActions
            .post("/postal-orders",  proposedPostalOrderPaymentInstruction.build())
            .andExpect(status().isCreated());

        restActions
            .patch("/payment-instructions/1",request)
            .andExpect(status().isOk());


    }

    @Test
    public void whenNonExistingPostalOrderPaymentInstructionIsUpdated_expectStatus_404() throws Exception {
        PostalOrderPaymentInstruction.PostalOrderPaymentInstructionBuilder  proposedPostalOrderPaymentInstruction =postalOrderPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .postalOrderNumber("000000");


        PaymentInstructionRequest request= paymentInstructionRequestWith()
            .status("P").build();

        restActions
            .post("/postal-orders",  proposedPostalOrderPaymentInstruction.build())
            .andExpect(status().isCreated());

        restActions
            .patch("/payment-instructions/1000",request)
            .andExpect(status().isNotFound());


    }








}

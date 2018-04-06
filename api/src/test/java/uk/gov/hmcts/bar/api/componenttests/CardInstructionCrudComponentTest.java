package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;
import uk.gov.hmcts.bar.api.data.model.Card;
import uk.gov.hmcts.bar.api.data.model.CardPaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.CaseReference;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionUpdateRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.bar.api.data.model.Card.cardWith;
import static uk.gov.hmcts.bar.api.data.model.CardPaymentInstruction.cardPaymentInstructionWith;
import static uk.gov.hmcts.bar.api.data.model.CaseReference.caseReferenceWith;
import static uk.gov.hmcts.bar.api.data.model.PaymentInstructionUpdateRequest.paymentInstructionUpdateRequestWith;

public class CardInstructionCrudComponentTest extends ComponentTestBase  {
    @Test
    public void whenCardPaymentInstructionDetails_thenCreateCardPaymentInstruction() throws Exception {
        Card proposedCardPaymentInstructionRequest = cardWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP").status("D").build();

        restActions
            .post("/cards", proposedCardPaymentInstructionRequest)
            .andExpect(status().isCreated())
            .andExpect(body().as(CardPaymentInstruction.class, cardPaymentInstruction -> {
                assertThat(cardPaymentInstruction).isEqualToComparingOnlyGivenFields(
                    cardPaymentInstructionWith()
                        .payerName("Mr Payer Payer")
                        .amount(500)
                        .status("D")
                        .currency("GBP"));
            }));

    }

    @Test
    public void whenCardPaymentInstructionWithInvalidCurrency_thenReturn400() throws Exception {
        Card proposedCardPaymentInstructionRequest = cardWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("XXX").build();

        restActions
            .post("/cards", proposedCardPaymentInstructionRequest)
            .andExpect(status().isBadRequest());
    }

    @Test
    public void givenCardPaymentInstructionDetails_retrieveThem() throws Exception {
        Card proposedCardPaymentInstructionRequest = cardWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP").build();

        CardPaymentInstruction  retrievedCardPaymentInstruction = cardPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP").build();

        restActions
            .post("/cards",  proposedCardPaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .get("/payment-instructions")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, (cardList) -> {
                assertThat(cardList.get(0).equals(retrievedCardPaymentInstruction));
            }));


    }

    @Test
    public void givenCardPaymentInstructionDetails_retrieveOneOfThem() throws Exception {
        Card proposedCardPaymentInstructionRequest = cardWith()
            .payerName("Mr Payer Payer").amount(500).currency("GBP").status("D").build();

        restActions.post("/cards", proposedCardPaymentInstructionRequest).andExpect(status().isCreated());

        restActions.get("/payment-instructions/1").andExpect(status().isOk())
            .andExpect(body().as(CardPaymentInstruction.class, (pi) -> {
                assertThat(pi.getAmount() == 500);
            }));
    }

    @Test
    public void givenCardPaymentInstructionDetails_retrieveOneOfThemWithWrongId() throws Exception {
        Card proposedCardPaymentInstructionRequest = cardWith()
            .payerName("Mr Payer Payer").amount(500).currency("GBP").build();

        restActions.post("/cards", proposedCardPaymentInstructionRequest).andExpect(status().isCreated());

        restActions.get("/payment-instructions/2").andExpect(status().isNotFound());
    }

    @Test
    public void whenCardPaymentInstructionIsDeleted_expectStatus_204() throws Exception {
        Card proposedCardPaymentInstructionRequest =cardWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP").build();

        restActions
            .post("/cards",  proposedCardPaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .delete("/payment-instructions/1")
            .andExpect(status().isNoContent());


    }


    @Test
    public void whenNonExistingCardPaymentInstructionIsDeleted_expectStatus_204() throws Exception {
        Card proposedCardPaymentInstructionRequest = cardWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP").build();

        restActions
            .post("/cards",  proposedCardPaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .delete("/payment-instructions/1000")
            .andExpect(status().isNotFound());


    }


    @Test
    public void whenCardPaymentInstructionIsSubmittedByPostClerk_expectStatus_200() throws Exception {
        Card proposedCardPaymentInstructionRequest = cardWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP").build();


        PaymentInstructionUpdateRequest request= paymentInstructionUpdateRequestWith()
            .status("P").build();

        restActions
            .post("/cards",  proposedCardPaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .patch("/payment-instructions/1",request)
            .andExpect(status().isOk());


    }


    @Test
    public void whenNonExistingCardPaymentInstructionIsSubmittedByPostClerk_expectStatus_404() throws Exception {
        Card proposedCardPaymentInstructionRequest = cardWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP").build();


        PaymentInstructionUpdateRequest request= paymentInstructionUpdateRequestWith()
            .status("P").build();

        restActions
            .post("/cards",  proposedCardPaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .patch("/payment-instructions/1000",request)
            .andExpect(status().isNotFound());


    }

    @Test
    public void whenCaseReferenceForACardPaymentInstructionIsCreated_expectStatus_201() throws Exception {
        Card proposedCardPaymentInstructionRequest = cardWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP").build();

        CaseReference caseReference = caseReferenceWith()
            .caseReference("case102")
            .build();

        restActions
            .post("/cards",  proposedCardPaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .post("/payment-instructions/1/cases",caseReference)
            .andExpect(status().isCreated());


    }
    @Test
    public void whenInvalidCaseReferenceForACardPaymentInstructionIsCreated_expectStatus_201() throws Exception {
        Card proposedCardPaymentInstructionRequest = cardWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP").build();

        CaseReference caseReference = caseReferenceWith()
            .caseReference("????????")
            .build();

        restActions
            .post("/cards",  proposedCardPaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .post("/payment-instructions/1/cases",caseReference)
            .andExpect(status().isBadRequest());


    }


    @Test
    public void whenSearchCardPaymentInstructionByPayerName_expectStatus_200() throws Exception {
        Card proposedCardPaymentInstructionRequest = cardWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP").build();

        restActions
            .post("/cards",  proposedCardPaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .get("/payment-instructions?payerName=Mr Payer Payer")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, cardPaymentInstructionList -> {
                assertThat(cardPaymentInstructionList.get(0)).isEqualToComparingOnlyGivenFields(
                    cardPaymentInstructionWith()
                        .payerName("Mr Payer Payer")
                        .amount(500)
                        .currency("GBP"));
            }));


    }
    @Test
    public void whenSearchNonExistingCardPaymentInstructionByPayerName_expectStatus_200AndEmptyList() throws Exception {
        Card proposedCardPaymentInstructionRequest = cardWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP").build();

        restActions
            .post("/cards",  proposedCardPaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .get("/payment-instructions?payerName=NonExisting")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, cardPaymentInstructionList-> assertTrue(cardPaymentInstructionList.isEmpty())));

    }


    @Test
    public void whenCardPaymentInstructionIsUpdated_expectStatus_200() throws Exception {
        Card proposedCardPaymentInstructionRequest = cardWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP").build();

        Card updatedCardPaymentInstructionRequest = cardWith()
            .payerName("Mr Updated Payer")
            .amount(6000)
            .currency("GBP").build();


        restActions
            .post("/cards",  proposedCardPaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .put("/cards/1",updatedCardPaymentInstructionRequest)
            .andExpect(status().isOk());

    }
    @Test
    public void whenNonExistingCardPaymentInstructionIsUpdated_expectStatus_404() throws Exception {
        Card proposedCardPaymentInstructionRequest = cardWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP").build();

        Card updatedCardPaymentInstructionRequest = cardWith()
            .payerName("Mr Updated Payer")
            .amount(6000)
            .currency("GBP").build();


        restActions
            .post("/cards",  proposedCardPaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .put("/cards/1000",updatedCardPaymentInstructionRequest)
            .andExpect(status().isNotFound());

    }

}



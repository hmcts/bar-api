package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;
import uk.gov.hmcts.bar.api.data.model.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.bar.api.data.model.PaymentInstructionUpdateRequest.paymentInstructionUpdateRequestWith;
import static uk.gov.hmcts.bar.api.data.model.PostalOrder.postalOrderPaymentInstructionRequestWith;
import static uk.gov.hmcts.bar.api.data.model.PostalOrderPaymentInstruction.postalOrderPaymentInstructionWith;

public class PostalOrderCrudComponentTest extends ComponentTestBase {

    @Test
    public void whenPostalOrderPaymentInstructionDetails_thenCreatePostalOrderPaymentInstruction() throws Exception {
        PostalOrder proposedPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .postalOrderNumber("000000").status("D").build();

        restActions
            .post("/postal-orders", proposedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isCreated())
            .andExpect(body().as(PostalOrderPaymentInstruction.class, postalOrderPaymentInstruction -> {
                assertThat(postalOrderPaymentInstruction).isEqualToComparingOnlyGivenFields(
                    postalOrderPaymentInstructionRequestWith()
                        .payerName("Mr Payer Payer")
                        .amount(500)
                        .currency("GBP").status("D")
                        .postalOrderNumber("000000"));
            }));
    }

    @Test
    public void whenPostalOrderInstructionWithInvalidCurrency_thenReturn400() throws Exception {
        PostalOrder proposedPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("xxx")
            .postalOrderNumber("000000").build();

        restActions
            .post("/postal-orders", proposedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isBadRequest())
        ;
    }


    @Test
    public void whenPostalOrderInstructionWithInvalidPostalOrderNumber_thenReturn400() throws Exception {
        PostalOrder proposedPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .postalOrderNumber("xxxxxx").build();

        restActions
            .post("/postal-orders", proposedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isBadRequest())
        ;
    }


    @Test
    public void givenPostalOrderPaymentInstructionDetails_retrieveThem() throws Exception {
        PostalOrder proposedPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP").status("D")
            .postalOrderNumber("000000").build();

        PostalOrderPaymentInstruction retrievedPostalOrderPaymentInstruction = postalOrderPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP").status("D")
            .postalOrderNumber("000000").build();

        restActions
            .post("/postal-orders", proposedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .get("/payment-instructions")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, (postalOrdersList) -> {
                assertThat(postalOrdersList.get(0).equals(retrievedPostalOrderPaymentInstruction));
            }));

    }

    @Test
    public void givenPostalOrderPaymentInstructionDetails_retrieveOneOfThem() throws Exception {
        PostalOrder proposedPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer").amount(500).currency("GBP").postalOrderNumber("000000").status("D").build();

        restActions.post("/postal-orders", proposedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions.get("/payment-instructions/1").andExpect(status().isOk())
            .andExpect(body().as(PostalOrderPaymentInstruction.class, (pi) -> {
                assertThat(pi.getAmount() == 500);
            }));
    }

    @Test
    public void givenPostalOrderPaymentInstructionDetails_retrieveOneOfThemWithWrongId() throws Exception {
        PostalOrder proposedPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer").amount(500).currency("GBP").postalOrderNumber("000000").status("D").build();

        restActions.post("/postal-orders", proposedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions.get("/payment-instructions/2").andExpect(status().isNotFound());
    }

    @Test
    public void whenPostalOrderPaymentInstructionIsDeleted_expectStatus_204() throws Exception {
        PostalOrder proposedPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .postalOrderNumber("000000").status("D").build();

        restActions
            .post("/postal-orders", proposedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .delete("/payment-instructions/1")
            .andExpect(status().isNoContent());


    }

    @Test
    public void whenNonExistingPostalOrderPaymentInstructionIsDeleted_expectStatus_204() throws Exception {
        PostalOrder proposedPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .postalOrderNumber("000000").status("D").build();

        restActions
            .post("/postal-orders", proposedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .delete("/payment-instructions/1000")
            .andExpect(status().isNotFound());


    }

    @Test
    public void whenPostalOrderPaymentInstructionIsSubmitted_expectStatus_200() throws Exception {
        PostalOrder proposedPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .postalOrderNumber("000000").status("D").build();


        PaymentInstructionUpdateRequest stattusUpdateRequest = paymentInstructionUpdateRequestWith()
            .status("P").build();

        restActions
            .post("/postal-orders", proposedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .put("/payment-instructions/1", stattusUpdateRequest)
            .andExpect(status().isOk());


    }

    @Test
    public void whenBgcNumberIsProvidedOnUpdate_expectedToBeSaved() throws Exception {
        PostalOrder proposedPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .postalOrderNumber("000000").status("D").build();

        PostalOrder updatedPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(6000)
            .currency("GBP")
            .status("P")
            .postalOrderNumber("000000").bgcNumber("12345").build();


        restActions
            .post("/cheques",  proposedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .put("/cheques/1",updatedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isOk());

        restActions
            .get("/payment-instructions")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, (allPayList) -> {
                String bgcNumber = (String)((Map)allPayList.get(0)).get("bgc_number");
                assertThat(bgcNumber.equals("12345"));
            }));
    }

    @Test
    public void whenNonExistingPostalOrderPaymentInstructionIsUpdated_expectStatus_404() throws Exception {
        PostalOrder proposedPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .postalOrderNumber("000000").status("D").build();


        PaymentInstructionUpdateRequest statusUpdateRequest = paymentInstructionUpdateRequestWith()
            .status("P").build();

        restActions
            .post("/postal-orders", proposedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .put("/payment-instructions/1000", statusUpdateRequest)
            .andExpect(status().isNotFound());


    }

    @Test
    public void whenCaseReferenceForAChequePaymentInstructionIsCreated_expectStatus_201() throws Exception {
        PostalOrder proposedPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .postalOrderNumber("000000").status("D").build();

        CaseFeeDetailRequest caseFeeDetailRequest = CaseFeeDetailRequest.caseFeeDetailRequestWith()
            .caseReference("case102")
            .feeCode("X001")
            .amount(200)
            .feeVersion("1")
            .build();

        restActions
            .post("/postal-orders", proposedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .post("/fees", caseFeeDetailRequest)
            .andExpect(status().isCreated());


    }


    @Test
    public void whenInvalidCaseReferenceForAChequePaymentInstructionIsCreated_expectStatus_201() throws Exception {
        PostalOrder proposedPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .postalOrderNumber("000000").status("D").build();

        CaseFeeDetailRequest caseFeeDetailRequest = CaseFeeDetailRequest.caseFeeDetailRequestWith()
            .caseReference("$$$$$$$$$")
            .build();

        restActions
            .post("/postal-orders", proposedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .post("/fees", caseFeeDetailRequest)
            .andExpect(status().isBadRequest());


    }


    @Test
    public void whenSearchPostalOrderPaymentInstructionByPayerName_expectStatus_200() throws Exception {
        PostalOrder proposedPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .postalOrderNumber("000000").status("D").build();

        restActions
            .post("/postal-orders", proposedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .get("/payment-instructions?payerName=Mr Payer Payer")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, postalOrderPaymentInstructionList -> {
                assertThat(postalOrderPaymentInstructionList.get(0)).isEqualToComparingOnlyGivenFields(
                    postalOrderPaymentInstructionWith()
                        .payerName("Mr Updated Payer")
                        .amount(600)
                        .currency("GBP")
                        .status("D")
                        .postalOrderNumber("000000"));
            }));

    }

    @Test
    public void whenSearchNonExistingPostalOrderPaymentInstructionByPayerName_expectStatus_200AndEmptyList() throws Exception {
        PostalOrder proposedPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .postalOrderNumber("000000").status("D").build();

        restActions
            .post("/postal-orders", proposedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .get("/payment-instructions?payerName=NonExisting")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, postalOrderPaymentInstructionList -> assertTrue(postalOrderPaymentInstructionList.isEmpty())));

    }

    @Test
    public void whenPostalOrderPaymentInstructionIsSubmittedByPostClerk_expectStatus_200() throws Exception {
        PostalOrder proposedPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP").status("D")
            .postalOrderNumber("000000").build();


        PostalOrder updatedPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Updated Payer")
            .amount(6000)
            .currency("GBP").status("D")
            .postalOrderNumber("000000").build();


        restActions
            .post("/postal-orders",  proposedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .put("/postal-orders/1",updatedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isOk());

    }
    @Test
    public void whenNonExistingPostalOrderPaymentInstructionIsSubmittedByPostClerk_expectStatus_404() throws Exception {
        PostalOrder proposedPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP").status("D")
            .postalOrderNumber("000000").build();

        PostalOrder updatedPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Updated Payer")
            .amount(6000)
            .currency("GBP").status("D")
            .postalOrderNumber("000000").build();


        restActions
            .post("/postal-orders",  proposedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .put("/postal-orders/1000",updatedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isNotFound());

    }




}

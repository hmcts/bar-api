package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;
import uk.gov.hmcts.bar.api.data.model.FullRemission;
import uk.gov.hmcts.bar.api.data.model.FullRemissionPaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionUpdateRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.bar.api.data.model.FullRemission.fullRemissionWith;
import static uk.gov.hmcts.bar.api.data.model.FullRemissionPaymentInstruction.fullRemissionPaymentInstructionWith;
import static uk.gov.hmcts.bar.api.data.model.PaymentInstructionUpdateRequest.paymentInstructionUpdateRequestWith;

public class FullRemissionCrudComponentTest extends ComponentTestBase {

    @Test
    public void whenFullRemissionPaymentInstructionDetails_thenCreateFullRemissionPaymentInstruction() throws Exception {
        FullRemission proposedFullRemissionPaymentInstructionRequest = fullRemissionWith()
            .payerName("Mr Payer Payer")
            .status("D").remissionReference("123456abcde").build();

        restActions
            .post("/remissions", proposedFullRemissionPaymentInstructionRequest)
            .andExpect(status().isCreated())
            .andExpect(body().as(FullRemissionPaymentInstruction.class, fullRemissionPaymentInstruction -> {
                assertThat(fullRemissionPaymentInstruction).isEqualToComparingOnlyGivenFields(
                    fullRemissionPaymentInstructionWith()
                        .payerName("Mr Payer Payer")
                        .remissionReference("123456abcde"));
            }));

    }

    @Test
    public void whenCardPaymentInstructionWithInvalidAuthorizationCode_thenReturn400() throws Exception {
        FullRemission proposedFullRemissionPaymentInstructionRequest = fullRemissionWith()
            .payerName("Mr Payer Payer")
            .remissionReference("123456abcde23").build();

        restActions
            .post("/remissions", proposedFullRemissionPaymentInstructionRequest)
            .andExpect(status().isBadRequest());
    }

    @Test
    public void givenFullRemissionPaymentInstructionDetails_retrieveThem() throws Exception {
        FullRemission proposedFullRemissionPaymentInstructionRequest = fullRemissionWith()
            .payerName("Mr Payer Payer")
            .remissionReference("12345678911").build();

        FullRemissionPaymentInstruction retrievedFullRemissionPaymentInstruction = fullRemissionPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .remissionReference("12345678911").build();


        restActions
            .post("/remissions", proposedFullRemissionPaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .get("/payment-instructions")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, (fullRemissionList) -> {
                assertThat(fullRemissionList.get(0).equals(retrievedFullRemissionPaymentInstruction));
            }));

    }


    @Test
    public void givenFullRemissionPaymentInstructionDetails_retrieveOneOfThem() throws Exception {
        FullRemission proposedFullRemissionPaymentInstructionRequest = fullRemissionWith()
            .payerName("Mr Payer Payer")
            .remissionReference("12345678911").build();

        restActions.post("/remissions", proposedFullRemissionPaymentInstructionRequest).andExpect(status().isCreated());

        restActions.get("/payment-instructions/1").andExpect(status().isOk())
            .andExpect(body().as(FullRemissionPaymentInstruction.class, (pi) -> {
                assertThat(pi.getPayerName().equals("Mr Payer Payer"));
            }));
    }

    @Test
    public void givenFullRemissionPaymentInstructionDetails_retrieveOneOfThemWithWrongId() throws Exception {
        FullRemission proposedFullRemissionPaymentInstructionRequest = fullRemissionWith()
            .payerName("Mr Payer Payer")
            .remissionReference("12345678911").build();

        restActions.post("/remissions", proposedFullRemissionPaymentInstructionRequest).andExpect(status().isCreated());

        restActions.get("/payment-instructions/2").andExpect(status().isNotFound());
    }


    @Test
    public void whenFullRemissionPaymentInstructionIsDeleted_expectStatus_204() throws Exception {
        FullRemission proposedFullRemissionPaymentInstructionRequest = fullRemissionWith()
            .payerName("Mr Payer Payer")
            .remissionReference("12345678911").build();
        restActions
            .post("/remissions",  proposedFullRemissionPaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .delete("/payment-instructions/1")
            .andExpect(status().isNoContent());


    }

    @Test
    public void whenFullRemissionPaymentInstructionIsSubmittedByPostClerk_expectStatus_200() throws Exception {
        FullRemission proposedFullRemissionPaymentInstructionRequest = fullRemissionWith()
            .payerName("Mr Payer Payer")
            .remissionReference("12345678911").build();

        PaymentInstructionUpdateRequest request= paymentInstructionUpdateRequestWith()
            .status("P").build();

        restActions
            .post("/remissions",  proposedFullRemissionPaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .put("/payment-instructions/1",request)
            .andExpect(status().isOk());
    }

    @Test
    public void whenNonExistingFullRemissionPaymentInstructionIsSubmittedByPostClerk_expectStatus_404() throws Exception {
        FullRemission proposedFullRemissionPaymentInstructionRequest = fullRemissionWith()
            .payerName("Mr Payer Payer")
            .remissionReference("12345678911").build();

        PaymentInstructionUpdateRequest request= paymentInstructionUpdateRequestWith()
            .status("P").build();

        restActions
            .post("/remissions",  proposedFullRemissionPaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .put("/payment-instructions/1000",request)
            .andExpect(status().isNotFound());
    }

    @Test
    public void whenSearchFullRemissionPaymentInstructionByPayerName_expectStatus_200() throws Exception {
        FullRemission proposedFullRemissionPaymentInstructionRequest = fullRemissionWith()
            .payerName("Mr Payer Payer")
            .remissionReference("12345678911").build();

        restActions
            .post("/remissions",  proposedFullRemissionPaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .get("/payment-instructions?payerName=Mr Payer Payer")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, fullRemissionPaymentInstructionList-> {
                assertThat(fullRemissionPaymentInstructionList.get(0)).isEqualToComparingOnlyGivenFields(
                    fullRemissionPaymentInstructionWith()
                        .payerName("Mr Payer Payer")
                        .status("D")
                        .remissionReference("12345678911").build());
            }));
    }

    @Test
    public void whenSearchNonExistingFullRemissionPaymentInstructionByPayerName_expectStatus_200AndEmptyList() throws Exception {
        FullRemission proposedFullRemissionPaymentInstructionRequest = fullRemissionWith()
            .payerName("Mr Payer Payer")
            .remissionReference("12345678911").build();

        restActions
            .post("/remissions",  proposedFullRemissionPaymentInstructionRequest)
            .andExpect(status().isCreated());


        restActions
            .get("/payment-instructions?payerName=NonExisting")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, fullRemissionPaymentInstructionList-> assertTrue(fullRemissionPaymentInstructionList.isEmpty())));

    }

    @Test
    public void whenFullRemissionPaymentInstructionIsUpdated_expectStatus_200() throws Exception {
        FullRemission proposedFullRemissionPaymentInstructionRequest = fullRemissionWith()
            .payerName("Mr Payer Payer")
            .remissionReference("12345678911").build();

        FullRemission updatedAllPayPaymentInstructionRequest = fullRemissionWith()
            .payerName("Mr Payer Payer")
            .remissionReference("12345678912").build();


        restActions
            .post("/remissions",  proposedFullRemissionPaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .put("/remissions/1",updatedAllPayPaymentInstructionRequest)
            .andExpect(status().isOk());

    }
    @Test
    public void whenNonExistingFullRemissionPaymentInstructionIsUpdated_expectStatus_404() throws Exception {
        FullRemission proposedFullRemissionPaymentInstructionRequest = fullRemissionWith()
            .payerName("Mr Payer Payer")
            .remissionReference("12345678911").build();

        FullRemission updatedAllPayPaymentInstructionRequest = fullRemissionWith()
            .payerName("Mr Payer Payer")
            .remissionReference("12345678912").build();


        restActions
            .post("/remissions",  proposedFullRemissionPaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .put("/remissions/2",updatedAllPayPaymentInstructionRequest)
            .andExpect(status().isNotFound());

    }




}

package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;
import uk.gov.hmcts.bar.api.data.model.AllPayPaymentInstruction;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.bar.api.data.model.AllPayPaymentInstruction.allPayPaymentInstructionWith;

public class AllPayInstructionCrudComponentTest extends ComponentTestBase {


    @Test
    public void whenAllPayPaymentInstructionDetails_thenCreateAllPayPaymentInstruction() throws Exception {
        AllPayPaymentInstruction.AllPayPaymentInstructionBuilder proposedAllPayPaymentInstruction = allPayPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .allPayTransactionId("12345");

        restActions
            .post("/allpay", proposedAllPayPaymentInstruction.build())
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
        AllPayPaymentInstruction.AllPayPaymentInstructionBuilder proposedAllPayPaymentInstruction = allPayPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .allPayTransactionId("abcd");

        restActions
            .post("/allpay", proposedAllPayPaymentInstruction.build())
            .andExpect(status().isBadRequest())
            ;
    }


    @Test
    public void whenAllPayPaymentInstructionWithInvalidCurrency_thenReturn400() throws Exception {
        AllPayPaymentInstruction.AllPayPaymentInstructionBuilder proposedAllPayPaymentInstruction = allPayPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("XXX")
            .allPayTransactionId("12345");

        restActions
            .post("/allpay", proposedAllPayPaymentInstruction.build())
            .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void givenAllPayPaymentInstructionDetails_retrieveThem() throws Exception {
        AllPayPaymentInstruction.AllPayPaymentInstructionBuilder proposedAllPayPaymentInstruction = allPayPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .allPayTransactionId("12345");

        restActions
            .post("/allpay", proposedAllPayPaymentInstruction.build())
            .andExpect(status().isCreated());

        restActions
            .get("/payment-instructions")
            .andExpect(status().isOk())
            .andExpect(body().as(List.class, (allPayList) -> {
                assertThat(allPayList.get(0).equals(proposedAllPayPaymentInstruction.build()));
            }));

    }

    @Test
    public void whenAllPayPaymentInstructionIsDeleted_expectStatus_204() throws Exception {
        AllPayPaymentInstruction.AllPayPaymentInstructionBuilder proposedAllPayPaymentInstruction = allPayPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .allPayTransactionId("12345");

        restActions
            .post("/allpay",  proposedAllPayPaymentInstruction.build())
            .andExpect(status().isCreated());

        restActions
            .delete("/payment-instructions/1")
            .andExpect(status().isNoContent());


    }

    @Test
    public void whenNonExistingAllPayPaymentInstructionIsDeleted_expectStatus_204() throws Exception {
        AllPayPaymentInstruction.AllPayPaymentInstructionBuilder proposedAllPayPaymentInstruction = allPayPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .allPayTransactionId("12345");

        restActions
            .post("/allpay",  proposedAllPayPaymentInstruction.build())
            .andExpect(status().isCreated());


        restActions
            .delete("/payment-instructions/1000")
            .andExpect(status().isNoContent());

    }


}


package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;
import uk.gov.hmcts.bar.api.data.model.AllPayPaymentInstruction;

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
            .andExpect(body().as(AllPayPaymentInstruction.class, allPayPaymentInstructionDto -> {
                assertThat(allPayPaymentInstructionDto).isEqualToComparingOnlyGivenFields(
                    allPayPaymentInstructionWith()
                        .payerName("Mr Payer Payer")
                        .amount(500)
                        .currency("GBP").allPayTransactionId("12345"));
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



}


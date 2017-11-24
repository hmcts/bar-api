package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;
import uk.gov.hmcts.bar.api.data.model.CashPaymentInstruction;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.bar.api.data.model.CashPaymentInstruction.cashPaymentInstructionWith;


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
    }

    @Test
    public void whenCashPaymentInstructionWithInvalidCurrency_thenReturn400() throws Exception {
        CashPaymentInstruction.CashPaymentInstructionBuilder  proposedCashPaymentInstruction =cashPaymentInstructionWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("XXX");

        restActions
            .post("/cash", proposedCashPaymentInstruction.build())
            .andExpect(status().isBadRequest())
            ;
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





}



package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;
import uk.gov.hmcts.bar.api.contract.CashPaymentInstructionDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.bar.api.contract.CashPaymentInstructionDto.cashPaymentInstructionDtoWith;


public class CashInstructionCrudComponentTest extends ComponentTestBase {


    @Test
    public void whenCashPaymentInstructionDetails_thenCreateCashPaymentInstruction() throws Exception {
        CashPaymentInstructionDto.CashPaymentInstructionDtoBuilder  proposedCashPaymentInstruction =cashPaymentInstructionDtoWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP");

        restActions
            .post("/cash", proposedCashPaymentInstruction.build())
            .andExpect(status().isCreated())
            .andExpect(body().as(CashPaymentInstructionDto.class, cashPaymentInstructionDto -> {
                assertThat(cashPaymentInstructionDto).isEqualToComparingOnlyGivenFields(
                    cashPaymentInstructionDtoWith()
                        .payerName("Mr Payer Payer")
                        .amount(500)
                        .currency("GBP"));
            }));
    }

    @Test
    public void whenCashPaymentInstructionWithInvalidCurrency_thenReturn400() throws Exception {
        CashPaymentInstructionDto.CashPaymentInstructionDtoBuilder  proposedCashPaymentInstruction =cashPaymentInstructionDtoWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("XXX");

        restActions
            .post("/cash", proposedCashPaymentInstruction.build())
            .andExpect(status().isBadRequest())
            ;
    }



}



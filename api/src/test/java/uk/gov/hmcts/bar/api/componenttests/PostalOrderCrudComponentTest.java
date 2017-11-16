package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;
import uk.gov.hmcts.bar.api.contract.PostalOrderPaymentInstructionDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.bar.api.contract.PostalOrderPaymentInstructionDto.postalOrderPaymentInstructionDtoWith;

public class PostalOrderCrudComponentTest extends ComponentTestBase {

    @Test
    public void whenCashPaymentInstructionDetails_thenCreateCashPaymentInstruction() throws Exception {
        PostalOrderPaymentInstructionDto.PostalOrderPaymentInstructionDtoBuilder  proposedCashPaymentInstruction =postalOrderPaymentInstructionDtoWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .instrumentNumber("000000");

        restActions
            .post("/postal-orders", proposedCashPaymentInstruction.build())
            .andExpect(status().isCreated())
            .andExpect(body().as(PostalOrderPaymentInstructionDto.class, cashPaymentInstructionDto -> {
                assertThat(cashPaymentInstructionDto).isEqualToComparingOnlyGivenFields(
                    postalOrderPaymentInstructionDtoWith()
                        .payerName("Mr Payer Payer")
                        .amount(500)
                        .currency("GBP")
                        .instrumentNumber("000000"));
            }));
    }

    @Test
    public void whenCashPaymentInstructionWithInvalidCurrency_thenReturn400() throws Exception {
        PostalOrderPaymentInstructionDto.PostalOrderPaymentInstructionDtoBuilder  proposedCashPaymentInstruction =postalOrderPaymentInstructionDtoWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("xxx")
            .instrumentNumber("000000");

        restActions
            .post("/postal-orders", proposedCashPaymentInstruction.build())
            .andExpect(status().isBadRequest())
            ;
    }



    @Test
    public void whenCashPaymentInstructionWithInvalidInstrumentNumber_thenReturn400() throws Exception {
        PostalOrderPaymentInstructionDto.PostalOrderPaymentInstructionDtoBuilder  proposedCashPaymentInstruction =postalOrderPaymentInstructionDtoWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .instrumentNumber("xxxxxx");

        restActions
            .post("/postal-orders", proposedCashPaymentInstruction.build())
            .andExpect(status().isBadRequest())
        ;
    }


}

package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;
import uk.gov.hmcts.bar.api.data.model.PostalOrderPaymentInstruction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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


}

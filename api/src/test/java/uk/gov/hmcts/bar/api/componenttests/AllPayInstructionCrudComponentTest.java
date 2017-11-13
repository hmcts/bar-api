package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;
import uk.gov.hmcts.bar.api.contract.AllPayPaymentInstructionDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.bar.api.contract.AllPayPaymentInstructionDto.allPayPaymentInstructionDtoWith;

public class AllPayInstructionCrudComponentTest extends ComponentTestBase {


    @Test
    public void whenAllPayPaymentInstructionDetails_thenCreateAllPayPaymentInstruction() throws Exception {
        AllPayPaymentInstructionDto.AllPayPaymentInstructionDtoBuilder proposedAllPayPaymentInstruction = allPayPaymentInstructionDtoWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .allPayTransactionId("12345");

        restActions
            .post("/allpay", proposedAllPayPaymentInstruction.build())
            .andExpect(status().isOk())
            .andExpect(body().as(AllPayPaymentInstructionDto.class, allPayPaymentInstructionDto -> {
                assertThat(allPayPaymentInstructionDto).isEqualToComparingOnlyGivenFields(
                    allPayPaymentInstructionDtoWith()
                        .payerName("Mr Payer Payer")
                        .amount(500)
                        .currency("GBP").allPayTransactionId("12345"));
            }));
    }
}


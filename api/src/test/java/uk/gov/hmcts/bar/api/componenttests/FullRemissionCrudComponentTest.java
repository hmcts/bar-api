package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;
import uk.gov.hmcts.bar.api.data.model.FullRemission;
import uk.gov.hmcts.bar.api.data.model.FullRemissionPaymentInstruction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.bar.api.data.model.FullRemission.remissionWith;
import static uk.gov.hmcts.bar.api.data.model.FullRemissionPaymentInstruction.fullRemissionPaymentInstructionWith;

public class FullRemissionCrudComponentTest extends ComponentTestBase{

        @Test
        public void whenFullRemissionPaymentInstructionDetails_thenCreateFullRemissionPaymentInstruction() throws Exception {
            FullRemission proposedFullRemissionPaymentInstructionRequest = remissionWith()
                .payerName("Mr Payer Payer")
                .status("D").remissionReference("123456abcde").build();

            restActions
                .post("/remissions", proposedFullRemissionPaymentInstructionRequest)
                .andExpect(status().isCreated())
                .andExpect(body().as(FullRemissionPaymentInstruction.class, fullRemissionPaymentInstruction -> {
                    assertThat(fullRemissionPaymentInstruction).isEqualToComparingOnlyGivenFields(
                        fullRemissionPaymentInstructionWith()
                            .payerName("Mr Payer Payer")
                            .amount(500)
                            .status("D").remissionReference("123456abcde")
                            .currency("GBP"));
                }));

        }

        @Test
        public void whenCardPaymentInstructionWithInvalidAuthorizationCode_thenReturn400() throws Exception {
            FullRemission proposedFullRemissionPaymentInstructionRequest = remissionWith()
                .payerName("Mr Payer Payer")
                .remissionReference("123456abcde23").build();

            restActions
                .post("/remissions", proposedFullRemissionPaymentInstructionRequest)
                .andExpect(status().isBadRequest());
        }

    }


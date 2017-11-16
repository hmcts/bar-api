package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;
import uk.gov.hmcts.bar.api.contract.ChequePaymentInstructionDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.bar.api.contract.ChequePaymentInstructionDto.chequePaymentInstructionDtoWith;


public class ChequeInstructionCrudComponentTest extends ComponentTestBase {


    @Test
    public void whenChequeInstructionDetails_thenCreateChequePaymentInstruction() throws Exception {
        ChequePaymentInstructionDto.ChequePaymentInstructionDtoBuilder  proposedChequePaymentInstruction =chequePaymentInstructionDtoWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .instrumentNumber("000000")
            .sortCode("000000")
            .accountNumber("00000000");

        restActions
            .post("/cheques", proposedChequePaymentInstruction.build())
            .andExpect(status().isCreated())
            .andExpect(body().as(ChequePaymentInstructionDto.class, chequeItemDto -> {
                assertThat(chequeItemDto).isEqualToComparingOnlyGivenFields(
                    chequePaymentInstructionDtoWith()
                        .payerName("Mr Payer Payer")
                        .amount(500)
                        .currency("GBP")
                        .instrumentNumber("000000")
                        .sortCode("000000")
                        .accountNumber("00000000"));
            }));
    }


    @Test
    public void whenChequeInstructionWithInvalidSortCode_thenReturn400() throws Exception {
        ChequePaymentInstructionDto.ChequePaymentInstructionDtoBuilder  proposedChequePaymentInstruction =chequePaymentInstructionDtoWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .instrumentNumber("000000")
            .sortCode("xxxxxx")
            .accountNumber("00000000");

        restActions
            .post("/cheques", proposedChequePaymentInstruction.build())
            .andExpect(status().isBadRequest())
            ;
    }
    @Test
    public void whenChequeInstructionWithInvalidAccountNumber_thenReturn400() throws Exception {
        ChequePaymentInstructionDto.ChequePaymentInstructionDtoBuilder  proposedChequePaymentInstruction =chequePaymentInstructionDtoWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .instrumentNumber("000000")
            .sortCode("000000")
            .accountNumber("xxxxxxxx");

        restActions
            .post("/cheques", proposedChequePaymentInstruction.build())
            .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void whenChequeInstructionWithInvalidInstrumentNumber_thenReturn400() throws Exception {
        ChequePaymentInstructionDto.ChequePaymentInstructionDtoBuilder  proposedChequePaymentInstruction =chequePaymentInstructionDtoWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .instrumentNumber("xxxxxx")
            .sortCode("000000")
            .accountNumber("00000000");

        restActions
            .post("/cheques", proposedChequePaymentInstruction.build())
            .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void whenChequeInstructionWithInvalidCurrency_thenReturn400() throws Exception {
        ChequePaymentInstructionDto.ChequePaymentInstructionDtoBuilder  proposedChequePaymentInstruction =chequePaymentInstructionDtoWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("xxx")
            .instrumentNumber("000000")
            .sortCode("000000")
            .accountNumber("00000000");

        restActions
            .post("/cheques", proposedChequePaymentInstruction.build())
            .andExpect(status().isBadRequest())
        ;
    }





}



package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Assert;
import org.junit.Test;
import uk.gov.hmcts.bar.api.data.model.PostalOrder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.bar.api.converters.CsvConverter.*;
import static uk.gov.hmcts.bar.api.data.model.PostalOrder.postalOrderPaymentInstructionRequestWith;

public class PaymentInstructionCsvRetrieveTest extends ComponentTestBase {

    public static final String CURRENT_DATE = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

    @Test
    public void givenPostalOrderPaymentInstructionDetails_retrieveAsCvs() throws Exception {
        PostalOrder proposedPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(500)
            .currency("GBP")
            .postalOrderNumber("000000").build();

        restActions
            .post("/postal-orders", proposedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .getCsv("/payment-instructions")
            .andExpect(status().isOk())
            .andExpect(result -> {
                Assert.assertEquals(String.format("Daily sequential payment ID%sDate%sPayee name%sCheque Amount%s" +
                    "Postal Order Amount%sCash Amount%sCard Amount%sAllPay Amount%sAction Taken%sCase ref no.%s" +
                    "Fee Amount%sFee code%sFee description%s1%s%s%sMr Payer Payer%s%s500%s%s%s%s%s%s%s%s%s",
                    SEPARATOR, SEPARATOR, SEPARATOR, SEPARATOR, SEPARATOR, SEPARATOR, SEPARATOR, SEPARATOR, SEPARATOR,
                    SEPARATOR, SEPARATOR, SEPARATOR, EOL, SEPARATOR, CURRENT_DATE, SEPARATOR, SEPARATOR, SEPARATOR,
                    SEPARATOR, SEPARATOR, SEPARATOR, SEPARATOR, SEPARATOR, SEPARATOR, SEPARATOR, SEPARATOR, EOL),
                    result.getResponse().getContentAsString());
            });

    }
}

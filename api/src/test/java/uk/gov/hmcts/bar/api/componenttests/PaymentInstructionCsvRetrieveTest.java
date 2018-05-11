package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Assert;
import org.junit.Test;
import uk.gov.hmcts.bar.api.data.model.PostalOrder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.bar.api.converters.PaymentInstructionsCsvConverter.EOL;
import static uk.gov.hmcts.bar.api.converters.PaymentInstructionsCsvConverter.SEPARATOR;
import static uk.gov.hmcts.bar.api.data.model.PostalOrder.postalOrderPaymentInstructionRequestWith;

public class PaymentInstructionCsvRetrieveTest extends ComponentTestBase {

    public static final String CURRENT_DATE = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

    @Test
    public void givenPostalOrderPaymentInstructionDetails_retrieveAsCvs() throws Exception {
        PostalOrder proposedPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(533)
            .currency("GBP").status("D")
            .postalOrderNumber("000000").build();

        restActions
            .post("/postal-orders", proposedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isCreated());

        restActions
            .getCsv("/payment-instructions")
            .andExpect(status().isOk())
            .andExpect(result -> {
                Assert.assertEquals(String.format("\"Daily sequential payment ID\"%s\"Date\"%s\"Payee name\"%s\"Cheque Amount\"%s" +
                    "\"Postal Order Amount\"%s\"Cash Amount\"%s\"Card Amount\"%s\"AllPay Amount\"%s\"Action Taken\"%s\"Case ref no.\"%s" +
                    "\"Fee Amount\"%s\"Fee code\"%s\"Fee description\"%s\"1\"%s\"%s\"%s\"Mr Payer Payer\"%s\"\"%s\"5.33\"" +
                    "%s\"\"%s\"\"%s\"\"%s\"\"%s\"\"%s\"\"%s\"\"%s\"\"%s",
                    SEPARATOR, SEPARATOR, SEPARATOR, SEPARATOR, SEPARATOR, SEPARATOR, SEPARATOR, SEPARATOR, SEPARATOR,
                    SEPARATOR, SEPARATOR, SEPARATOR, EOL, SEPARATOR, CURRENT_DATE, SEPARATOR, SEPARATOR, SEPARATOR,
                    SEPARATOR, SEPARATOR, SEPARATOR, SEPARATOR, SEPARATOR, SEPARATOR, SEPARATOR, SEPARATOR, EOL),
                    result.getResponse().getContentAsString());
            });

    }
}

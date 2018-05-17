package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Assert;
import org.junit.Test;
import uk.gov.hmcts.bar.api.data.model.PostalOrder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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

        PostalOrder updatedPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(533)
            .currency("GBP").status("V")
            .postalOrderNumber("000000").build();

        restActions
            .put("/postal-orders/1", updatedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isOk());

        updatedPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(533)
            .currency("GBP").status("A")
            .postalOrderNumber("000000").build();

        restActions
            .put("/postal-orders/1", updatedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isOk());

        updatedPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(533)
            .currency("GBP").status("TTB")
            .postalOrderNumber("000000").build();

        restActions
            .put("/postal-orders/1", updatedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isOk());


        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter paramFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        String paramStartDate = currentDate.format(paramFormatter);
        DateTimeFormatter actualFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String actualStartDate = currentDate.format(actualFormatter);


        restActions
            .getCsv("/payment-instructions?startDate=" + paramStartDate)
            .andExpect(status().isOk())
            .andExpect(result -> {
                Assert.assertEquals("\"Daily sequential payment ID\",\"Date\",\"Payee name\",\"Cheque Amount\",\"Postal Order Amount\",\"Cash Amount\",\"Card Amount\",\"AllPay Amount\",\"Action Taken\",\"Case ref no.\",\"Fee Amount\",\"Fee code\",\"Fee description\",\"Recorded user\",\"Recorded time\",\"Validated user\",\"Validated time\",\"Approved user\",\"Approved time\",\"Transferred to BAR user\",\"Transferred to BAR time\"\n" +
                        "\"1\",\""+actualStartDate+"\",\"Mr Payer Payer\",\"\",\"5.33\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"John Doe\",\""+actualStartDate+"\",\"John Doe\",\""+actualStartDate+"\",\"John Doe\",\""+actualStartDate+"\",\"John Doe\",\""+actualStartDate+"\"\n",
                    result.getResponse().getContentAsString());
            });
    }
}

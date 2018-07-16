package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Assert;
import org.junit.Test;
import uk.gov.hmcts.bar.api.data.model.PostalOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

        PostalOrder validatedPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(533)
            .currency("GBP").status("V")
            .postalOrderNumber("000000").build();

        PostalOrder approvedPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(533)
            .currency("GBP").status("A")
            .postalOrderNumber("000000").build();


        PostalOrder ttbPostalOrderPaymentInstructionRequest = postalOrderPaymentInstructionRequestWith()
            .payerName("Mr Payer Payer")
            .amount(533)
            .currency("GBP").status("TTB")
            .postalOrderNumber("000000").build();

        restActions
            .post("/postal-orders", proposedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isCreated());
        restActions
            .put("/postal-orders/1", validatedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isOk());
        restActions
            .put("/postal-orders/1", approvedPostalOrderPaymentInstructionRequest)
            .andExpect(status().isOk());
        restActions
            .put("/postal-orders/1", ttbPostalOrderPaymentInstructionRequest)
            .andExpect(status().isOk());
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDate currentDate = LocalDate.now();
        String expectedPaymentDate = currentDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        DateTimeFormatter paramFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        String paramStartDate = currentDate.format(paramFormatter);
        DateTimeFormatter actualFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");
        String actualStartDateTime = currentDateTime.format(actualFormatter);

        restActions
            .getCsv("/payment-instructions?startDate=" + paramStartDate)
            .andExpect(status().isOk())
            .andExpect(result -> {
                System.out.println(result.getResponse().getContentAsString());
                Assert.assertEquals("\"Daily sequential payment ID\",\"Date\",\"Payee name\",\"Cheque Amount\",\"Postal Order Amount\",\"Cash Amount\",\"Card Amount\",\"AllPay Amount\",\"Action Taken\",\"Case ref no.\",\"BGC Slip No.\",\"Fee Amount\",\"Fee code\",\"Fee description\",\"Recorded user\",\"Recorded time\",\"Validated user\",\"Validated time\",\"Approved user\",\"Approved time\",\"Transferred to BAR user\",\"Transferred to BAR time\"\n" +
                        "\"1\",\"" + expectedPaymentDate + "\",\"Mr Payer Payer\",\"\",\"5.33\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"1234-fn 1234-ln\",\""+actualStartDateTime+"\",\"1234-fn 1234-ln\",\""+actualStartDateTime+"\",\"1234-fn 1234-ln\",\""+actualStartDateTime+"\",\"1234-fn 1234-ln\",\""+actualStartDateTime+"\"\n",
                    result.getResponse().getContentAsString());
            });
    }
}

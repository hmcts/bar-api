package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Assert;
import org.junit.Test;
import uk.gov.hmcts.bar.api.data.model.Card;
import uk.gov.hmcts.bar.api.data.model.CashPaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PostalOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.bar.api.data.model.Card.cardWith;
import static uk.gov.hmcts.bar.api.data.model.PostalOrder.postalOrderPaymentInstructionRequestWith;

public class PaymentInstructionCsvRetrieveTest extends ComponentTestBase {
    public static final String SEPARATOR = ",";
    @Test
    public void givenPostalOrderPaymentInstructionDetails_retrieveAsCsv() throws Exception {
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
            .postalOrderNumber("000000").bgcNumber("123456").build();


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
        DateTimeFormatter actualFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");
        String recordedDateTime = currentDateTime.format(actualFormatter);
        LocalDate currentDate = LocalDate.now();
        String paymentDate = currentDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        DateTimeFormatter paramFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        String paramStartDate = currentDate.format(paramFormatter);
        String recordedUser = "1234-fn 1234-ln";
        String expectedHeader =convertLine(CashPaymentInstruction.CSV_TABLE_HEADER);
        String dailySequenceId = "1";
        String payeeName = "Mr Payer Payer";
        String amount = "5.33";
        String bgcNumber = "123456";

        restActions
            .getCsv("/payment-instructions?startDate=" + paramStartDate)
            .andExpect(status().isOk())
            .andExpect(result -> {
                String csv  = result.getResponse().getContentAsString();
                System.out.println(csv);
                int indexOfdailySequenceId = result.getResponse().getContentAsString().indexOf("1");
                String actualHeader = csv.substring(0,indexOfdailySequenceId - 2);
                Assert.assertEquals(expectedHeader,actualHeader);
                Assert.assertTrue(csv.contains(dailySequenceId));
                Assert.assertTrue(csv.contains(paymentDate));
                Assert.assertTrue(csv.contains(payeeName));
                Assert.assertTrue(csv.contains(amount));
                Assert.assertTrue(csv.contains(bgcNumber));
                Assert.assertTrue(csv.contains(recordedUser));
                Assert.assertTrue(csv.contains(recordedDateTime));
            });
    }


    @Test
    public void givenCardPaymentInstructionDetails_retrieveAsCsv() throws Exception {
        Card proposedCardPaymentInstructionRequest = cardWith()
            .payerName("Mr Payer Payer")
            .amount(600)
            .currency("GBP").status("D")
            .authorizationCode("000000").build();

        Card validatedCardPaymentInstructionRequest = cardWith()
            .payerName("Mr Payer Payer")
            .amount(600)
            .currency("GBP").status("V")
            .authorizationCode("000000").build();

        Card approvedCardPaymentInstructionRequest = cardWith()
            .payerName("Mr Payer Payer")
            .amount(600)
            .currency("GBP").status("A")
            .authorizationCode("000000").build();


        Card ttbCardPaymentInstructionRequest = cardWith()
            .payerName("Mr Payer Payer")
            .amount(600)
            .currency("GBP").status("TTB")
            .authorizationCode("000000").build();

        restActions
            .post("/postal-orders", proposedCardPaymentInstructionRequest)
            .andExpect(status().isCreated());
        restActions
            .put("/postal-orders/1", validatedCardPaymentInstructionRequest)
            .andExpect(status().isOk());
        restActions
            .put("/postal-orders/1", approvedCardPaymentInstructionRequest)
            .andExpect(status().isOk());
        restActions
            .put("/postal-orders/1", ttbCardPaymentInstructionRequest)
            .andExpect(status().isOk());

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter actualFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");
        String recordedDateTime = currentDateTime.format(actualFormatter);
        LocalDate currentDate = LocalDate.now();
        String paymentDate = currentDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        DateTimeFormatter paramFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        String paramStartDate = currentDate.format(paramFormatter);
        String recordedUser = "1234-fn 1234-ln";
        String expectedHeader =convertLine(CashPaymentInstruction.CSV_TABLE_HEADER);
        String dailySequenceId = "1";
        String payeeName = "Mr Payer Payer";
        String amount = "6.00";

        restActions
            .getCsv("/payment-instructions?startDate=" + paramStartDate)
            .andExpect(status().isOk())
            .andExpect(result -> {
                String csv  = result.getResponse().getContentAsString();
                System.out.println(csv);
                int indexOfdailySequenceId = result.getResponse().getContentAsString().indexOf("1");
                String actualHeader = csv.substring(0,indexOfdailySequenceId - 2);
                Assert.assertEquals(expectedHeader,actualHeader);
                Assert.assertTrue(csv.contains(dailySequenceId));
                Assert.assertTrue(csv.contains(paymentDate));
                Assert.assertTrue(csv.contains(payeeName));
                Assert.assertTrue(csv.contains(amount));
                Assert.assertTrue(csv.contains(recordedUser));
                Assert.assertTrue(csv.contains(recordedDateTime));
            });
    }



    private String convertLine(String[] line){
        return Arrays.stream(line).reduce("", (s, s2) -> s + SEPARATOR + (s2 == null ? "\"\"" : replaceSeparator(s2))).substring(1);
    }

    private String replaceSeparator(String source){
        return "\"" + source.replaceAll("\"", "\"\"") + "\"";
    }

}

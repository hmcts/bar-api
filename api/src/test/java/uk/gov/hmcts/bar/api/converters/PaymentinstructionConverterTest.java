package uk.gov.hmcts.bar.api.converters;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.http.MockHttpOutputMessage;
import uk.gov.hmcts.bar.api.data.exceptions.PaymentInstructionConverterException;
import uk.gov.hmcts.bar.api.data.model.CardPaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.CashPaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.bar.api.converters.CsvConverter.EOL;
import static uk.gov.hmcts.bar.api.converters.CsvConverter.SEPARATOR;

public class PaymentinstructionConverterTest {

    private List<PaymentInstruction> paymentInstructions = new ArrayList<>();
    private MockHttpOutputMessage message = new MockHttpOutputMessage();

    private static final String S = SEPARATOR;
    private static final String HEADER = "Daily sequential payment ID" + S + "Date" + S + "Payee name" + S +
        "Cheque Amount" + S + "Postal Order Amount" + S + "Cash Amount" + S + "Card Amount" + S + "AllPay Amount" + S +
        "Action Taken" + S + "Case ref no." + S + "Fee Amount" + S + "Fee code" + S + "Fee description" + EOL;
    public static final String CURRENT_DATE = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

    @Before
    public void setup(){
        paymentInstructions.add(CashPaymentInstruction.cashPaymentInstructionWith()
            .amount(10000)
            .currency("GBP")
            .payerName("John Doe, Bill")
            .build());
        paymentInstructions.add(CardPaymentInstruction.cardPaymentInstructionWith()
            .amount(2000)
            .currency("GBP")
            .payerName("Jane Doe,Alice")
            .build());
    }

    @Test(expected = PaymentInstructionConverterException.class)
    public void whenRequestNonPaymentInstruction_thenShouldThrow() throws IOException {
        PaymentInstruction paymentInstruction = new PaymentInstruction();

        PaymentInstructionCsvConverter converter = new PaymentInstructionCsvConverter();
        converter.writeInternal(paymentInstruction, message);
    }

    @Test
    public void testCashPaymentInstructionCsv() throws IOException {
        PaymentInstructionsCsvConverter converter = new PaymentInstructionsCsvConverter();
        converter.writeInternal(paymentInstructions, message);

        Assert.assertEquals(HEADER +
            "0" + S + CURRENT_DATE + S + "John Doe Bill" + S  + S  + S + "10000" + S  + S  + S  + S  + S  + S  + S  + EOL +
            "0" + S + CURRENT_DATE + S + "Jane Doe Alice" + S  + S  + S  + S + "2000" + S  + S  + S  + S  + S  + S  + EOL,
            message.getBodyAsString());
    }

    @Test(expected = PaymentInstructionConverterException.class)
    public void testInvalidPaymentInstruction() throws IOException {
        PaymentInstructionsCsvConverter converter = new PaymentInstructionsCsvConverter();
        paymentInstructions.add(new PaymentInstruction());
        converter.writeInternal(paymentInstructions, message);
    }
}

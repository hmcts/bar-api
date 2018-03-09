package uk.gov.hmcts.bar.api.converters;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.http.MockHttpOutputMessage;
import uk.gov.hmcts.bar.api.data.model.CardPaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.CashPaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static uk.gov.hmcts.bar.api.converters.PaymentInstructionsCsvConverter.EOL;
import static uk.gov.hmcts.bar.api.converters.PaymentInstructionsCsvConverter.SEPARATOR;
import static uk.gov.hmcts.bar.api.converters.PaymentInstructionsCsvConverter.CSV_MEDIA_TYPE;

public class PaymentinstructionConverterTest {

    private List<PaymentInstruction> paymentInstructions = new ArrayList<>();
    private MockHttpOutputMessage message = new MockHttpOutputMessage();

    private static final String S = SEPARATOR;
    private static final String HEADER = "\"Daily sequential payment ID\"" + S + "\"Date\"" + S + "\"Payee name\"" + S +
        "\"Cheque Amount\"" + S + "\"Postal Order Amount\"" + S + "\"Cash Amount\"" + S + "\"Card Amount\"" + S + "\"AllPay Amount\"" + S +
        "\"Action Taken\"" + S + "\"Case ref no.\"" + S + "\"Fee Amount\"" + S + "\"Fee code\"" + S + "\"Fee description\"" + EOL;
    public static final String CURRENT_DATE = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

    @Before
    public void setup(){
        paymentInstructions.add(CashPaymentInstruction.cashPaymentInstructionWith()
            .amount(10050)
            .currency("GBP")
            .payerName("John Doe, \"Bill\"")
            .build());
        paymentInstructions.add(CardPaymentInstruction.cardPaymentInstructionWith()
            .amount(2065)
            .currency("GBP")
            .payerName("\"Jane\" Doe,Alice")
            .build());
        paymentInstructions.add(CardPaymentInstruction.cardPaymentInstructionWith()
            .amount(3432)
            .currency("GBP")
            .payerName("\"Jane\" Doe,Alice")
            .build());
    }

    @Test
    public void testCashPaymentInstructionCsv() throws IOException {
        PaymentInstructionsCsvConverter converter = new PaymentInstructionsCsvConverter();
        CaseReference caseReference1 = CaseReference.caseReferenceWith().caseReference("1234").build();
        CaseReference caseReference2 = CaseReference.caseReferenceWith().caseReference("1234").build();
        paymentInstructions.get(1).setCaseReferences(Arrays.asList(caseReference1));
        paymentInstructions.get(2).setCaseReferences(Arrays.asList(caseReference2));

        caseReference2.setCaseFeeDetails(Arrays.asList(
            CaseFeeDetail.caseFeeDetailWith()
                .feeDescription("This is a \"fee\" description")
                .feeCode("x0123")
                .amount(4567)
                .build(),
            CaseFeeDetail.caseFeeDetailWith()
                .feeDescription("This is another `fee` description")
                .feeCode("x0123")
                .amount(55555)
                .build()
        ));
        converter.write(paymentInstructions, CSV_MEDIA_TYPE, message);

        Assert.assertEquals(HEADER +
            "\"0\"" + S + "\"" + CURRENT_DATE + "\"" + S + "\"John Doe, \"\"Bill\"\"\"" + S + "\"\"" + S + "\"\"" + S +
            "\"" + "100.50" + "\"" + S + "\"\"" + S + "\"\"" + S + "\"\"" + S + "\"\"" + S + "\"\"" + S + "\"\"" + S + "\"\"" + EOL +
            "\"0\"" + S + "\"" + CURRENT_DATE + "\"" + S + "\"\"\"Jane\"\" Doe,Alice\"" + S + "\"\"" + S + "\"\"" + S +
            "\"\"" + S + "\"" + "20.65" + "\"" + S + "\"\"" + S + "\"\"" + S + "\"1234\"" + S + "\"\"" + S + "\"\"" + S + "\"\"" + EOL +
            "\"0\"" + S + "\"" + CURRENT_DATE + "\"" + S + "\"\"\"Jane\"\" Doe,Alice\"" + S + "\"\"" + S + "\"\"" + S +
            "\"\"" + S + "\"" + "34.32" + "\"" + S + "\"\"" + S + "\"\"" + S + "\"1234\"" + S + "\"45.67\"" + S +
            "\"x0123\"" + S + "\"This is a \"\"fee\"\" description\"" + EOL +
            "\"\"" + S + "\"\"" + S + "\"\"" + S + "\"\"" + S + "\"\"" + S +
            "\"\"" + S + "\"\"" + S + "\"\"" + S + "\"\"" + S + "\"1234\"" + S + "\"555.55\"" + S +
            "\"x0123\"" + S + "\"This is another `fee` description\"" + EOL,
            message.getBodyAsString());
    }

}

package uk.gov.hmcts.bar.api.data.utils;

import uk.gov.hmcts.bar.api.data.exceptions.PaymentInstructionConverterException;
import uk.gov.hmcts.bar.api.data.model.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PaymentInstructionUtil {

    public static String[] createTableHeader(){
        return new String[] {"Daily sequential payment ID", "Date", "Payee name" , "Cheque Amount",
            "Postal Order Amount", "Cash Amount", "Card Amount" , "AllPay Amount", "Action Taken", "Case ref no.",
            "Fee Amount", "Fee code", "Fee description"};
    }

    public static List<String[]> flattenPaymentInstruction(PaymentInstruction paymentInstruction){

        List<String[]> paymentLines = new ArrayList<>();

        if (paymentInstruction.getCaseReferences() != null){
            paymentInstruction.getCaseReferences().forEach(reference -> {
                if (reference.getCaseFeeDetails() != null){
                    reference.getCaseFeeDetails().forEach(caseFeeDetail -> {
                        String[] line = new String[13];
                        line[9] = reference.getCaseReference();
                        line[10] = caseFeeDetail.getAmount().toString();
                        line[11] = caseFeeDetail.getFeeCode();
                        line[12] = caseFeeDetail.getFeeDescription();
                        paymentLines.add(line);
                    });
                }else{
                    String[] line = new String[13];
                    line[9] = reference.getCaseReference();
                    paymentLines.add(line);
                }
            });
        }

        if (paymentLines.size() == 0){
            paymentLines.add(new String[13]);
        }

        paymentLines.get(0)[0] = Integer.toString(paymentInstruction.getDailySequenceId());
        paymentLines.get(0)[1] = paymentInstruction.getPaymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        paymentLines.get(0)[2] = paymentInstruction.getPayerName();
        paymentLines.get(0)[8] = paymentInstruction.getAction();
        paymentLines.get(0)[findAmountIndex(paymentInstruction)] = paymentInstruction.getAmount() == null ? null : paymentInstruction.getAmount().toString();

        return paymentLines;
    }

    static private int findAmountIndex(PaymentInstruction paymentInstruction){
        Class clazz = paymentInstruction.getClass();
        if (clazz.equals(ChequePaymentInstruction.class)){
            return 3;
        } else if (clazz.equals(PostalOrderPaymentInstruction.class)) {
            return 4;
        } else if (clazz.equals(CashPaymentInstruction.class)){
            return 5;
        } else if (clazz.equals(CardPaymentInstruction.class)){
            return 6;
        } else if (clazz.equals(AllPayPaymentInstruction.class)){
            return 7;
        }
        throw new PaymentInstructionConverterException("The payment instruction type in unknown: " + paymentInstruction.getClass().getName());
    }
}

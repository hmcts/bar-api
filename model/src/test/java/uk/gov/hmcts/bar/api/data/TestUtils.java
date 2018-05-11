package uk.gov.hmcts.bar.api.data;

import uk.gov.hmcts.bar.api.data.model.*;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TestUtils {

    public static PaymentInstruction createSamplePaymentInstruction(String type, int paymentAmount, int[][] caseDetails){
        PaymentInstruction pi = TestUtils.createPaymentInstructions(type, paymentAmount);
        pi.setCaseFeeDetails(Arrays.stream(caseDetails).map(amounts -> TestUtils.createCaseFeeDetail(amounts)).collect(Collectors.toList()));
        pi.setCaseFeeDetails(Arrays.stream(caseDetails).map(amounts -> TestUtils.createCaseFeeDetail(amounts)).collect(Collectors.toList()));
        return pi;
    }

    public static Object[] dataProvider() {
        int paymentAmount = 10000;
        return new Object[][] {
            {paymentAmount, new int[][] {{10000, 0, 0}}, 0},
            {paymentAmount, new int[][] {{10000, 1000, 0}}, -1000},
            {paymentAmount, new int[][] {{10000, 0, 1000}}, 1000},
            {paymentAmount, new int[][] {{10000, 500, 300}}, -200},
            {paymentAmount, new int[][] {{10000, 300, 500}}, 200},
            {paymentAmount, new int[][] {{5000, 0, 0}, {5000, 0, 0}}, 0},
            {paymentAmount, new int[][] {{5000, 0, 0}, {3000, 2000, 0}}, 0},
            {paymentAmount, new int[][] {{5000, 1000, 0}, {3000, 1000, 0}}, 0},
            {paymentAmount, new int[][] {{5000, 1000, 1000}, {3000, 1000, 500}}, 1500},

        };
    }

    public static Object[] dataProviderMultipleCase() {
        int paymentAmount = 20000;
        return new Object[][] {
            {paymentAmount, new int[][] {{10000, 0, 0}}, 0},
            {paymentAmount, new int[][] {{10000, 1000, 0}}, -2000},
            {paymentAmount, new int[][] {{10000, 0, 1000}}, 2000},
            {paymentAmount, new int[][] {{10000, 500, 300}}, -400},
            {paymentAmount, new int[][] {{10000, 300, 500}}, 400},
            {paymentAmount, new int[][] {{5000, 0, 0}, {5000, 0, 0}}, 0},
            {paymentAmount, new int[][] {{5000, 0, 0}, {3000, 2000, 0}}, 0},
            {paymentAmount, new int[][] {{5000, 1000, 0}, {3000, 1000, 0}}, 0},
            {paymentAmount, new int[][] {{5000, 1000, 1000}, {3000, 1000, 500}}, 3000},

        };
    }

    public static PaymentInstruction createPaymentInstructions(String type, int amount) {
        switch (type){
            case "cash":
                return new CashPaymentInstruction("John Doe", amount, "GBP","D");
            case "card":
                return new CardPaymentInstruction("John Doe", amount, "GBP","D","123456");
            case "cheque":
                return new ChequePaymentInstruction("John Doe", amount, "GBP", "1234","D");
            case "postal":
                return new PostalOrderPaymentInstruction("John Doe", amount, "GBP", "1234","D");
            case "all":
                return new AllPayPaymentInstruction("John Doe", amount, "GBP", "1234","D");
            default:
                return new CashPaymentInstruction("John Doe", amount, "GBP","D");
        }
    }

    public static CaseFeeDetail createCaseFeeDetail(int[] amounts){
        CaseFeeDetail cf = CaseFeeDetail.caseFeeDetailWith()
            .amount(amounts[0])
            .feeCode("x00335")
            .feeDescription("Recovery of Land - Online (County Court)")
            .build();
        if (amounts[1] != 0){
            cf.setRefundAmount(amounts[1]);
        }
        if (amounts[2] != 0){
            cf.setRemissionAmount(amounts[2]);
        }
        return cf;
    }
}

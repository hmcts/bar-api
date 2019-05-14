package uk.gov.hmcts.bar.api.data;

import uk.gov.hmcts.bar.api.data.model.*;
import uk.gov.hmcts.bar.api.integration.payhub.data.PayhubCaseFeeDetail;
import uk.gov.hmcts.bar.api.integration.payhub.data.PayhubFullRemission;
import uk.gov.hmcts.bar.api.integration.payhub.data.PayhubPaymentInstruction;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

public class TestUtils {

    public static PaymentInstruction createSamplePaymentInstruction(String type, int paymentAmount, int[][] caseDetails){
        PaymentInstruction pi = TestUtils.createPaymentInstructions(type, paymentAmount);
        pi.setCaseFeeDetails(Arrays.stream(caseDetails).map(amounts -> TestUtils.createCaseFeeDetail(amounts)).collect(Collectors.toList()));
        return pi;
    }

    public static PayhubPaymentInstruction createSamplePayhuPaymentInstruction(int paymentAmount, int[][] caseDetails) {
        PayhubPaymentInstruction ppi = new PayhubPaymentInstruction("John Doe", paymentAmount, "GBP", "TTB");
        ppi.setPaymentDate(LocalDateTime.of(2018, 8, 13, 0, 0));
        ppi.setCaseFeeDetails(Arrays.stream(caseDetails).map(amounts -> TestUtils.createPayhubCaseFeeDetail(amounts)).collect(Collectors.toList()));
        return ppi;
    }

    public static PayhubFullRemission createSampleFullRemissionInstruction(int[] caseDetail) {
        PayhubFullRemission ppi = new PayhubFullRemission("John Doe", "GBP", "TTB");
        ppi.setPaymentDate(LocalDateTime.of(2018, 8, 13, 0, 0));
        ppi.setCaseFeeDetails(Arrays.asList(TestUtils.createPayhubCaseFeeDetail(caseDetail)));
        return ppi;
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
            case "CARD":
                CardPaymentInstruction card = new CardPaymentInstruction("John Doe", amount, "GBP","D","123456");
                card.setId(1);
                card.setPaymentType(PaymentType.paymentTypeWith().id("CARD").name("Card").build());
                return card;
            case "CHEQUE":
                ChequePaymentInstruction cheque = new ChequePaymentInstruction("John Doe", amount, "GBP", "1234","D");
                cheque.setId(1);
                cheque.setPaymentType(PaymentType.paymentTypeWith().id("CHEQUE").name("Cheque").build());
                return cheque;
            case "POSTAL_ORDER":
                PostalOrderPaymentInstruction postal = new PostalOrderPaymentInstruction("John Doe", amount, "GBP", "1234","D");
                postal.setId(1);
                postal.setPaymentType(PaymentType.paymentTypeWith().id("POSTAL_ORDER").name("Postal Order").build());
                return postal;
            case "ALLPAY":
                AllPayPaymentInstruction allpay = new AllPayPaymentInstruction("John Doe", amount, "GBP", "1234","D");
                allpay.setId(1);
                allpay.setPaymentType(PaymentType.paymentTypeWith().id("ALLPAY").name("Allpay").build());
                return allpay;
            case "FULL_REMISSION":
                FullRemissionPaymentInstruction full = new FullRemissionPaymentInstruction("John Doe",  "D","12345678911");
                full.setId(1);
                full.setPaymentType(PaymentType.paymentTypeWith().id("FULL_REMISSION").name("Full Remission").build());
                return full;
            default:
                CashPaymentInstruction cash = new CashPaymentInstruction("John Doe", amount, "GBP","D");
                cash.setId(1);
                cash.setPaymentType(PaymentType.paymentTypeWith().id("CASH").name("Cash").build());
                return cash;
        }
    }

    public static CaseFeeDetail createCaseFeeDetail(int[] amounts) {
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

    public static PayhubCaseFeeDetail createPayhubCaseFeeDetail(int[] amounts) {
        PayhubCaseFeeDetail cf = PayhubCaseFeeDetail.payhubCaseFeeDetailWith()
            .amount(amounts[0])
            .feeCode("x00335")
            .feeDescription("Recovery of Land - Online (County Court)")
            .feeVersion("1")
            .caseReference("12345")
            .build();
        if (amounts[1] != 0){
            cf.setRefundAmount(amounts[1]);
        }
        if (amounts[2] != 0){
            cf.setRemissionAmount(amounts[2]);
            cf.setRemissionAuthorisation("12345678901");
            cf.setRemissionBenefiter("John Doe");
        }
        return cf;
    }
}

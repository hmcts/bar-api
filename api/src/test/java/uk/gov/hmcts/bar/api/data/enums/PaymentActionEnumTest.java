package uk.gov.hmcts.bar.api.data.enums;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class PaymentActionEnumTest {

    @Test
    public void testFindPaymentActionByDisplayValue() {
        Optional<PaymentActionEnum> actionEnum = PaymentActionEnum.findByDisplayValue("Process");
        Assert.assertEquals(PaymentActionEnum.PROCESS, actionEnum.get());

        actionEnum = PaymentActionEnum.findByDisplayValue("Return");
        Assert.assertEquals(PaymentActionEnum.RETURN, actionEnum.get());

        actionEnum = PaymentActionEnum.findByDisplayValue("Refund");
        Assert.assertEquals(PaymentActionEnum.REFUND, actionEnum.get());

        actionEnum = PaymentActionEnum.findByDisplayValue("Suspense");
        Assert.assertEquals(PaymentActionEnum.SUSPENSE, actionEnum.get());

        actionEnum = PaymentActionEnum.findByDisplayValue("Suspense Deficiency");
        Assert.assertEquals(PaymentActionEnum.SUSPENSE_DEFICIENCY, actionEnum.get());

        actionEnum = PaymentActionEnum.findByDisplayValue("Not valid");
        Assert.assertEquals(false, actionEnum.isPresent());

    }
}

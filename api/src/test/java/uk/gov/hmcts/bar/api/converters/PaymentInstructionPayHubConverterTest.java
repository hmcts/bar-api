package uk.gov.hmcts.bar.api.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.hmcts.bar.api.data.TestUtils;
import uk.gov.hmcts.bar.api.data.model.PayHubPayload;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentType;

import java.time.LocalDateTime;

public class PaymentInstructionPayHubConverterTest {

    @Test
    public void testConvert() throws JsonProcessingException {
        PaymentInstruction paymentInstruction = TestUtils.createSamplePaymentInstruction("cheque",10050, new int [][] {{5000, 0, 0}, {5000, 0, 0}});
        paymentInstruction.setId(1);
        paymentInstruction.setPaymentType(new PaymentType("cheque", "Cheque"));
        paymentInstruction.setSiteId("Y431");
        paymentInstruction.setDailySequenceId(2);
        paymentInstruction.setPaymentDate(LocalDateTime.of(2018, 8, 8, 0, 0));
        PayHubPayload payload = PaymentInstructionPayHubConverter.convert(paymentInstruction);
        ObjectMapper objectMapper = new ObjectMapper();
        String strPayload = objectMapper.writeValueAsString(payload);
        Assert.assertEquals(2, payload.getFees().size());
        Assert.assertTrue("The generated json should not contain payment instruction id!",
            !strPayload.contains("payment_instruction_id"));
        Assert.assertEquals("Y431-201808082", payload.getReference());
    }
}

package uk.gov.hmcts.bar.api.utils;

import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.hmcts.bar.api.data.enums.PaymentStatusEnum;
import uk.gov.hmcts.bar.api.data.utils.PaymentStatusEnumConverter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PaymentStatusEnumConverterTest {

    @Test
    public void testSetAsText() {
        PaymentStatusEnumConverter converter = mock(PaymentStatusEnumConverter.class);
        doCallRealMethod().when(converter).setAsText(anyString());
        converter.setAsText("P");
        Mockito.verify(converter).setValue(PaymentStatusEnum.PENDING);
    }
}

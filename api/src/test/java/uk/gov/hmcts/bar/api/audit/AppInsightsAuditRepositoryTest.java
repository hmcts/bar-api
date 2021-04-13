package uk.gov.hmcts.bar.api.audit;

import com.microsoft.applicationinsights.TelemetryClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.bar.api.data.model.BarUser;
import uk.gov.hmcts.bar.api.data.model.CaseFeeDetailRequest;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AppInsightsAuditRepositoryTest {
    TelemetryClient telemetry;

    AppInsightsAuditRepository appInsightsAuditRepository;

    BarUser barUser = BarUser.builder()
        .id("user-id")
        .roles(Set.of("role1","role2"))
        .build();

    @Before
    public void setUp(){
        telemetry = spy(TelemetryClient.class);
        appInsightsAuditRepository = new AppInsightsAuditRepository("key",telemetry);
    }

    @Test
    public void trackPaymentInstructionEvent(){
        PaymentInstruction paymentInstruction = Mockito.mock(PaymentInstruction.class);
        when(paymentInstruction.getId()).thenReturn(1);
        when(paymentInstruction.getPaymentType()).thenReturn(PaymentType.paymentTypeWith().name("CASH").build());
        when(paymentInstruction.getAmount()).thenReturn(100);
        when(paymentInstruction.getStatus()).thenReturn("status");
        when(paymentInstruction.getBgcNumber()).thenReturn("bgc-number");

        appInsightsAuditRepository.trackPaymentInstructionEvent("name",paymentInstruction,barUser);
        verify(telemetry).trackEvent(anyString(),anyMap(),any());
        Mockito.reset(paymentInstruction);

    }

    @Test
    public  void  trackCaseEvent(){
        CaseFeeDetailRequest caseFeeDetailRequest = CaseFeeDetailRequest.caseFeeDetailRequestWith()
                                                        .caseReference("case-reference")
                                                        .feeCode("fee-code")
                                                        .build();
        appInsightsAuditRepository.trackCaseEvent("name",caseFeeDetailRequest,barUser);
        verify(telemetry).trackEvent(anyString(),anyMap(),any());
    }
    @Test
    public void trackEvent(){
        Map mockMap = new HashMap<String,String>();
        mockMap.put("key","value");
        appInsightsAuditRepository.trackEvent("name",mockMap);
        verify(telemetry).trackEvent(anyString(),Mockito.anyMap(),any());
    }

}

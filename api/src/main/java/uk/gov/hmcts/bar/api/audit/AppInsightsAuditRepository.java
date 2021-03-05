package uk.gov.hmcts.bar.api.audit;


import com.google.common.collect.ImmutableMap;
import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.TelemetryConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.bar.api.data.model.BarUser;
import uk.gov.hmcts.bar.api.data.model.CaseFeeDetailRequest;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;

import java.util.HashMap;
import java.util.Map;

@Component
public class AppInsightsAuditRepository implements AuditRepository{

    private final TelemetryClient telemetry;


    @Autowired
    public AppInsightsAuditRepository(@Value("${azure.application-insights.instrumentation-key}") String instrumentationKey,
                                      TelemetryClient telemetry) {
        TelemetryConfiguration.getActive().setInstrumentationKey(instrumentationKey);
        telemetry.getContext().getComponent().setVersion(getClass().getPackage().getImplementationVersion());
        this.telemetry = telemetry;
    }

    public void trackPaymentInstructionEvent(String name, PaymentInstruction paymentInstruction, BarUser barUser){

        Map<String, String> properties = new HashMap<>();
        properties.put("Payment instruction id",paymentInstruction.getId().toString());
        properties.put("User id", barUser.getId());
        properties.put("User role",barUser.getRoles());
        properties.put("Payment type", paymentInstruction.getPaymentType().getName());
        properties.put("Amount", paymentInstruction.getAmount().toString());

        if (null != paymentInstruction.getAction()){
            properties.put("Action",paymentInstruction.getAction());
        }
        properties.put("Status",paymentInstruction.getStatus());

        if (null != paymentInstruction.getBgcNumber()){
            properties.put("BGC Number",paymentInstruction.getBgcNumber());
        }
        telemetry.trackEvent(name, ImmutableMap.copyOf(properties),null);
    }

    public void trackCaseEvent(String name, CaseFeeDetailRequest caseFeeDetailRequest, BarUser barUser){
        Map<String, String> properties = new ImmutableMap.Builder<String, String>()
            .put("User id ", barUser.getId())
            .put("Case reference", caseFeeDetailRequest.getCaseReference())
            .put("Fee code", caseFeeDetailRequest.getFeeCode())
            .build();
        telemetry.trackEvent(name, properties,null);
    }

    @Override
    public void trackEvent(String name, Map<String, String> properties) {
        telemetry.trackEvent(name, properties,null);
    }

}

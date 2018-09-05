package uk.gov.hmcts.bar.api.audit;

import org.springframework.scheduling.annotation.Async;
import uk.gov.hmcts.bar.api.data.model.BarUser;
import uk.gov.hmcts.bar.api.data.model.CaseFeeDetailRequest;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;

import java.util.Map;

@Async
public interface AuditRepository {


    void trackPaymentInstructionEvent(String name, PaymentInstruction paymentInstruction, BarUser barUser);
    void trackCaseEvent(String name, CaseFeeDetailRequest caseFeeDetailRequest, BarUser barUser);
    void trackEvent(String name, Map<String, String> properties);
}

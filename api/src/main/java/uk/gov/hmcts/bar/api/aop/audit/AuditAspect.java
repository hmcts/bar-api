package uk.gov.hmcts.bar.api.aop.audit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.bar.api.audit.AuditRepository;
import uk.gov.hmcts.bar.api.data.exceptions.BarUserNotFoundException;
import uk.gov.hmcts.bar.api.data.model.BarUser;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.service.BarUserService;

import java.lang.reflect.Method;
import java.util.Optional;

@Aspect
@Component
public class AuditAspect {

    private final BarUserService barUserService;
    private final AuditRepository auditRepository;

    @Autowired
    public AuditAspect(BarUserService barUserService, AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
        this.barUserService = barUserService;
    }

    @Around("@annotation(Audit)")
    public Object auditEvent(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // Get the annotation
        Audit audit = method.getAnnotation(Audit.class);

        // Get BAR user
        Optional<BarUser> optBarUser = barUserService.getBarUser();
        BarUser barUser = optBarUser.orElseThrow(()-> new BarUserNotFoundException("Bar user not found"));

        //Get return value
        PaymentInstruction returnValue = (PaymentInstruction) joinPoint.proceed();

        // Make the audit
        auditRepository.trackPaymentInstructionEvent(audit.eventName(), returnValue, barUser);
        return returnValue;
    }
}

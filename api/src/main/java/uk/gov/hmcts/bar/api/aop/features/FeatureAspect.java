package uk.gov.hmcts.bar.api.aop.features;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.ff4j.FF4j;
import org.ff4j.exception.FeatureAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class FeatureAspect {

    public static final String ERROR_MESSAGE = "This function is temporarily unavailable.\nPlease contact support.";

    @Autowired
    private final FF4j ff4j;

    public FeatureAspect(FF4j ff4j) {
        this.ff4j = ff4j;
    }

    @Around("@annotation(Featured)")
    public Object isFeatureIsEnabled(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Featured featured = method.getAnnotation(Featured.class);
        if (!ff4j.check(featured.featureKey())) {
            throw new FeatureAccessException(ERROR_MESSAGE);
        }
        return joinPoint.proceed();
    }
}

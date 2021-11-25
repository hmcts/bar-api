package uk.gov.hmcts.bar.multisite.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class ToUpperCaseAspect {

    @Around("within(@uk.gov.hmcts.bar.multisite.aop.ToUpperCase *) || @annotation(ToUpperCase)")
    public Object converToUpperCase(final ProceedingJoinPoint joinPoint) throws Throwable {

        Object[] arguments = joinPoint.getArgs();
        Object[] convertedArgs = Arrays.stream(arguments).map(o -> {
            if (o instanceof String) {
                return ((String) o).trim().toUpperCase();
            } else {
                return o;
            }
        }).toArray();

        return joinPoint.proceed(convertedArgs);
    }
}

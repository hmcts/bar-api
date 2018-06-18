package uk.gov.hmcts.bar.api.controllers.errors;

import com.google.common.collect.Iterators;
import org.ff4j.exception.FeatureAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uk.gov.hmcts.bar.api.data.exceptions.ResourceNotFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Locale;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Error> entityNotFoundException(ResourceNotFoundException e) {
        LOG.error("Resource not found: " + e.getResourceName());
        String message = String.format("%s for %s=%s was not found", e.getResourceName(), e.getIdName(), e.getIdValue());
        return new ResponseEntity<>(new Error(message), NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        LOG.error("Method argument not valid: " + e.getParameter() + ", " + e.getMessage());
        FieldError firstFieldError = e.getBindingResult().getFieldErrors().get(0);
        String message = firstFieldError.getField() + ": " + messageSource.getMessage(firstFieldError, Locale.getDefault());
        return new ResponseEntity<>(new Error(message), BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Error> handleResourceNotFoundException(ConstraintViolationException e) {
        LOG.debug("Constraint violated: " + e.getConstraintViolations());
        ConstraintViolation<?> violation = e.getConstraintViolations().iterator().next();
        String parameterName = Iterators.getLast(violation.getPropertyPath().iterator()).getName();
        return new ResponseEntity<>(new Error(parameterName + ": " + violation.getMessage()), BAD_REQUEST);
    }

    @ExceptionHandler(FeatureAccessException.class)
    public ResponseEntity<Error> handleFeatureAccessException(FeatureAccessException e) {
        LOG.debug("Feature is not accessible: " + e.getMessage());
        return new ResponseEntity<>(new Error(e.getMessage()), BAD_REQUEST);
    }

}


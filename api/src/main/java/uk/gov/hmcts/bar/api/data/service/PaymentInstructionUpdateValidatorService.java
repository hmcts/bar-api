package uk.gov.hmcts.bar.api.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.bar.api.data.exceptions.PaymentProcessException;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionUpdateRequest;
import uk.gov.hmcts.bar.api.data.validators.ActionValidator;
import uk.gov.hmcts.bar.api.data.validators.FullRemissionValidator;
import uk.gov.hmcts.bar.api.data.validators.UnallocatedAmountValidator;
import uk.gov.hmcts.bar.api.data.validators.Validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PaymentInstructionUpdateValidatorService {

    private final ActionValidator actionValidator;
    private final FullRemissionValidator fullRemissionValidator;
    private final UnallocatedAmountValidator unallocatedAmountValidator;
    private final List<Validator> validators;

    @Autowired
    public PaymentInstructionUpdateValidatorService(
        ActionValidator actionValidator,
        FullRemissionValidator fullRemissionValidator,
        UnallocatedAmountValidator unallocatedAmountValidator
    ) {
        this.actionValidator = actionValidator;
        this.fullRemissionValidator = fullRemissionValidator;
        this.unallocatedAmountValidator = unallocatedAmountValidator;
        validators = Arrays.asList(actionValidator, fullRemissionValidator, unallocatedAmountValidator);
    }

    public void validateAction(PaymentInstruction paymentInstruction, PaymentInstructionUpdateRequest updateRequest) throws PaymentProcessException {
        this.actionValidator.validate(paymentInstruction, updateRequest);
    }

    public void validateFullRemission(PaymentInstruction paymentInstruction, PaymentInstructionUpdateRequest updateRequest) throws PaymentProcessException {
        this.fullRemissionValidator.validate(paymentInstruction, updateRequest);
    }

    public void validateUnallocatedAmount(PaymentInstruction paymentInstruction, PaymentInstructionUpdateRequest updateRequest) throws PaymentProcessException {
        this.unallocatedAmountValidator.validate(paymentInstruction, updateRequest);
    }

    public void validateAll(PaymentInstruction paymentInstruction, PaymentInstructionUpdateRequest updateRequest) throws PaymentProcessException {
        List<PaymentProcessException> exceptions = new ArrayList<>();
        validators.forEach(validator -> {
            try {
                validator.validate(paymentInstruction, updateRequest);
            } catch (PaymentProcessException e) {
                exceptions.add(e);
            }
        });
        if (!exceptions.isEmpty()){
            throw exceptions.get(0);
        }
    }
}

package uk.gov.hmcts.bar.api.data.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.bar.api.data.TestUtils;
import uk.gov.hmcts.bar.api.data.exceptions.PaymentProcessException;
import uk.gov.hmcts.bar.api.data.model.CaseFeeDetail;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionUpdateRequest;
import uk.gov.hmcts.bar.api.data.validators.ActionValidator;
import uk.gov.hmcts.bar.api.data.validators.FullRemissionValidator;
import uk.gov.hmcts.bar.api.data.validators.UnallocatedAmountValidator;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PaymentInstructionUpdateValidatorServiceTest {

    private PaymentInstructionUpdateValidatorService paymentInstructionUpdateValidatorService;

    @Mock
    private UnallocatedAmountService mockUnallocatedAmountService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        paymentInstructionUpdateValidatorService = new PaymentInstructionUpdateValidatorService(
            new ActionValidator(),
            new FullRemissionValidator(),
            new UnallocatedAmountValidator(mockUnallocatedAmountService)
        );
    }

    @Test
    public void testReturnFullRemissionValidationWhenCaseFeeAttached() {
        processFullRemissionValidationWhenCaseFeeAttached("Return");
    }

    @Test
    public void testWithdrawFullRemissionValidationWhenCaseFeeAttached() {
        processFullRemissionValidationWhenCaseFeeAttached("Withdraw");
    }

    @Test
    public void testReturnFullRemissionValidationWhenNoCaseFee() throws PaymentProcessException {
        processFullRemissionValidationWhenNoCaseFee("Return");
    }

    @Test
    public void testWithdrawFullRemissionValidationWhenNoCaseFee() throws PaymentProcessException {
        processFullRemissionValidationWhenNoCaseFee("Withdraw");
    }

    @Test
    public void testProcessFullRemissionValidationWhenNoCaseFee() {
        PaymentInstruction pi = TestUtils.createPaymentInstructions("FULL_REMISSION",10000);
        pi.setCaseFeeDetails(new ArrayList<>());
        try {
            PaymentInstructionUpdateRequest pir = PaymentInstructionUpdateRequest.paymentInstructionUpdateRequestWith()
                .status("V").action("Process").build();
            paymentInstructionUpdateValidatorService.validateAll(pi, pir);
            fail("should fail here");
        } catch (PaymentProcessException ppe) {
            assertEquals("Full Remission must have one and only one fee", ppe.getMessage());
        }
    }

    @Test
    public void testProcessFullRemissionValidationWhenCaseFeeAttached() throws PaymentProcessException {
        PaymentInstruction pi = TestUtils.createPaymentInstructions("FULL_REMISSION",10000);
        pi.setCaseFeeDetails(Arrays.asList(new CaseFeeDetail()));
        PaymentInstructionUpdateRequest pir = PaymentInstructionUpdateRequest.paymentInstructionUpdateRequestWith()
            .status("V").action("Process").build();
        paymentInstructionUpdateValidatorService.validateAll(pi, pir);
        assert true;
    }

    @Test
    public void testReturnCardValidationWhenNoCaseFee() throws PaymentProcessException {
        PaymentInstruction pi = TestUtils.createPaymentInstructions("CARD",10000);
        pi.setCaseFeeDetails(new ArrayList<>());
        PaymentInstructionUpdateRequest pir = PaymentInstructionUpdateRequest.paymentInstructionUpdateRequestWith()
            .status("V").action("Return").build();
        paymentInstructionUpdateValidatorService.validateAll(pi, pir);
        assert true;
    }

    @Test
    public void testReturnCardValidationWhenCaseFeeAttached() throws PaymentProcessException {
        PaymentInstruction pi = TestUtils.createPaymentInstructions("CARD",10000);
        pi.setCaseFeeDetails(Arrays.asList(new CaseFeeDetail()));
        PaymentInstructionUpdateRequest pir = PaymentInstructionUpdateRequest.paymentInstructionUpdateRequestWith()
            .status("V").action("Return").build();
        try {
            paymentInstructionUpdateValidatorService.validateAll(pi, pir);
            fail("should fail here");
        } catch (PaymentProcessException ppe) {
            assertEquals("Please remove all case and fee details before attempting this action.", ppe.getMessage());
        }
    }

    private void processFullRemissionValidationWhenNoCaseFee(String action) throws PaymentProcessException {
        PaymentInstruction pi = TestUtils.createPaymentInstructions("FULL_REMISSION",10000);
        pi.setCaseFeeDetails(new ArrayList<>());
        PaymentInstructionUpdateRequest pir = PaymentInstructionUpdateRequest.paymentInstructionUpdateRequestWith()
            .status("V").action(action).build();
        paymentInstructionUpdateValidatorService.validateAll(pi, pir);
        assert true;
    }

    private void processFullRemissionValidationWhenCaseFeeAttached(String action) {
        PaymentInstruction pi = TestUtils.createPaymentInstructions("FULL_REMISSION",10000);
        pi.setCaseFeeDetails(Arrays.asList(new CaseFeeDetail()));
        try {
            PaymentInstructionUpdateRequest pir = PaymentInstructionUpdateRequest.paymentInstructionUpdateRequestWith()
                .status("V").action(action).build();
            paymentInstructionUpdateValidatorService.validateAll(pi, pir);
            fail("should fail here");
        } catch (PaymentProcessException ppe) {
            assertEquals("Please remove all case and fee details before attempting this action.", ppe.getMessage());
        }
    }
}

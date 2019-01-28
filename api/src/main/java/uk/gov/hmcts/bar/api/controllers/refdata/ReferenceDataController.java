package uk.gov.hmcts.bar.api.controllers.refdata;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.hmcts.bar.api.data.model.PaymentInstructionAction;
import uk.gov.hmcts.bar.api.data.model.PaymentType;
import uk.gov.hmcts.bar.api.data.service.PaymentActionService;
import uk.gov.hmcts.bar.api.data.service.PaymentTypeService;

import java.util.List;

@RestController
@Validated

public class ReferenceDataController {

    private final PaymentTypeService paymentTypeService;

    private final PaymentActionService paymentActionService;


	@Autowired
	public ReferenceDataController(PaymentTypeService paymentTypeService, PaymentActionService paymentActionService) {
		this.paymentTypeService = paymentTypeService;
		this.paymentActionService = paymentActionService;
	}

    @GetMapping("/payment-types")
    public List<PaymentType> getPaymentTypes(){
        return paymentTypeService.getAllPaymentTypes();
    }

    @GetMapping("/payment-action")
    public List<PaymentInstructionAction> getPaymentAction(){
        return paymentActionService.getAllPaymentInstructionAction();
    }



}

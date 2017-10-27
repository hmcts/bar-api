package uk.gov.hmcts.bar.api.controllers.refdata;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.bar.api.model.PaymentType;
import uk.gov.hmcts.bar.api.model.PaymentTypeService;

import java.util.List;

@RestController
@Validated

public class ReferenceDataController {

    private final PaymentTypeService paymentTypeService;


    @Autowired
    public ReferenceDataController(PaymentTypeService paymentTypeService) {
        this.paymentTypeService = paymentTypeService;
    }

    @GetMapping("/payment-types")
    public List<PaymentType> getPaymentTypes(){
        return paymentTypeService.getAllPaymentTypes();
    }



}

package uk.gov.hmcts.bar.api.controllers.refdata;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.bar.api.model.PaymentType;
import uk.gov.hmcts.bar.api.model.ReferenceDataService;

import java.util.List;

@RestController
@Validated

public class ReferenceDataController {

    private final ReferenceDataService referenceDataService;


    @Autowired
    public ReferenceDataController(ReferenceDataService referenceDataService) {
        this.referenceDataService = referenceDataService;
    }

    @GetMapping("/payment-types")
    public List<PaymentType> getPaymentTypes(){
        return referenceDataService.getAllPaymentTypes();
    }



}

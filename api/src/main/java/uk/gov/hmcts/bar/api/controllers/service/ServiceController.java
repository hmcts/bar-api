package uk.gov.hmcts.bar.api.controllers.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.bar.api.controllers.payment.PaymentDtoMapper;
import uk.gov.hmcts.bar.api.model.Service;
import uk.gov.hmcts.bar.api.model.ServiceRepository;

import java.util.List;

@RestController
@Validated
public class ServiceController {

    private final ServiceRepository serviceRepository;

    @Autowired
    public ServiceController(PaymentDtoMapper paymentDtoMapper, ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;

    }

    @GetMapping("/services")
    public List<Service> getServices(){
        return serviceRepository.findAll();
    }


}

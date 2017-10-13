package uk.gov.hmcts.bar.api.controllers.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.bar.api.contract.ServiceDto;
import uk.gov.hmcts.bar.api.model.ServiceRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@Validated
public class ServiceController {

    private final ServiceRepository serviceRepository;
    private final ServiceDtoMapper serviceDtoMapper;

    @Autowired
    public ServiceController(ServiceRepository serviceRepository, ServiceDtoMapper serviceDtoMapper) {
        this.serviceRepository = serviceRepository;
        this.serviceDtoMapper = serviceDtoMapper;

    }

    @GetMapping("/services")
    public List<ServiceDto> getServices(){

        return serviceRepository.findAll().stream().map(serviceDtoMapper::toServiceDto).collect(toList());
    }

}

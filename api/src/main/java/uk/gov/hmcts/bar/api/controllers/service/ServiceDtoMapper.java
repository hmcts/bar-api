package uk.gov.hmcts.bar.api.controllers.service;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.bar.api.contract.ServiceDto;
import uk.gov.hmcts.bar.api.contract.ServiceDto.SubServiceDto;
import uk.gov.hmcts.bar.api.model.Service;
import uk.gov.hmcts.bar.api.model.SubService;

import static java.util.stream.Collectors.toList;

@Component
public  class ServiceDtoMapper {

    public ServiceDto toServiceDto(Service service) {
        return ServiceDto.serviceDtoWith()
            .name(service.getName())
            .subServices(service.getSubServices().stream().map(this::toSubServiceDto).collect(toList()))
            .build();
    }

    public SubServiceDto toSubServiceDto(SubService subService) {
        return SubServiceDto.subServiceDtoWith()
            .name(subService.getName())
            .build();
    }


    public Service toService(ServiceDto serviceDto) {

        return Service.serviceWith()
            .name(serviceDto.getName())
            .subServices(serviceDto.getSubServices().stream().map(this::toSubService).collect(toList()))
            .build();
    }


    public SubService toSubService(SubServiceDto subServiceDto) {
        return SubService.subServiceWith()
            .name(subServiceDto.getName())
            .build();
    }


}

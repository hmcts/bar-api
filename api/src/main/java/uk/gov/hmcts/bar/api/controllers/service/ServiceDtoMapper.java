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
            .id(service.getId())
            .name(service.getName())
            .subServices(service.getSubServices().stream().map(this::toSubServiceDto).collect(toList()))
            .build();
    }

    public SubServiceDto toSubServiceDto(SubService subService) {
        return SubServiceDto.subServiceDtoWith()
            .id(subService.getId())
            .name(subService.getName())
            .build();
    }


    public Service toService(ServiceDto serviceDto) {

        return Service.serviceWith()
            .id(serviceDto.getId())
            .name(serviceDto.getName())
            .subServices(serviceDto.getSubServices().stream().map(this::toSubService).collect(toList()))
            .build();
    }


    public SubService toSubService(SubServiceDto subServiceDto) {
        return SubService.subServiceWith()
            .id(subServiceDto.getId())
            .name(subServiceDto.getName())
            .build();
    }


}

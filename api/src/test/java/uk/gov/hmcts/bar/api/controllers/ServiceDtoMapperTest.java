package uk.gov.hmcts.bar.api.controllers;

import org.junit.Test;
import uk.gov.hmcts.bar.api.contract.ServiceDto;
import uk.gov.hmcts.bar.api.contract.ServiceDto.SubServiceDto;
import uk.gov.hmcts.bar.api.controllers.service.ServiceDtoMapper;
import uk.gov.hmcts.bar.api.model.Service;
import uk.gov.hmcts.bar.api.model.SubService;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceDtoMapperTest {

    private final ServiceDtoMapper serviceDtoMapper = new ServiceDtoMapper();
    @Test
    public void convertsToServiceDto() {
        assertThat(serviceDtoMapper.toServiceDto(
            Service.serviceWith()
                .name("someservice")
                .subServices(Arrays.asList(SubService.subServiceWith().name("somesubservice").build()))
                .build()
            )
        ).isEqualTo(
            ServiceDto.serviceDtoWith()
                .name("someservice")
                .subServices((Arrays.asList(SubServiceDto.subServiceDtoWith().name("somesubservice").build())))
                .build());
    }
    @Test
    public void convertsToService() {
        assertThat(serviceDtoMapper.toService(
            ServiceDto.serviceDtoWith()
                .name("someservice")
                .subServices(Arrays.asList(SubServiceDto.subServiceDtoWith().name("somesubservice").build()))
                .build()
            )
        ).isEqualTo(
            Service.serviceWith()
                .name("someservice")
                .subServices((Arrays.asList(SubService.subServiceWith().name("somesubservice").build())))
                .build());
    }

}

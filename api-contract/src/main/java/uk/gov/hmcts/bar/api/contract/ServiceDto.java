package uk.gov.hmcts.bar.api.contract;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceDto {

    private final String name;
    private final List<SubServiceDto> subServices;

    @JsonCreator
    @Builder(builderMethodName = "serviceDtoWith")
    public ServiceDto(@JsonProperty("name") String name,
                      @JsonProperty("subServices") List<SubServiceDto> subServices) {
        this.name = name;
        this.subServices = subServices;


    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SubServiceDto {
        private String name;


        @JsonCreator
        @Builder(builderMethodName = "subServiceDtoWith")
        public SubServiceDto(@JsonProperty("name") String name) {
            this.name = name;

        }
    }


}

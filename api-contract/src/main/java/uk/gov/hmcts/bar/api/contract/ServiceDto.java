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
    private final Integer id;
    private final String name;
    private final List<SubServiceDto> subServices;

    @JsonCreator
    @Builder(builderMethodName = "serviceDtoWith")
    public ServiceDto(@JsonProperty("id")   Integer id,
                      @JsonProperty("name") String name,
                      @JsonProperty("subServices") List<SubServiceDto> subServices) {
        this.id = id;
        this.name = name;
        this.subServices = subServices;


    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SubServiceDto {
        private Integer id;
        private String name;


        @JsonCreator
        @Builder(builderMethodName = "subServiceDtoWith")
        public SubServiceDto(@JsonProperty("id") Integer id,
            @JsonProperty("name") String name) {
            this.id =id;
            this.name = name;

        }
    }


}

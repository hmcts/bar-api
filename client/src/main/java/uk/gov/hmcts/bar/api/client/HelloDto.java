package uk.gov.hmcts.bar.api.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;



@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(builderMethodName = "helloDtoWith")
public class HelloDto {


    private final String hello;
    

}

package uk.gov.hmcts.bar.api.contract;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Builder(builderMethodName = "helloDtoWith")
public class HelloDto {
    private String hello;
    private String description;
}

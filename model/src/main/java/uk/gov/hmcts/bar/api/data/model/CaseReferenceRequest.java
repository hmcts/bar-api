package uk.gov.hmcts.bar.api.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CaseReferenceRequest {

    @NotNull
    @Pattern(regexp ="^[a-zA-Z0-9]{1,11}",message = "invalid case reference number")
    private String caseReference;

    @JsonCreator
    @Builder(builderMethodName = "caseReferenceRequestWith")
    public CaseReferenceRequest(@JsonProperty("case_reference") String caseReference) {

        this.caseReference = caseReference;

    }
}

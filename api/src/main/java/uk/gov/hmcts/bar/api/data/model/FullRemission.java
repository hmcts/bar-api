package uk.gov.hmcts.bar.api.data.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FullRemission extends PaymentInstructionRequest {

    @Pattern(regexp = "^[a-zA-Z0-9-]{11,11}$", message = "invalid remission reference")
    private String remissionReference;

    @JsonCreator
    @Builder(builderMethodName = "remissionWith")
    public FullRemission(@JsonProperty("payer_name") String payerName,
                     @JsonProperty("status") String status,
                     @JsonProperty("remission_reference") String remissionReference) {

        super(payerName, 0, "GBP", status);
        this.remissionReference = remissionReference;
    }

    @JsonIgnore
    @Override
    public void setAmount(Integer amount) {
    }

    @JsonIgnore
    @Override
    public void setBgcNumber(String bgcNumber) {
    }
    @JsonIgnore
    @Override
    public void setCurrency(String currency) {
    }



}

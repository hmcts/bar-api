package uk.gov.hmcts.bar.api.data.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PaymentInstructionUpdateRequest {

    private String status;
    private String action;
    private Integer actionReason;
    private String actionComment;

    @JsonCreator
    @Builder(builderMethodName = "paymentInstructionUpdateRequestWith")
    public PaymentInstructionUpdateRequest(@JsonProperty("status") String status, @JsonProperty("action") String action
    ) {

        this.status = status;
        this.action = action;

    }

}

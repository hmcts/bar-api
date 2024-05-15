package uk.gov.hmcts.bar.api.data.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Card extends PaymentInstructionRequest {

    @Pattern(regexp = "^[a-zA-Z0-9]{6,6}$", message = "invalid authorization code")
    private String authorizationCode;

    @JsonCreator
    @Builder(builderMethodName = "cardWith")
    public Card(@JsonProperty("payer_name") String payerName,
                @JsonProperty("amount") Integer amount,
                @JsonProperty("currency") String currency,
                @JsonProperty("status") String status,
                @JsonProperty("authorization_code") String authorizationCode) {

        super(payerName,amount,currency,status);
        this.authorizationCode= authorizationCode;


    }
}

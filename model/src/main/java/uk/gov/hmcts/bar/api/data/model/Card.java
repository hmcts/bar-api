package uk.gov.hmcts.bar.api.data.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Card extends PaymentInstructionRequest {

    @JsonCreator
    @Builder(builderMethodName = "cardWith")
    public Card(@JsonProperty("payer_name") String payerName,
                @JsonProperty("amount") Integer amount,
                @JsonProperty("currency") String currency,
                @JsonProperty("status") String status) {

        super(payerName,amount,currency,status);

    }
}
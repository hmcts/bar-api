package uk.gov.hmcts.bar.api.data.model;


import com.fasterxml.jackson.annotation.JsonCreator;
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
public class Cheque extends PaymentInstructionRequest{

    @Pattern(regexp ="^\\d{6,6}$",message = "invalid cheque number")
    private String chequeNumber;

    @JsonCreator
    @Builder(builderMethodName = "chequePaymentInstructionRequestWith")
    public Cheque(@JsonProperty("payer_name") String payerName,
                  @JsonProperty("amount") Integer amount,
                  @JsonProperty("currency") String currency,
                  @JsonProperty("cheque_number") String chequeNumber) {

        super(payerName,amount,currency);
        this.chequeNumber = chequeNumber;
    }

}

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
public class PostalOrder extends PaymentInstructionRequest{

    @Pattern(regexp ="^\\d{1,20}$",message = "invalid all pay transaction id")
    private String postalOrderNumber;

    @JsonCreator
    @Builder(builderMethodName = "postalOrderPaymentInstructionRequestWith")
    public PostalOrder(@JsonProperty("payer_name") String payerName,
                       @JsonProperty("amount") Integer amount,
                       @JsonProperty("currency") String currency,
                       @JsonProperty("postal_order_number") String postalOrderNumber,
                       @JsonProperty("status") String status,
                       @JsonProperty("bgc_number") String bgcNumber) {

        super(payerName,amount,currency,status);
        this.postalOrderNumber = postalOrderNumber;
        this.bgcNumber = bgcNumber;
    }
}

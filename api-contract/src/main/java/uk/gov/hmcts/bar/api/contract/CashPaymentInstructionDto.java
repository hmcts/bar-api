package uk.gov.hmcts.bar.api.contract;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CashPaymentInstructionDto extends PaymentInstructionDto {
    private static final String CASH = "cash";
    @JsonCreator
    @Builder(builderMethodName = "cashPaymentInstructionDtoWith")
    public CashPaymentInstructionDto(@JsonProperty("payer_name") String payerName,
                                       @JsonProperty("amount") Integer amount,
                                       @JsonProperty("currency") String currency
                                       ) {
        super(payerName,amount,currency,CASH);

    }


}

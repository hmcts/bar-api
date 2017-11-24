package uk.gov.hmcts.bar.api.contract;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.Pattern;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostalOrderPaymentInstructionDto extends PaymentInstructionDto {

    @Pattern(regexp ="^\\d{6,6}$",message = "invalid postal order number")
    private final String postalOrderNumber;
    private static final String PAYMENT_INSTRUCTION_TYPE = "postal-orders";

    @JsonCreator
    @Builder(builderMethodName = "postalOrderPaymentInstructionDtoWith")
    public PostalOrderPaymentInstructionDto(@JsonProperty("payer_name") String payerName,
                                     @JsonProperty("amount") Integer amount,
                                     @JsonProperty("currency") String currency,
                                     @JsonProperty("postal_order_number") String postalOrderNumber) {
        super(payerName, amount, currency, PAYMENT_INSTRUCTION_TYPE);
        this.postalOrderNumber = postalOrderNumber;

    }
}

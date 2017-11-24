package uk.gov.hmcts.bar.api.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.Pattern;

@Data
@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@DiscriminatorValue("postal-orders")
public class PostalOrderPaymentInstruction extends PaymentInstruction {

    @NonNull
    @Pattern(regexp ="^\\d{6,6}$",message = "invalid postal order number")
    private String postalOrderNumber;

    @JsonCreator
    @Builder(builderMethodName = "postalOrderPaymentInstructionWith")
    public PostalOrderPaymentInstruction(@JsonProperty("payer_name") String payerName,
                                            @JsonProperty("amount") Integer amount,
                                            @JsonProperty("currency") String currency,
                                            @JsonProperty("postal_order_number") String postalOrderNumber) {
        super(payerName, amount, currency);
        this.postalOrderNumber = postalOrderNumber;

    }
}

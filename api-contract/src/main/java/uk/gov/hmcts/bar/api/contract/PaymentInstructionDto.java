package uk.gov.hmcts.bar.api.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NonNull;
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "payment_type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = ChequePaymentInstructionDto.class, name = "cheque"),
    @JsonSubTypes.Type(value = CashPaymentInstructionDto.class, name = "cash")})
public class PaymentInstructionDto {

    @NonNull
    private String payerName;
    @NonNull
    private Integer amount;
    @NonNull
    private String paymentType;
    @NonNull
    private String currency;
    public PaymentInstructionDto(String payerName, Integer amount, String currency) {
        this.payerName = payerName;
        this.amount = amount;
        this.currency = currency;

    }

}




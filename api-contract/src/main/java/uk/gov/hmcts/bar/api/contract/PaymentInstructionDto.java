package uk.gov.hmcts.bar.api.contract;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Pattern;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = "siteId", allowGetters=true)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "payment_type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = ChequePaymentInstructionDto.class, name = "cheques"),
    @JsonSubTypes.Type(value = CashPaymentInstructionDto.class, name = "cash"),
    @JsonSubTypes.Type(value = PostalOrderPaymentInstructionDto.class, name = "postal-orders"),
    @JsonSubTypes.Type(value = AllPayPaymentInstructionDto.class, name = "allpay")})
public class PaymentInstructionDto {

    @NonNull
    private String payerName;
    @NonNull
    private Integer amount;
    @NonNull
    private final String paymentType;

    private String siteId;
    private String paymentDate;
    private int dailySequenceId;

    @NonNull
    @Pattern(regexp ="(?:GBP)",message = "invalid currency")
    private String currency;
    public PaymentInstructionDto(String payerName, Integer amount, String currency,String paymentType) {
        this.payerName = payerName;
        this.amount = amount;
        this.currency = currency;
        this.paymentType = paymentType;

    }

}




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
public class ChequePaymentInstructionDto extends PaymentInstructionDto {
    private final String sortCode;
    private final String accountNumber;
    private final String instrumentNumber;
    private static final String CHEQUE = "cheque";
    @JsonCreator
    @Builder(builderMethodName = "chequePaymentInstructionDtoWith")
    public ChequePaymentInstructionDto(@JsonProperty("payer_name") String payerName,
                                       @JsonProperty("amount") Integer amount,
                                       @JsonProperty("currency") String currency,
                                       @JsonProperty("sort_code") String sortCode,
                                       @JsonProperty("account_number") String accountNumber,
                                       @JsonProperty("instrument_number") String instrumentNumber) {
        super(payerName,amount,currency,CHEQUE);
        this.sortCode = sortCode;
        this.accountNumber = accountNumber ;
        this.instrumentNumber = instrumentNumber;
    }


}

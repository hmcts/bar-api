package uk.gov.hmcts.bar.api.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import uk.gov.hmcts.bar.api.contract.ServiceDto.SubServiceDto;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Builder(builderMethodName = "paymentDtoWith")
public class PaymentDto {
    @JsonProperty("payee_name")
    private String payeeName;
    @JsonProperty("payment_receipt_type")
    private String paymentReceiptType;
    @JsonProperty("counter_code")
    private String counterCode;
    @JsonProperty("event_type")
    private String eventType;
    @JsonProperty("fee_code")
    private String feeCode;
    @JsonProperty("sort_code")
    private String sortCode;
    @JsonProperty("account_number")
    private String accountNumber;
    @JsonProperty("cheque_number")
    private String chequeNumber;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("payment_type")
    private Integer paymentType;
    @JsonProperty("payment_date")
    private String paymentDate;
    private String amount;
    @JsonProperty("created_by_user_id")
    private String createdByUserId;
    @JsonProperty("updated_by_user_id")
    private String updatedByUserId;
    @JsonProperty("update_date")
    private String updateDate;
    private List<CaseDto> cases;


    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString(callSuper = true)
    @Builder(builderMethodName = "caseDtoWith")
    public static class CaseDto {
        private String reference;
        private String jurisdiction1;
        private String jurisdiction2;
        @JsonProperty("sub_service")
        private SubServiceDto subService;

    }

}

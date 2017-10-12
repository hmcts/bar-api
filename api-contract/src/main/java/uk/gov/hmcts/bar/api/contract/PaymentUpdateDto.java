package uk.gov.hmcts.bar.api.contract;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Builder(builderMethodName = "paymentUpdateDtoWith")
public class PaymentUpdateDto {
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
    @JsonProperty("currency_type")
    private String currencyType;
    @JsonProperty("payment_type")
    private Integer paymentType;
    @JsonProperty("payment_date")
    private String paymentDate;
    private Integer amount;
    @JsonProperty("created_by_user_id")
    private String createdByUserId;
    @JsonProperty("updated_by_user_id")
    private String updatedByUserId;
    @JsonProperty("update_date")
    private String updateDate;
    private List<CaseUpdateDto> cases;


    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString(callSuper = true)
    @Builder(builderMethodName = "caseUpdateDtoWith")
    public static class CaseUpdateDto {
        private String reference;
        private String jurisdiction1;
        private String jurisdiction2;
        @JsonProperty("sub_service_id")
        private String subServiceId;

    }


}

package uk.gov.hmcts.bar.api.contract;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.List;
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Builder(builderMethodName = "paymentUpdateDtoWith")
public class PaymentUpdateDto {
    private String payeeName;
    private String paymentReceiptType;
    private String counterCode;
    private String eventType;
    private String feeCode;
    private String sortCode;
    private String accountNumber;
    private String chequeNumber;
    private String currency;
    private Integer paymentTypeId;
    private String paymentDate;
    private Integer amount;
    private String createdByUserId;
    private String updatedByUserId;
    private String updateDate;
    private List<CaseUpdateDto> cases;

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
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
        private Integer subServiceId;

    }

}

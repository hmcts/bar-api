package uk.gov.hmcts.bar.api.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import uk.gov.hmcts.bar.api.contract.ServiceDto.SubServiceDto;

import java.util.List;
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Builder(builderMethodName = "paymentDtoWith")
public class PaymentDto {
    private String payeeName;
    private String paymentReceiptType;
    private String counterCode;
    private String eventType;
    private String feeCode;
    private String sortCode;
    private String accountNumber;
    private String chequeNumber;
    private String currency;
    private PaymentTypeDto paymentType;
    private String paymentDate;
    private Integer amount;
    private String createdByUserId;
    private String updatedByUserId;
    private String updateDate;
    private List<CaseDto> cases;

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
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
        private SubServiceDto subService;

    }
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString(callSuper = true)
    @Builder(builderMethodName = "paymentTypeDtoWith")
    public static class PaymentTypeDto {
        private Integer id;
        private String name;

    }

}

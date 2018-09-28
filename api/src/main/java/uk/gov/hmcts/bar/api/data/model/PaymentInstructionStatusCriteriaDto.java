package uk.gov.hmcts.bar.api.data.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Builder(builderMethodName = "paymentInstructionStatusCriteriaDto")
public class PaymentInstructionStatusCriteriaDto {
    private String userId;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}


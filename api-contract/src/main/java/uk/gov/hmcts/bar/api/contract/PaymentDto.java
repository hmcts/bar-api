package uk.gov.hmcts.bar.api.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Builder(builderMethodName = "paymentDtoWith")
public class PaymentDto {
    private String payeeName;
    private String caseReference;
    private String paymentChannel;
    private String paymentDate;
    private Integer amount;

}

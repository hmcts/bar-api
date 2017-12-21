package uk.gov.hmcts.bar.api.data.model;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Builder(builderMethodName = "searchCriteriaRequestWith")
public class PaymentInstructionSearchCriteriaDto {

	private String siteId;
    private Integer dailySequenceId;
    private String payerName;
    private String chequeNumber;
    private String postalOrderNumer;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String allPayInstructionId;
    private String paymentType;

}

package uk.gov.hmcts.bar.api.data.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class PaymentInstructionReportLine {

    private Integer         dailyId;
    private LocalDateTime   date;
    private String          name;
    private Integer         checkAmount;
    private Integer         postalOrderAmount;
    private Integer         cashAmount;
    private Integer         cardAmount;
    private Integer         allPayAmount;
    private String          action;
    private String          caseRef;
    private Integer         feeAmount;
    private String          feeCode;
    private String          feeDescription;


}

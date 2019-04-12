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

    private String         dailyId;
    private LocalDateTime   date;
    private String          name;
    private Integer         checkAmount;
    private Integer         postalOrderAmount;
    private Integer         cashAmount;
    private Integer         cardAmount;
    private Integer         allPayAmount;
    private String          action;
    private String          caseRef;
    private String          bgcNumber;
    private Integer         feeAmount;
    private String          feeCode;
    private String          feeDescription;
    private Integer         remissionAmount;
    private String          remissionReference;
    private String          recordedUser;
    private LocalDateTime   recordedTime;
    private String          validatedUser;
    private LocalDateTime   validatedTime;
    private String          approvedUser;
    private LocalDateTime   approvedTime;
    private String          transferredToBarUser;
    private LocalDateTime   transferredToBarTime;
    private String          sentToPayhub;
}


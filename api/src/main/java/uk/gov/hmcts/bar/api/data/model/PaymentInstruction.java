package uk.gov.hmcts.bar.api.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonIgnoreProperties(value = {"case_references"}, allowGetters = true)
@DiscriminatorColumn(name = "payment_type_id")
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class PaymentInstruction extends BasePaymentInstruction {

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "paymentInstructionId", referencedColumnName = "id")
    private List<CaseFeeDetail> caseFeeDetails;

    private Integer actionReason;
    @Length(max = 2048)
    private String actionComment;

    public PaymentInstruction(String payerName, Integer amount, String currency, String status) {
        super(payerName, amount, currency, status);
    }

    public List<PaymentInstructionReportLine> flattenPaymentInstruction() {

        List<PaymentInstructionReportLine> paymentLines = new ArrayList<>();

        if (this.getCaseFeeDetails() != null) {
            this.getCaseFeeDetails().forEach(caseFeeDetail -> {
                PaymentInstructionReportLine line = new PaymentInstructionReportLine();
                line.setCaseRef(caseFeeDetail.getCaseReference());
                line.setFeeAmount(caseFeeDetail.getAmount());
                line.setFeeCode(caseFeeDetail.getFeeCode());
                line.setFeeDescription(caseFeeDetail.getFeeDescription());
                paymentLines.add(line);
            });
        }

        if (paymentLines.size() == 0) {
            paymentLines.add(new PaymentInstructionReportLine());
        }

        paymentLines.get(0).setDailyId(this.getDailySequenceId());
        paymentLines.get(0).setDate(this.getPaymentDate());
        paymentLines.get(0).setName(this.getPayerName());
        paymentLines.get(0).setAction(this.getAction());
        paymentLines.get(0).setBgcNumber(this.getBgcNumber());
        fillAmount(paymentLines.get(0));
        setUserActivity(paymentLines);
        paymentLines.get(0).setSentToPayhub(this.getSentToPayhub());

        return paymentLines;
    }

    private void setUserActivity(List<PaymentInstructionReportLine> paymentLines){

        Iterator<PaymentInstructionStatusHistory> iterator = this.getPaymentInstructionStatusHistory().iterator();
        while (iterator.hasNext()){
            PaymentInstructionStatusHistory statusHistory = iterator.next();
            if(statusHistory.getStatus().equals("D")){
                paymentLines.get(0).setRecordedUser(statusHistory.getBarUserFullName());
                paymentLines.get(0).setRecordedTime(statusHistory.getStatusUpdateTime());
            }
            if(statusHistory.getStatus().equals("V")){
                paymentLines.get(0).setValidatedUser(statusHistory.getBarUserFullName());
                paymentLines.get(0).setValidatedTime(statusHistory.getStatusUpdateTime());
            }
            if(statusHistory.getStatus().equals("A")){
                paymentLines.get(0).setApprovedUser(statusHistory.getBarUserFullName());
                paymentLines.get(0).setApprovedTime(statusHistory.getStatusUpdateTime());
            }
            if(statusHistory.getStatus().equals("TTB") || statusHistory.getStatus().equals("C")){
                paymentLines.get(0).setTransferredToBarUser(statusHistory.getBarUserFullName());
                paymentLines.get(0).setTransferredToBarTime(statusHistory.getStatusUpdateTime());
            }
        }

    }

    public abstract void fillAmount(PaymentInstructionReportLine reportRow);
    public abstract void setBgcNumber(String bgcNumber);
}

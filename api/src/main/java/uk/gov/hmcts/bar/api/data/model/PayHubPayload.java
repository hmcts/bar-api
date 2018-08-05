package uk.gov.hmcts.bar.api.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * {
 *  "amount": 0,
 *  "payment_method": "CASH",
 *  "reference": "string",
 *  "service": "CMC",
 *  "currency": "GBP",
 *  "external_reference": "string",
 *  "external_provider": "string",
 *  "giro_slip_no": "string",
 *  "site_id": "string",
 *  "fees": [
 *      {
 *          "calculated_amount": 0,
 *          "ccd_case_number": "string",
 *          "code": "string",
 *          "memo_line": "string",
 *          "natural_account_code": "string",
 *          "reference": "string",
 *          "version": "string",
 *          "volume": 0
 *      }
 *  ]
 * }
 */

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.ALWAYS)
@Builder
public class PayHubPayload {

    private long amount;
    private String paymentMethod;
    private String reference;
    private String service;
    private String currency;
    private String externalReference;
    private String externalProvider;
    private String giroSlipNo;
    private String siteId;
    private List<PayHubPayloadFees> fees;
    @JsonIgnore
    private int paymentInstructionId;

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.ALWAYS)
    @Builder
    public static class PayHubPayloadFees {
        private long calculatedAmount;
        private String ccdCaseNumber;
        private String code;
        private String memoLine;
        private String naturalAccountCode;
        private String reference;
        private String version;
        private int volume;
    }

}

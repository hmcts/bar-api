package uk.gov.hmcts.bar.api.converters;

import uk.gov.hmcts.bar.api.data.model.PayHubPayload;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.bar.api.data.model.PayHubPayload.PayHubPayloadFees;

public class PaymentInstructionPayHubConverter {

    public static final String SERVICE = "BAR";

    public static PayHubPayload convert(PaymentInstruction pi) {
        List<PayHubPayloadFees> fees = pi.getCaseFeeDetails().stream().map(caseFeeDetail -> PayHubPayloadFees.builder()
            .calculatedAmount(caseFeeDetail.getAmount())
            .reference(createReference(pi))
            .code(caseFeeDetail.getFeeCode())
            .version(caseFeeDetail.getFeeVersion())
            .build()).collect(Collectors.toList());
        return PayHubPayload.builder()
            .paymentInstructionId(pi.getId())
            .amount(pi.getAmount())
            .paymentMethod(pi.getPaymentType().getId())
            .reference(createReference(pi))
            .service(SERVICE)
            .currency(pi.getCurrency())
            .externalReference(pi.getExternalReference())
            .giroSlipNo(pi.getBgcNumber())
            .siteId(pi.getSiteId())
            .fees(fees)
            .build();
    }

    private static String createReference(PaymentInstruction pi) {
        return pi.getSiteId() + "-" + pi.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + Integer.toString(pi.getDailySequenceId());
    }
}

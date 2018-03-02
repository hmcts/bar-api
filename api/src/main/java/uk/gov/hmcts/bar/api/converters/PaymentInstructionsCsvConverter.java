package uk.gov.hmcts.bar.api.converters;

import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.utils.PaymentInstructionUtil;

import java.util.ArrayList;
import java.util.List;

public class PaymentInstructionsCsvConverter extends CsvConverter<List<PaymentInstruction>> {

    @Override
    protected boolean supports(Class<?> clazz) {
        return List.class.isAssignableFrom(clazz);
    }

    @Override
    List<String[]> flattenEntity(List<PaymentInstruction> paymentInstructions) {
        List<String[]> paymentLines = new ArrayList<>();
        paymentLines.add(PaymentInstructionUtil.createTableHeader());
        for (PaymentInstruction paymentInstruction : paymentInstructions){
            paymentLines.addAll(PaymentInstructionUtil.flattenPaymentInstruction(paymentInstruction));
        }
        return paymentLines;
    }
}

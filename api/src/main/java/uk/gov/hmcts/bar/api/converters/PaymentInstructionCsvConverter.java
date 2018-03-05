package uk.gov.hmcts.bar.api.converters;

import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.utils.PaymentInstructionUtil;

import java.util.ArrayList;
import java.util.List;

public class PaymentInstructionCsvConverter extends CsvConverter<PaymentInstruction> {


    @Override
    protected boolean supports(Class<?> clazz) {
        return PaymentInstruction.class.isAssignableFrom(clazz);
    }


    @Override
    List<String[]> flattenEntity(PaymentInstruction entity) {
        List<String[]> paymentLines = new ArrayList<>();
        paymentLines.add(PaymentInstructionUtil.createTableHeader());
        paymentLines.addAll(PaymentInstructionUtil.flattenPaymentInstruction(entity));
        return paymentLines;
    }
}

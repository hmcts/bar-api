package uk.gov.hmcts.bar.api.data.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import uk.gov.hmcts.bar.api.data.enums.PaymentStatusEnum;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;

import java.beans.FeatureDescriptor;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Util {

    public  static String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
            .map(FeatureDescriptor::getName)
            .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
            .toArray(String[]::new);
    }
    
    public static List<PaymentInstruction> updateStatusDisplayValue(final List<PaymentInstruction> paymentInstructions) {
		return paymentInstructions.stream().map(paymentInstruction -> {
			paymentInstruction
					.setStatus(PaymentStatusEnum.getPaymentStatusEnum(paymentInstruction.getStatus()).displayValue());
			return paymentInstruction;
		}).collect(Collectors.toList());
	}

}

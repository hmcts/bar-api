package uk.gov.hmcts.bar.api.data.utils;

import java.beans.FeatureDescriptor;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.criteria.CriteriaBuilder.In;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import uk.gov.hmcts.bar.api.data.enums.PaymentActionEnum;
import uk.gov.hmcts.bar.api.data.enums.PaymentStatusEnum;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;

public interface Util {

    public  static String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
            .map(FeatureDescriptor::getName)
            .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
            .toArray(String[]::new);
    }
    
	public static List<PaymentInstruction> updateStatusAndActionDisplayValue(
			final List<PaymentInstruction> paymentInstructions) {
		return paymentInstructions.stream().map(paymentInstruction -> {
			paymentInstruction
					.setStatus(PaymentStatusEnum.getPaymentStatusEnum(paymentInstruction.getStatus()).displayValue());
			if (paymentInstruction.getAction() != null) {
				paymentInstruction.setAction(
						PaymentActionEnum.getPaymentActionEnum(paymentInstruction.getAction()).displayValue());
			}
			return paymentInstruction;
		}).collect(Collectors.toList());
	}
    
	public static In<String> getListOfStatuses(In<String> inCriteriaForStatus, String status) {
		if (inCriteriaForStatus == null) {
			return null;
		}
		String[] statusArray = status.split(",");
		if (statusArray != null && statusArray.length > 0) {
			for (String statusValue : statusArray) {
				inCriteriaForStatus.value(statusValue);
			}
		} else {
			inCriteriaForStatus.value(status);
		}
		return inCriteriaForStatus;
	}

}

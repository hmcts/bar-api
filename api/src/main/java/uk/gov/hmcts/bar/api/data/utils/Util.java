package uk.gov.hmcts.bar.api.data.utils;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import uk.gov.hmcts.bar.api.data.enums.PaymentStatusEnum;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionUserStats;

import javax.persistence.criteria.CriteriaBuilder.In;
import java.beans.FeatureDescriptor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Util {
	
	static final String POST_CLERK_ROLE = "bar-post-clerk";
	static final String FEE_CLERK_ROLE = "bar-fee-clerk";
	static final String SR_FEE_CLERK_ROLE = "bar-senior-clerk";
	static final String DELIVERY_MANAGER_ROLE = "bar-delivery-manager";

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
				paymentInstruction.setAction(paymentInstruction.getAction());
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


    public static String getFormattedDateTime(LocalDateTime localDateTime, DateTimeFormatter dateFormatter){
        return (localDateTime == null || dateFormatter == null) ? null : localDateTime.format(dateFormatter);
    }
    
    public static boolean isUserDeliveryManager(String userRoles) {
    	return StringUtils.containsIgnoreCase(userRoles, DELIVERY_MANAGER_ROLE);
    }
    
    public static boolean isUserSrFeeClerk(String userRoles) {
    	return StringUtils.containsIgnoreCase(userRoles, SR_FEE_CLERK_ROLE);
    }
    
	public static MultiMap createMultimapFromList(List<PaymentInstructionUserStats> piStatsList) {
		MultiMap paymentInstructionStatsUserMap = new MultiValueMap();
		piStatsList.forEach(pius -> paymentInstructionStatsUserMap.put(pius.getBarUserId(), pius));
		return paymentInstructionStatsUserMap;
	}

}

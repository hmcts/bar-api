package uk.gov.hmcts.bar.api.data.utils;

import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import uk.gov.hmcts.bar.api.data.enums.BarUserRoleEnum;
import uk.gov.hmcts.bar.api.data.enums.PaymentStatusEnum;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionStaticsByUser;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionUserStats;

import javax.persistence.criteria.CriteriaBuilder.In;
import java.beans.FeatureDescriptor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public interface Util {

    static String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
            .map(FeatureDescriptor::getName)
            .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
            .toArray(String[]::new);
    }

    static List<PaymentInstruction> updateStatusAndActionDisplayValue(
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

    static In<String> getInCriteriaWithStringValues(In<String> inCriteria, String columnName) {
        if (inCriteria == null) {
            return null;
        }
        String[] valueArray = columnName.split(",");
        if (valueArray != null && valueArray.length > 0) {
            for (String statusValue : valueArray) {
                inCriteria.value(statusValue);
            }
        } else {
            inCriteria.value(columnName);
        }
        return inCriteria;
    }
    
	static In<Integer> getInCriteriaWithIntegerValues(In<Integer> inCriteria, String columnName) {
		if (inCriteria == null) {
			return null;
		}
		String[] valueArray = columnName.split(",");
		if (valueArray != null && valueArray.length > 0) {
			for (String statusValue : valueArray) {
				inCriteria.value(Integer.valueOf(statusValue));
			}
		} else {
			inCriteria.value(Integer.valueOf(columnName));
		}
		return inCriteria;
	}
    
	static List<PaymentInstructionStaticsByUser> getFilteredPisList(
			List<PaymentInstructionStaticsByUser> paymentInstructionStaticsByUserObjects) {
		Map<Integer, PaymentInstructionStaticsByUser> pisMap = new HashMap<>();
		paymentInstructionStaticsByUserObjects.forEach(pisByUser -> {
			Integer key = pisByUser.getPaymentInstructionId();
			if (pisMap.containsKey(key)) {
				PaymentInstructionStaticsByUser insertedObj = pisMap.get(key);
				if (insertedObj.getUpdateTime().isBefore(pisByUser.getUpdateTime())) {
					pisMap.put(key, pisByUser);
				}
			} else {
				pisMap.put(key, pisByUser);
			}
		});
		return new ArrayList<>(pisMap.values());
	}

    static String getFormattedDateTime(LocalDateTime localDateTime, DateTimeFormatter dateFormatter) {
        return (localDateTime == null || dateFormatter == null) ? null : localDateTime.format(dateFormatter);
    }

    static boolean isUserDeliveryManager(String userRoles) {
        return StringUtils.containsIgnoreCase(userRoles, BarUserRoleEnum.BAR_DELIVERY_MANAGER.getIdamRole());
    }

    static boolean isUserSrFeeClerk(String userRoles) {
        return StringUtils.containsIgnoreCase(userRoles, BarUserRoleEnum.BAR_SENIOR_CLERK.getIdamRole());
    }

    static MultiMap createMultimapFromList(List<PaymentInstructionUserStats> piStatsList) {
        MultiMap paymentInstructionStatsUserMap = new MultiValueMap();
        piStatsList.forEach(pius -> paymentInstructionStatsUserMap.put(pius.getBarUserId(), pius));
        return paymentInstructionStatsUserMap;
    }
    
	static MultiMap createMultimapFromPisByUserList(List<PaymentInstructionStaticsByUser> pisByUserList) {
		MultiMap paymentInstructionStatsByUserMapTemp = new MultiValueMap();
		MultiMap paymentInstructionStatsMap = new MultiValueMap();
		pisByUserList.forEach(piByUser -> paymentInstructionStatsByUserMapTemp.put(piByUser.getBarUserId(), piByUser));
		Set<String> keys = paymentInstructionStatsByUserMapTemp.keySet();
		for (String key : keys) {
			List<PaymentInstructionStaticsByUser> pisByUserListInner = (List<PaymentInstructionStaticsByUser>) paymentInstructionStatsByUserMapTemp
					.get(key);
			if (pisByUserList.size() > 0) {
				PaymentInstructionUserStats piuStats = new PaymentInstructionUserStats(
						pisByUserListInner.get(0).getBarUserId(), pisByUserListInner.get(0).getBarUserFullName(),
						(long) pisByUserListInner.size());
				Integer[] piArray = new Integer[pisByUserListInner.size()];
				int i = 0;
				for (PaymentInstructionStaticsByUser pisByUserObj : pisByUserListInner) {
					piArray[i++] = pisByUserObj.getPaymentInstructionId();
				}
				piuStats.setListOfPaymentInstructions(piArray);
				paymentInstructionStatsMap.put(key, piuStats);
			}
		}
		return paymentInstructionStatsMap;
	}

}

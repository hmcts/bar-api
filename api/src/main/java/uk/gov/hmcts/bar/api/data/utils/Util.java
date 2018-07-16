package uk.gov.hmcts.bar.api.data.utils;

import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
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

    String SR_FEE_CLERK_ROLE = "bar-senior-clerk";
    String DELIVERY_MANAGER_ROLE = "bar-delivery-manager";

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

    static In<String> getListOfStatuses(In<String> inCriteriaForStatus, String status) {
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

    static String getFormattedDateTime(LocalDateTime localDateTime, DateTimeFormatter dateFormatter) {
        return (localDateTime == null || dateFormatter == null) ? null : localDateTime.format(dateFormatter);
    }

    static boolean isUserDeliveryManager(String userRoles) {
        return StringUtils.containsIgnoreCase(userRoles, DELIVERY_MANAGER_ROLE);
    }

    static boolean isUserSrFeeClerk(String userRoles) {
        return StringUtils.containsIgnoreCase(userRoles, SR_FEE_CLERK_ROLE);
    }

    static MultiMap createMultimapFromList(List<PaymentInstructionUserStats> piStatsList) {
        MultiMap paymentInstructionStatsUserMap = new MultiValueMap();
        piStatsList.forEach(pius -> paymentInstructionStatsUserMap.put(pius.getBarUserId(), pius));
        return paymentInstructionStatsUserMap;
    }

}

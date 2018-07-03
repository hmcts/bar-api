package uk.gov.hmcts.bar.api.data.service;


import com.google.common.collect.Lists;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.ff4j.FF4j;
import org.ff4j.exception.FeatureAccessException;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.bar.api.data.enums.PaymentActionEnum;
import uk.gov.hmcts.bar.api.data.enums.PaymentStatusEnum;
import uk.gov.hmcts.bar.api.data.exceptions.PaymentInstructionNotFoundException;
import uk.gov.hmcts.bar.api.data.model.*;
import uk.gov.hmcts.bar.api.data.repository.BankGiroCreditRepository;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionRepository;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionStatusRepository;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionsSpecifications;
import uk.gov.hmcts.bar.api.data.utils.Util;

import java.time.LocalDate;
import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;


@Service
@Transactional
public class PaymentInstructionService {

    private static final Logger LOG = getLogger(PaymentInstructionService.class);

    public static final String SITE_ID = "BR01";
    private static final int PAGE_NUMBER = 0;
    private static final int MAX_RECORDS_PER_PAGE = 200;
    private PaymentInstructionRepository paymentInstructionRepository;
    private PaymentInstructionStatusRepository paymentInstructionStatusRepository;
    private PaymentReferenceService paymentReferenceService;
    private final BarUserService barUserService;
    private final BankGiroCreditRepository bankGiroCreditRepository;
    private final FF4j ff4j;


    public PaymentInstructionService(PaymentReferenceService paymentReferenceService, PaymentInstructionRepository paymentInstructionRepository,
                                     BarUserService barUserService,
                                     PaymentInstructionStatusRepository paymentInstructionStatusRepository,
                                     FF4j ff4j,
                                     BankGiroCreditRepository bankGiroCreditRepository
                                     ) {
        this.paymentReferenceService = paymentReferenceService;
        this.paymentInstructionRepository = paymentInstructionRepository;
        this.barUserService = barUserService;
        this.paymentInstructionStatusRepository = paymentInstructionStatusRepository;
        this.ff4j = ff4j;
        this.bankGiroCreditRepository = bankGiroCreditRepository;
    }

    public PaymentInstruction createPaymentInstruction(PaymentInstruction paymentInstruction) {
        String userId = barUserService.getCurrentUserId();

        PaymentReference nextPaymentReference = paymentReferenceService.getNextPaymentReferenceSequenceBySite(SITE_ID);
        paymentInstruction.setSiteId(SITE_ID);
        paymentInstruction.setDailySequenceId(nextPaymentReference.getDailySequenceId());
        paymentInstruction.setStatus(PaymentStatusEnum.DRAFT.dbKey());
        paymentInstruction.setUserId(userId);
        PaymentInstruction savedPaymentInstruction = paymentInstructionRepository.saveAndRefresh(paymentInstruction);
        savePaymentInstructionStatus(savedPaymentInstruction, userId);
        return savedPaymentInstruction;
    }

    public List<PaymentInstruction> getAllPaymentInstructions(PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto) {

        paymentInstructionSearchCriteriaDto.setSiteId(SITE_ID);
        PaymentInstructionsSpecifications paymentInstructionsSpecification = new PaymentInstructionsSpecifications(paymentInstructionSearchCriteriaDto);
        Sort sort = new Sort(Sort.Direction.DESC, "paymentDate");
        Pageable pageDetails = new PageRequest(PAGE_NUMBER, MAX_RECORDS_PER_PAGE, sort);

        return Lists.newArrayList(paymentInstructionRepository
            .findAll(paymentInstructionsSpecification.getPaymentInstructionsSpecification(), pageDetails)
            .iterator());
    }
    
	public List<PaymentInstruction> getPaymentInstructionsRejectedByDMByUser(String userId) {
		return Lists.newArrayList(paymentInstructionRepository.getPaymentInstructionsRejectedByDMByUser(userId,
				PaymentStatusEnum.REJECTEDBYDM.dbKey(), PaymentStatusEnum.APPROVED.dbKey()).iterator());
	}

    public PaymentInstruction getPaymentInstruction(Integer id) {
        Optional<PaymentInstruction> op = paymentInstructionRepository.findById(id);
        return op.orElse(null);
    }

    public void deletePaymentInstruction(Integer id) {
        try {
            paymentInstructionRepository.deleteById(id);
        } catch (EmptyResultDataAccessException erdae) {
            LOG.error("Resource not found: " + erdae.getMessage(), erdae);
            throw new PaymentInstructionNotFoundException(id);
        }

    }

    public PaymentInstruction submitPaymentInstruction(Integer id, PaymentInstructionUpdateRequest paymentInstructionUpdateRequest) {
        if (!checkIfActionEnabled(paymentInstructionUpdateRequest)) {
            throw new FeatureAccessException(paymentInstructionUpdateRequest.getAction() + " is not allowed");
        }
        String userId = barUserService.getCurrentUserId();
        Optional<PaymentInstruction> optionalPaymentInstruction = paymentInstructionRepository.findById(id);
        PaymentInstruction existingPaymentInstruction = optionalPaymentInstruction
            .orElseThrow(() -> new PaymentInstructionNotFoundException(id));
        String[] nullPropertiesNamesToIgnore = Util.getNullPropertyNames(paymentInstructionUpdateRequest);
        BeanUtils.copyProperties(paymentInstructionUpdateRequest, existingPaymentInstruction, nullPropertiesNamesToIgnore);
        existingPaymentInstruction.setUserId(userId);
        savePaymentInstructionStatus(existingPaymentInstruction, userId);
        return paymentInstructionRepository.saveAndRefresh(existingPaymentInstruction);
    }

    public PaymentInstruction updatePaymentInstruction(Integer id, PaymentInstructionRequest paymentInstructionRequest) {
        String userId = barUserService.getCurrentUserId();
        Optional<PaymentInstruction> optionalPaymentInstruction = paymentInstructionRepository.findById(id);
        PaymentInstruction existingPaymentInstruction = optionalPaymentInstruction
            .orElseThrow(() -> new PaymentInstructionNotFoundException(id));

        // handle bgc number
        if (paymentInstructionRequest.getBgcNumber() != null) {
            BankGiroCredit bgc = bankGiroCreditRepository.findByBgcNumber(paymentInstructionRequest.getBgcNumber())
                .orElseGet(() -> bankGiroCreditRepository.save(new BankGiroCredit(paymentInstructionRequest.getBgcNumber(), SITE_ID)));
            existingPaymentInstruction.setBgcNumber(bgc.getBgcNumber());
        }

        String[] nullPropertiesNamesToIgnore = Util.getNullPropertyNames(paymentInstructionRequest);
        BeanUtils.copyProperties(paymentInstructionRequest, existingPaymentInstruction, nullPropertiesNamesToIgnore);
        existingPaymentInstruction.setUserId(userId);
        savePaymentInstructionStatus(existingPaymentInstruction, userId);
        return paymentInstructionRepository.saveAndRefresh(existingPaymentInstruction);
    }

    public List<PaymentInstruction> getAllPaymentInstructionsByCaseReference(String caseReference) {
        return paymentInstructionRepository.findByCaseReference(caseReference);
    }

    public MultiMap getPaymentInstructionStats(String userRole, String status) {
		MultiMap paymentInstructionStatsUserMap = new MultiValueMap();
		BarUser user = barUserService.getBarUser();
		if (user == null) {
			LOG.error("Invalid BarUser.");
			return paymentInstructionStatsUserMap;
		}
		if (Util.isUserSrFeeClerk(user.getRoles())) {
			List<PaymentInstructionUserStats> paymentInstructionRejByDMList = paymentInstructionStatusRepository
					.getPaymentInstructionsRejectedByDM(PaymentStatusEnum.REJECTEDBYDM.dbKey());
			paymentInstructionRejByDMList
					.forEach(pirej -> paymentInstructionStatsUserMap.put(pirej.getBarUserId(), pirej));
		}
		List<PaymentInstructionUserStats> paymentInstructionInStatusList = paymentInstructionStatusRepository
				.getPaymentInstructionsByStatusByUserGroup(userRole, status);
		paymentInstructionInStatusList.forEach(pius -> paymentInstructionStatsUserMap.put(pius.getBarUserId(), pius));
		return paymentInstructionStatsUserMap;
    }

    private void savePaymentInstructionStatus(PaymentInstruction pi, String userId) {
        PaymentInstructionStatusReferenceKey pisrKey = new PaymentInstructionStatusReferenceKey(pi.getId(),
            pi.getStatus());
        PaymentInstructionStatus pis = new PaymentInstructionStatus(pisrKey, userId);
        paymentInstructionStatusRepository.save(pis);
    }

    public Map<Integer, List<PaymentInstructionStatusHistory>> getStatusHistortMapForTTB(LocalDate startDate, LocalDate endDate) {

        if (null != endDate && startDate.isAfter(endDate)) {
            LOG.error("PaymentInstructionService - Error while generating daily fees csv file. Incorrect start and end dates ");
            return Collections.EMPTY_MAP;
        }

        if (null == endDate || startDate.equals(endDate)) {
            endDate = startDate.plusDays(1);
        } else {
            endDate = endDate.plusDays(1);
        }

        List<PaymentInstructionStatusHistory> statusHistoryList = paymentInstructionStatusRepository.getPaymentInstructionStatusHistoryForTTB
            (startDate.atStartOfDay(), endDate.atStartOfDay());

        final Map<Integer, List<PaymentInstructionStatusHistory>> statusHistoryMapByPaymentInstructionId = new HashMap<>();
        for (final PaymentInstructionStatusHistory statusHistory : statusHistoryList) {
            if (statusHistoryMapByPaymentInstructionId.get(statusHistory.getPaymentInstructionId()) == null) {
                List<PaymentInstructionStatusHistory> listByPaymentInstructionId = new ArrayList<>();
                listByPaymentInstructionId.add(statusHistory);
                statusHistoryMapByPaymentInstructionId.put(statusHistory.getPaymentInstructionId(), listByPaymentInstructionId);
            } else {
                statusHistoryMapByPaymentInstructionId.get(statusHistory.getPaymentInstructionId()).add(statusHistory);
            }

        }
        return statusHistoryMapByPaymentInstructionId;
    }

    public List<PaymentInstruction> getAllPaymentInstructionsByTTB(LocalDate startDate, LocalDate endDate) {
        Map<Integer, List<PaymentInstructionStatusHistory>> statusHistortMapForTTB = getStatusHistortMapForTTB(startDate, endDate);
        Iterator<Map.Entry<Integer, List<PaymentInstructionStatusHistory>>> iterator = statusHistortMapForTTB.entrySet().iterator();
        List<PaymentInstruction> paymentInstructionsList = new ArrayList<>();
        while (iterator.hasNext()) {
            Map.Entry<Integer, List<PaymentInstructionStatusHistory>> entry = iterator.next();
            PaymentInstruction paymentInstruction = paymentInstructionRepository.getOne(entry.getKey());
            paymentInstruction.setPaymentInstructionStatusHistory(entry.getValue());
            paymentInstructionsList.add(paymentInstruction);

        }
        return paymentInstructionsList;
    }

    private boolean checkIfActionEnabled(PaymentInstructionUpdateRequest paymentInstructionUpdateRequest){
        boolean[] ret = { true };
        String action = paymentInstructionUpdateRequest.getAction();
        PaymentActionEnum.findByDisplayValue(action).ifPresent(paymentActionEnum -> {
            ret[0] = ff4j.check(paymentActionEnum.featureKey());
        });
        return ret[0];
    }


}

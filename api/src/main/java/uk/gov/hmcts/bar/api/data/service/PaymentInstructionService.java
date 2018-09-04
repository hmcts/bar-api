package uk.gov.hmcts.bar.api.data.service;


import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import uk.gov.hmcts.bar.api.controllers.payment.PaymentInstructionController;
import uk.gov.hmcts.bar.api.data.enums.PaymentActionEnum;
import uk.gov.hmcts.bar.api.data.enums.PaymentStatusEnum;
import uk.gov.hmcts.bar.api.data.exceptions.PaymentInstructionNotFoundException;
import uk.gov.hmcts.bar.api.data.model.BankGiroCredit;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionRequest;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionSearchCriteriaDto;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionStaticsByUser;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionStats;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionStatus;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionStatusHistory;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionStatusReferenceKey;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionUpdateRequest;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionUserStats;
import uk.gov.hmcts.bar.api.data.model.PaymentReference;
import uk.gov.hmcts.bar.api.data.repository.BankGiroCreditRepository;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionRepository;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionStatusRepository;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionsSpecifications;
import uk.gov.hmcts.bar.api.data.model.*;
import uk.gov.hmcts.bar.api.data.repository.*;
import uk.gov.hmcts.bar.api.data.utils.Util;
import uk.gov.hmcts.bar.api.integration.payhub.data.PayhubPaymentInstruction;


@Service
@Transactional
public class PaymentInstructionService {

    public static final String STAT_GROUP_DETAILS = "stat-group-details";
    public static final String STAT_DETAILS = "stat-details";

    private static final Logger LOG = getLogger(PaymentInstructionService.class);

    private static final List<String> GROUPED_TYPES = Arrays.asList("CHEQUE", "POSTAL_ORDER");

    public static final String SITE_ID = "Y431";
    private static final int PAGE_NUMBER = 0;
    private static final int MAX_RECORDS_PER_PAGE = 200;
    private PaymentInstructionRepository paymentInstructionRepository;
    private PaymentInstructionStatusRepository paymentInstructionStatusRepository;
    private PaymentReferenceService paymentReferenceService;
    private final BarUserService barUserService;
    private final BankGiroCreditRepository bankGiroCreditRepository;
    private final FF4j ff4j;
    private PaymentTypeService paymentTypeService;
    private final PayhubPaymentInstructionRepository payhubPaymentInstructionRepository;


    public PaymentInstructionService(PaymentReferenceService paymentReferenceService, PaymentInstructionRepository paymentInstructionRepository,
                                     BarUserService barUserService,
                                     PaymentInstructionStatusRepository paymentInstructionStatusRepository,
                                     FF4j ff4j,
                                     BankGiroCreditRepository bankGiroCreditRepository,
                                     PaymentTypeService paymentTypeService,
                                     PayhubPaymentInstructionRepository payhubPaymentInstructionRepository
                                     ) {
        this.paymentReferenceService = paymentReferenceService;
        this.paymentInstructionRepository = paymentInstructionRepository;
        this.barUserService = barUserService;
        this.paymentInstructionStatusRepository = paymentInstructionStatusRepository;
        this.ff4j = ff4j;
        this.bankGiroCreditRepository = bankGiroCreditRepository;
        this.paymentTypeService = paymentTypeService;
        this.payhubPaymentInstructionRepository = payhubPaymentInstructionRepository;
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
        PaymentInstructionsSpecifications<PaymentInstruction> paymentInstructionsSpecification = new PaymentInstructionsSpecifications<>(paymentInstructionSearchCriteriaDto,paymentTypeService);
        Sort sort = new Sort(Sort.Direction.DESC, "paymentDate");
        Pageable pageDetails = PageRequest.of(PAGE_NUMBER, MAX_RECORDS_PER_PAGE, sort);

		Specification<PaymentInstruction> piSpecification = null;
		if (paymentInstructionSearchCriteriaDto.getMultiplePiIds() != null) {
			piSpecification = paymentInstructionsSpecification.getPaymentInstructionsMultipleIdSpecification();
		} else {
			piSpecification = paymentInstructionsSpecification.getPaymentInstructionsSpecification();
		}

		return Lists.newArrayList(paymentInstructionRepository.findAll(piSpecification, pageDetails).iterator());
    }

    public List<PayhubPaymentInstruction> getAllPaymentInstructionsForPayhub(
        PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto
    ) {

        paymentInstructionSearchCriteriaDto.setSiteId(SITE_ID);
        PaymentInstructionsSpecifications<PayhubPaymentInstruction> paymentInstructionsSpecification =
            new PaymentInstructionsSpecifications<>(paymentInstructionSearchCriteriaDto, paymentTypeService);

        Specification<PayhubPaymentInstruction> piForPayhubSpecification = paymentInstructionsSpecification.getPaymentInstructionsSpecification();
        return payhubPaymentInstructionRepository.findAll(piForPayhubSpecification);
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

	public MultiMap getPaymentInstructionStats(String status) {
		List<PaymentInstructionUserStats> paymentInstructionInStatusList = paymentInstructionStatusRepository
				.getPaymentInstructionsByStatusGroupedByUser(status);
		return Util.createMultimapFromList(paymentInstructionInStatusList);
	}

	public MultiMap getPaymentInstructionStatsByCurrentStatusGroupedByOldStatus(String currentStatus,
			String oldStatus) {
		List<PaymentInstructionStaticsByUser> paymentInstructionStaticsByUserObjects = paymentInstructionStatusRepository
				.getPaymentInstructionStatsByCurrentStatusAndByOldStatus(currentStatus, oldStatus);
		paymentInstructionStaticsByUserObjects = Util.getFilteredPisList(paymentInstructionStaticsByUserObjects);
		return Util.createMultimapFromPisByUserList(paymentInstructionStaticsByUserObjects);
	}

    public MultiMap getPaymentStatsByUserGroupByType(String userId, String status) {
        List<PaymentInstructionStats> results = paymentInstructionStatusRepository.getStatsByUserGroupByType(userId, status);
        MultiMap paymentInstructionStatsGroupedByBgc = new MultiValueMap();
        results.stream().forEach(stat -> {
            Link detailslink = linkTo(methodOn(PaymentInstructionController.class)
                .getPaymentInstructionsByIdamId(userId, status,
                    null, null, null, null, null,
                    null, null, null, stat.getPaymentType(), null, null, stat.getBgc())
            ).withRel(STAT_DETAILS);

            Resource<PaymentInstructionStats> resource = new Resource<>(stat, detailslink.expand());

            // TODO: this is just a temp solution we have to clarify with PO if we really need to group cheques and postal-orders
            if (GROUPED_TYPES.contains(stat.getPaymentType())){
                Link groupedLink = linkTo(methodOn(PaymentInstructionController.class)
                    .getPaymentInstructionsByIdamId(userId, status,
                        null, null, null, null, null,
                        null, null, null,
                        GROUPED_TYPES.stream().collect(Collectors.joining( "," )), null, null, stat.getBgc())
                ).withRel(STAT_GROUP_DETAILS);
                resource.add(groupedLink.expand());
            }

            paymentInstructionStatsGroupedByBgc.put(
            stat.getBgc() == null ? "0" : stat.getBgc(), resource);
        });
        return paymentInstructionStatsGroupedByBgc;
    }

    private void savePaymentInstructionStatus(PaymentInstruction pi, String userId) {
        PaymentInstructionStatusReferenceKey pisrKey = new PaymentInstructionStatusReferenceKey(pi.getId(),
            pi.getStatus());
        PaymentInstructionStatus pis = new PaymentInstructionStatus(pisrKey, userId);
        paymentInstructionStatusRepository.save(pis);
    }

    public Map<Integer, List<PaymentInstructionStatusHistory>> getStatusHistoryMapForTTB(LocalDate startDate,  LocalDate endDate) {

        if (null != endDate && startDate.isAfter(endDate)) {
            LOG.error("PaymentInstructionService - Error while generating daily fees csv file. Incorrect start and end dates ");
            return Collections.emptyMap();
        }
        LocalDate searchEndDate;

        if (null == endDate || startDate.equals(endDate)) {
            searchEndDate = startDate.plusDays(1);
        } else {
            searchEndDate = endDate.plusDays(1);
        }

        List<PaymentInstructionStatusHistory> statusHistoryList = paymentInstructionStatusRepository.getPaymentInstructionStatusHistoryForTTB
            (startDate.atStartOfDay(), searchEndDate.atStartOfDay());

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
        Map<Integer, List<PaymentInstructionStatusHistory>> statusHistortMapForTTB = getStatusHistoryMapForTTB(startDate, endDate);
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

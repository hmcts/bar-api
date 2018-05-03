package uk.gov.hmcts.bar.api.data.service;


import com.google.common.collect.Lists;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.bar.api.data.enums.PaymentActionEnum;
import uk.gov.hmcts.bar.api.data.enums.PaymentStatusEnum;
import uk.gov.hmcts.bar.api.data.exceptions.InvalidActionException;
import uk.gov.hmcts.bar.api.data.exceptions.PaymentInstructionNotFoundException;
import uk.gov.hmcts.bar.api.data.model.*;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionRepository;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionStatusRepository;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionsSpecifications;
import uk.gov.hmcts.bar.api.data.utils.Util;

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
    private CaseReferenceService caseReferenceService;
    private final BarUserService barUserService;


    public PaymentInstructionService(PaymentReferenceService paymentReferenceService,
                                     CaseReferenceService caseReferenceService, PaymentInstructionRepository paymentInstructionRepository,
                                     BarUserService barUserService, PaymentInstructionStatusRepository paymentInstructionStatusRepository) {
        this.paymentReferenceService = paymentReferenceService;
        this.caseReferenceService = caseReferenceService;
        this.paymentInstructionRepository = paymentInstructionRepository;
        this.barUserService = barUserService;
        this.paymentInstructionStatusRepository = paymentInstructionStatusRepository;
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

    public CaseReference createCaseReference(Integer paymentInstructionId, CaseReferenceRequest caseReferenceRequest) {

        Optional<PaymentInstruction> optionalPaymentInstruction = paymentInstructionRepository.findById(paymentInstructionId);
        PaymentInstruction existingPaymentInstruction = optionalPaymentInstruction
            .orElseThrow(() -> new PaymentInstructionNotFoundException(paymentInstructionId));

        CaseReference caseReference = new CaseReference(caseReferenceRequest.getCaseReference(), existingPaymentInstruction.getId());

        return caseReferenceService.saveCaseReference(caseReference);
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

    public PaymentInstruction getPaymentInstruction(Integer id) {
        Optional<PaymentInstruction> op = paymentInstructionRepository.findById(id);

        if (op.isPresent()) {
            return op.get();
        } else {
            return null;
        }
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
        String[] nullPropertiesNamesToIgnore = Util.getNullPropertyNames(paymentInstructionRequest);
        BeanUtils.copyProperties(paymentInstructionRequest, existingPaymentInstruction, nullPropertiesNamesToIgnore);
        existingPaymentInstruction.setUserId(userId);
        savePaymentInstructionStatus(existingPaymentInstruction, userId);
        return paymentInstructionRepository.saveAndRefresh(existingPaymentInstruction);
    }

    public List<PaymentInstruction> getAllPaymentInstructionsByCaseReference(String caseReference) {
        return paymentInstructionRepository.findByCaseReference(caseReference);
    }

    public PaymentInstruction actionPaymentInstruction(Integer id,
                                                       PaymentInstructionActionRequest paymentInstructionActionRequest) throws InvalidActionException {
        if (PaymentActionEnum.getPaymentActionEnum(paymentInstructionActionRequest.getAction().trim()) == null) {
            throw new InvalidActionException("Invalid action string: " + paymentInstructionActionRequest.getAction());
        }
        Optional<PaymentInstruction> optionalPaymentInstruction = paymentInstructionRepository.findById(id);
        PaymentInstruction existingPaymentInstruction = optionalPaymentInstruction
            .orElseThrow(() -> new PaymentInstructionNotFoundException(id));
        String[] nullPropertiesNamesToIgnore = Util.getNullPropertyNames(paymentInstructionActionRequest);
        BeanUtils.copyProperties(paymentInstructionActionRequest, existingPaymentInstruction,
            nullPropertiesNamesToIgnore);
        return paymentInstructionRepository.saveAndRefresh(existingPaymentInstruction);
    }

    @SuppressWarnings("unchecked")
    public Map<String, MultiMap> getPaymentInstructionOverview() {
        List<PaymentInstructionOverview> paymentInstructionOverviewList = paymentInstructionStatusRepository
            .getPaymentOverviewStats();
        Map<String, MultiMap> combinedPaymentInstructionOverviewMap = new HashMap<>();
        MultiMap paymentInstructionOverviewRolesMap = new MultiValueMap();
        paymentInstructionOverviewList.forEach(paymentInstructionOverview -> paymentInstructionOverviewRolesMap
            .put(paymentInstructionOverview.getBarUserRole(), paymentInstructionOverview));
        Set<String> paymentInstructionOverviewRolesMapKeys = paymentInstructionOverviewRolesMap.keySet();
        paymentInstructionOverviewRolesMapKeys.forEach(paymentInstructionOverviewRolesMapKey -> {
            MultiMap paymentInstructionOverviewUserMap = new MultiValueMap();
            List<PaymentInstructionOverview> pioList = (ArrayList<PaymentInstructionOverview>) paymentInstructionOverviewRolesMap
                .get(paymentInstructionOverviewRolesMapKey);
            pioList.forEach(pio -> paymentInstructionOverviewUserMap.put(pio.getBarUserId(), pio));
            combinedPaymentInstructionOverviewMap.put(paymentInstructionOverviewRolesMapKey,
                paymentInstructionOverviewUserMap);
        });

        return combinedPaymentInstructionOverviewMap;
    }

    private void savePaymentInstructionStatus(PaymentInstruction pi, String userId) {
        PaymentInstructionStatusReferenceKey pisrKey = new PaymentInstructionStatusReferenceKey(pi.getId(),
            pi.getStatus());
        PaymentInstructionStatus pis = new PaymentInstructionStatus(pisrKey, userId);
        paymentInstructionStatusRepository.save(pis);
    }


}

package uk.gov.hmcts.bar.api.data.service;


import com.google.common.collect.Lists;
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
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionsSpecifications;
import uk.gov.hmcts.bar.api.data.utils.Util;

import java.util.List;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;


@Service
@Transactional
public class PaymentInstructionService {

    private static final Logger LOG = getLogger(PaymentInstructionService.class);

    public static final String SITE_ID = "BR01";
    private static final int PAGE_NUMBER = 0;
    private static final int MAX_RECORDS_PER_PAGE = 200;
    private PaymentInstructionRepository paymentInstructionRepository;
    private PaymentReferenceService paymentReferenceService;
    private CaseReferenceService caseReferenceService;


    public PaymentInstructionService(PaymentReferenceService paymentReferenceService, CaseReferenceService caseReferenceService,
                                     PaymentInstructionRepository paymentInstructionRepository, BarUserService barUserService) {
        this.paymentReferenceService = paymentReferenceService;
        this.caseReferenceService = caseReferenceService;
        this.paymentInstructionRepository = paymentInstructionRepository;

    }

    public PaymentInstruction createPaymentInstruction(PaymentInstruction paymentInstruction, BarUser user) {
        if (PaymentStatusEnum.contains(paymentInstruction.getStatus())) {
            paymentInstruction.setStatus(paymentInstruction.getStatus());
        } else {
            paymentInstruction.setStatus(PaymentStatusEnum.DRAFT.dbKey());
        }

        PaymentReference nextPaymentReference = paymentReferenceService.getNextPaymentReferenceSequenceBySite(SITE_ID);
        paymentInstruction.setSiteId(SITE_ID);
        paymentInstruction.setDailySequenceId(nextPaymentReference.getDailySequenceId());
        paymentInstruction.setBarUser(user);
        return paymentInstructionRepository.saveAndRefresh(paymentInstruction);
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
        return paymentInstructionRepository.findOne(id);
    }

    public void deletePaymentInstruction(Integer id) {
        try {
            paymentInstructionRepository.delete(id);
        } catch (EmptyResultDataAccessException erdae) {
            LOG.error("Resource not found: " + erdae.getMessage(), erdae);
            throw new PaymentInstructionNotFoundException(id);
        }

    }

    public PaymentInstruction submitPaymentInstruction(Integer id, PaymentInstructionUpdateRequest paymentInstructionUpdateRequest) {
        Optional<PaymentInstruction> optionalPaymentInstruction = paymentInstructionRepository.findById(id);
        PaymentInstruction existingPaymentInstruction = optionalPaymentInstruction
            .orElseThrow(() -> new PaymentInstructionNotFoundException(id));
        String[] nullPropertiesNamesToIgnore = Util.getNullPropertyNames(paymentInstructionUpdateRequest);
        BeanUtils.copyProperties(paymentInstructionUpdateRequest, existingPaymentInstruction, nullPropertiesNamesToIgnore);
        return paymentInstructionRepository.saveAndRefresh(existingPaymentInstruction);
    }

    public PaymentInstruction updatePaymentInstruction(Integer id, PaymentInstructionRequest paymentInstructionRequest) {
        Optional<PaymentInstruction> optionalPaymentInstruction = paymentInstructionRepository.findById(id);
        PaymentInstruction existingPaymentInstruction = optionalPaymentInstruction
            .orElseThrow(() -> new PaymentInstructionNotFoundException(id));
        String[] nullPropertiesNamesToIgnore = Util.getNullPropertyNames(paymentInstructionRequest);
        BeanUtils.copyProperties(paymentInstructionRequest, existingPaymentInstruction, nullPropertiesNamesToIgnore);
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


}

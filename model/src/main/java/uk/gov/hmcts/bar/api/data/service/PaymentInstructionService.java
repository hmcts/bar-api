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
import uk.gov.hmcts.bar.api.data.enums.PaymentStatusEnum;
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

    public static final String SITE_ID="BR01";
    private static final int PAGE_NUMBER = 0;
    private static final int MAX_RECORDS_PER_PAGE = 200;
    private PaymentInstructionRepository paymentInstructionRepository;
    private PaymentReferenceService paymentReferenceService;
    private CaseReferenceService caseReferenceService;


    public PaymentInstructionService(PaymentReferenceService paymentReferenceService,CaseReferenceService caseReferenceService,
                                     PaymentInstructionRepository paymentInstructionRepository) {
        this.paymentReferenceService = paymentReferenceService;
        this.caseReferenceService = caseReferenceService;
        this.paymentInstructionRepository = paymentInstructionRepository;

    }

    public PaymentInstruction createPaymentInstruction(PaymentInstruction paymentInstruction){
        paymentInstruction.setStatus(PaymentStatusEnum.DRAFT.dbKey());
        PaymentReference nextPaymentReference = paymentReferenceService.getNextPaymentReferenceSequenceBySite(SITE_ID);
        paymentInstruction.setSiteId(SITE_ID);
        paymentInstruction.setDailySequenceId(nextPaymentReference.getDailySequenceId());
        return paymentInstructionRepository.saveAndRefresh(paymentInstruction);
    }

    public PaymentInstruction createCaseReference(Integer paymentInstructionId, CaseReferenceRequest caseReferenceRequest) {

        Optional<PaymentInstruction> optionalPaymentInstruction = paymentInstructionRepository.findById(paymentInstructionId);
        PaymentInstruction existingPaymentInstruction = optionalPaymentInstruction
            .orElseThrow(() -> new PaymentInstructionNotFoundException(paymentInstructionId));

        Optional<CaseReference> optionalCaseReference = caseReferenceService.getCaseReference(caseReferenceRequest.getCaseReference());
        if (optionalCaseReference.isPresent())
        {
            existingPaymentInstruction.getCaseReferences().add(optionalCaseReference.get());
        }
        else{
            existingPaymentInstruction.getCaseReferences().add(caseReferenceService.saveCaseReference(caseReferenceRequest.getCaseReference()));
        }
        return paymentInstructionRepository.saveAndRefresh(existingPaymentInstruction);
    }


	public List<PaymentInstruction> getAllPaymentInstructions(PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto) {

		paymentInstructionSearchCriteriaDto.setSiteId(SITE_ID);
		PaymentInstructionsSpecifications paymentInstructionsSpecification = new PaymentInstructionsSpecifications(paymentInstructionSearchCriteriaDto);
		Sort sort = new Sort(Sort.Direction.ASC, "paymentDate");
		Pageable pageDetails = new PageRequest(PAGE_NUMBER, MAX_RECORDS_PER_PAGE, sort);

		return Lists.newArrayList(paymentInstructionRepository
				.findAll(paymentInstructionsSpecification.getPaymentInstructionsSpecification(), pageDetails)
				.iterator());
	}

	public PaymentInstruction getPaymentInstruction(Integer id) {
		return paymentInstructionRepository.findOne(id);
	}

    public void deletePaymentInstruction(Integer id){
        try {
            paymentInstructionRepository.delete(id);
        }
        catch (EmptyResultDataAccessException erdae){
        		LOG.error("Resource not found: "+erdae.getMessage(),erdae);
            throw new PaymentInstructionNotFoundException(id);
        }

    }

    public PaymentInstruction submitPaymentInstruction(Integer id,PaymentInstructionUpdateRequest paymentInstructionUpdateRequest) {
        Optional<PaymentInstruction> optionalPaymentInstruction = paymentInstructionRepository.findById(id);
        PaymentInstruction existingPaymentInstruction = optionalPaymentInstruction
            .orElseThrow(() -> new PaymentInstructionNotFoundException(id));
        String [] nullPropertiesNamesToIgnore = Util.getNullPropertyNames(paymentInstructionUpdateRequest);
        BeanUtils.copyProperties(paymentInstructionUpdateRequest,existingPaymentInstruction,nullPropertiesNamesToIgnore);
        return paymentInstructionRepository.saveAndRefresh(existingPaymentInstruction);
    }

    public PaymentInstruction updatePaymentInstruction(Integer id,PaymentInstructionRequest paymentInstructionRequest){
        Optional<PaymentInstruction> optionalPaymentInstruction = paymentInstructionRepository.findById(id);
        PaymentInstruction existingPaymentInstruction = optionalPaymentInstruction
            .orElseThrow(() -> new PaymentInstructionNotFoundException(id));
        String [] nullPropertiesNamesToIgnore = Util.getNullPropertyNames(paymentInstructionRequest);
        BeanUtils.copyProperties(paymentInstructionRequest,existingPaymentInstruction,nullPropertiesNamesToIgnore);
        return paymentInstructionRepository.saveAndRefresh(existingPaymentInstruction);
    }


}

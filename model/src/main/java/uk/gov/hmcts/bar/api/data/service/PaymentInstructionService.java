package uk.gov.hmcts.bar.api.data.service;


import static java.time.LocalDateTime.now;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import uk.gov.hmcts.bar.api.data.enums.PaymentStatusEnum;
import uk.gov.hmcts.bar.api.data.exceptions.PaymentInstructionNotFoundException;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentReference;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionRepository;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionsSpecifications;


@Service
@Transactional
public class PaymentInstructionService {
	
    private static final String SITE_ID="BR01";
    private static final int PAGE_NUMBER = 0;
    private static final int MAX_RECORDS_PER_PAGE = 200;
    private PaymentInstructionRepository paymentInstructionRepository;
    private PaymentReferenceService paymentReferenceService;


    public PaymentInstructionService(PaymentReferenceService paymentReferenceService,
                                     PaymentInstructionRepository paymentInstructionRepository) {
        this.paymentReferenceService = paymentReferenceService;
        this.paymentInstructionRepository = paymentInstructionRepository;

    }

    public PaymentInstruction createPaymentInstruction(PaymentInstruction paymentInstruction){
        paymentInstruction.setStatus(PaymentStatusEnum.DRAFT.dbKey());
        PaymentReference nextPaymentReference = paymentReferenceService.getNextPaymentReferenceSequenceBySite(SITE_ID);
        paymentInstruction.setSiteId(SITE_ID);
        paymentInstruction.setDailySequenceId(nextPaymentReference.getDailySequenceId());
        return paymentInstructionRepository.save(paymentInstruction);
    }


	public List<PaymentInstruction> getAllPaymentInstructions(String status, LocalDateTime startDate,
			LocalDateTime endDate) {

		PaymentInstructionsSpecifications paymentInstructionsSpecification = new PaymentInstructionsSpecifications(
				status, startDate, endDate);
		Pageable pageDetails = new PageRequest(PAGE_NUMBER, MAX_RECORDS_PER_PAGE);

		return Lists.newArrayList(paymentInstructionRepository
				.findAll(paymentInstructionsSpecification.getPaymentInstructionsSpecification(), pageDetails)
				.iterator());
	}

    public void deleteCurrentPaymentInstructionWithDraftStatus(Integer id){
        try {
            paymentInstructionRepository.deletePaymentInstructionByIdAndStatusAndPaymentDateAfter(id,PaymentStatusEnum.DRAFT.dbKey(),now().truncatedTo(ChronoUnit.DAYS));
        }
        catch (EmptyResultDataAccessException erdae){
            throw new PaymentInstructionNotFoundException(id);
        }

    }
    
}

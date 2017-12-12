package uk.gov.hmcts.bar.api.data.service;


import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.bar.api.data.enums.PaymentStatusEnum;
import uk.gov.hmcts.bar.api.data.exceptions.PaymentInstructionNotFoundException;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionRequest;
import uk.gov.hmcts.bar.api.data.model.PaymentReference;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionRepository;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionsSpecifications;
import uk.gov.hmcts.bar.api.data.utils.Util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;


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
        paymentInstructionRepository.saveAndFlush(paymentInstruction);
        paymentInstructionRepository.refresh(paymentInstruction);
        return paymentInstruction;
    }


	public List<PaymentInstruction> getAllPaymentInstructions(String status, Date startDate,
			Date endDate) {

		LocalDateTime paramStartDate = null;
		LocalDateTime paramEndDate = null;

		if (startDate != null) {
			paramStartDate = LocalDateTime.ofInstant(startDate.toInstant(), ZoneId.systemDefault()).toLocalDate()
					.atStartOfDay();
		}

		if (endDate != null) {
			paramEndDate = LocalDateTime.ofInstant(endDate.toInstant(), ZoneId.systemDefault()).toLocalDate()
					.atTime(LocalTime.now());
		}

		PaymentInstructionsSpecifications paymentInstructionsSpecification = new PaymentInstructionsSpecifications(
				status, paramStartDate, paramEndDate);
		Pageable pageDetails = new PageRequest(PAGE_NUMBER, MAX_RECORDS_PER_PAGE);

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
            throw new PaymentInstructionNotFoundException(id);
        }

    }

    public PaymentInstruction updatePaymentInstruction(Integer id,PaymentInstructionRequest paymentInstructionRequest) {
        Optional<PaymentInstruction> optionalPaymentInstruction = paymentInstructionRepository.findById(id);
        if(!optionalPaymentInstruction.isPresent()){
            throw new PaymentInstructionNotFoundException(id);
        }
        PaymentInstruction existingPaymentInstruction = optionalPaymentInstruction.get();
        String [] nullPropertiesNamesToIgnore = Util.getNullPropertyNames(paymentInstructionRequest);
        BeanUtils.copyProperties(paymentInstructionRequest,existingPaymentInstruction,nullPropertiesNamesToIgnore);
        paymentInstructionRepository.saveAndFlush(existingPaymentInstruction);
        paymentInstructionRepository.refresh(existingPaymentInstruction);
        return existingPaymentInstruction;
    }


}

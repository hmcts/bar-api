package uk.gov.hmcts.bar.api.data.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.hmcts.bar.api.data.model.CaseFeeDetail;
import uk.gov.hmcts.bar.api.data.model.CaseFeeDetailRequest;
import uk.gov.hmcts.bar.api.data.model.CaseReference;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionCaseReference;
import uk.gov.hmcts.bar.api.data.repository.CaseFeeDetailRepository;
import uk.gov.hmcts.bar.api.data.repository.CaseReferenceRepository;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionCaseReferenceRepository;

@Service
@Transactional
public class CaseFeeDetailService {

	private CaseFeeDetailRepository caseFeeDetailRepository;

	private PaymentInstructionCaseReferenceRepository paymentInstructionCaseReferenceRepository;

	private CaseReferenceRepository caseReferenceRepository;

	@Autowired
	public CaseFeeDetailService(CaseFeeDetailRepository caseFeeDetailRepository,
			PaymentInstructionCaseReferenceRepository paymentInstructionCaseReferenceRepository,
			CaseReferenceRepository caseReferenceRepository) {
		super();
		this.caseFeeDetailRepository = caseFeeDetailRepository;
		this.paymentInstructionCaseReferenceRepository = paymentInstructionCaseReferenceRepository;
		this.caseReferenceRepository = caseReferenceRepository;
	}

	public CaseFeeDetail saveCaseFeeDetail(Integer id, CaseFeeDetailRequest caseFeeDetailRequest) {

		int caseRefId = 0;
		PaymentInstructionCaseReference picr = null;

		Optional<CaseReference> caseReference = caseReferenceRepository
				.findByCaseReference(caseFeeDetailRequest.getCaseReference());
		if (caseReference.isPresent()) {
			caseRefId = caseReference.get().getId();
		}

		Optional<PaymentInstructionCaseReference> paymentInstructionCaseReference = paymentInstructionCaseReferenceRepository
				.findBypaymentInstructionIdAndCaseReferenceId(id, caseRefId);
		if (paymentInstructionCaseReference.isPresent()) {
			picr = paymentInstructionCaseReference.get();
		}

		return caseFeeDetailRepository.saveAndRefresh(CaseFeeDetail.caseFeeDetailWith()
				.amount(caseFeeDetailRequest.getAmount()).feeCode(caseFeeDetailRequest.getFeeCode())
				.feeDescription(caseFeeDetailRequest.getFeeDescription())
				.feeVersion(caseFeeDetailRequest.getFeeVersion()).paymentInstructionCaseReference(picr).build());
	}
}

package uk.gov.hmcts.bar.api.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.hmcts.bar.api.data.model.CaseFeeDetail;
import uk.gov.hmcts.bar.api.data.model.CaseFeeDetailRequest;
import uk.gov.hmcts.bar.api.data.repository.CaseFeeDetailRepository;
import uk.gov.hmcts.bar.api.data.repository.CaseReferenceRepository;

@Service
@Transactional
public class CaseFeeDetailService {

	private CaseFeeDetailRepository caseFeeDetailRepository;

	@Autowired
	public CaseFeeDetailService(CaseFeeDetailRepository caseFeeDetailRepository,
			CaseReferenceRepository caseReferenceRepository) {
		super();
		this.caseFeeDetailRepository = caseFeeDetailRepository;
	}

	public CaseFeeDetail saveCaseFeeDetail(CaseFeeDetailRequest caseFeeDetailRequest) {

		return caseFeeDetailRepository.saveAndRefresh(CaseFeeDetail.caseFeeDetailWith()
				.amount(caseFeeDetailRequest.getAmount()).feeCode(caseFeeDetailRequest.getFeeCode())
				.feeDescription(caseFeeDetailRequest.getFeeDescription())
				.feeVersion(caseFeeDetailRequest.getFeeVersion())
				.caseReferenceId(caseFeeDetailRequest.getCaseReferenceId())
				.remissionAmount(caseFeeDetailRequest.getRemissionAmount())
				.remissionAuthorisation(caseFeeDetailRequest.getRemissionAuthorisation())
				.remissionBenefiter(caseFeeDetailRequest.getRemissionBenefiter())
				.refundAmount(caseFeeDetailRequest.getRefundAmount()).build());
	}
}

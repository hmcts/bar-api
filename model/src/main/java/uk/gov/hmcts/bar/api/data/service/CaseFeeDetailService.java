package uk.gov.hmcts.bar.api.data.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.hmcts.bar.api.data.exceptions.CaseFeeDetailNotFoundException;
import uk.gov.hmcts.bar.api.data.model.CaseFeeDetail;
import uk.gov.hmcts.bar.api.data.model.CaseFeeDetailRequest;
import uk.gov.hmcts.bar.api.data.repository.CaseFeeDetailRepository;
import uk.gov.hmcts.bar.api.data.repository.CaseReferenceRepository;
import uk.gov.hmcts.bar.api.data.utils.Util;

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
				.refundAmount(caseFeeDetailRequest.getRefundAmount())
				.caseReference(caseFeeDetailRequest.getCaseReference()).build());
	}

	public CaseFeeDetail updateCaseFeeDetail(Integer feeId, CaseFeeDetailRequest caseFeeDetailRequest) {

		CaseFeeDetail existingCaseFeeDetail = caseFeeDetailRepository.findOne(feeId);
		if (existingCaseFeeDetail == null) {
			throw new CaseFeeDetailNotFoundException(feeId);
		}

		//Disabling this right now to be able to remove remission if needed (null out all values)
		String[] nullPropertiesNamesToIgnore = Util.getNullPropertyNames(caseFeeDetailRequest);
		BeanUtils.copyProperties(caseFeeDetailRequest, existingCaseFeeDetail);

		return caseFeeDetailRepository.saveAndRefresh(existingCaseFeeDetail);
	}

	public void deleteCaseFeeDetail(Integer feeId) {

        caseFeeDetailRepository.delete(feeId);
    }
}

package uk.gov.hmcts.bar.api.data.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.bar.api.audit.AuditRepository;
import uk.gov.hmcts.bar.api.data.exceptions.CaseFeeDetailNotFoundException;
import uk.gov.hmcts.bar.api.data.model.BarUser;
import uk.gov.hmcts.bar.api.data.model.CaseFeeDetail;
import uk.gov.hmcts.bar.api.data.model.CaseFeeDetailRequest;
import uk.gov.hmcts.bar.api.data.repository.CaseFeeDetailRepository;

import java.util.Optional;

@Service
@Transactional
public class CaseFeeDetailService {

    private CaseFeeDetailRepository caseFeeDetailRepository;
    private final BarUserService barUserService;
    private final AuditRepository auditRepository;

    @Autowired
    public CaseFeeDetailService(CaseFeeDetailRepository caseFeeDetailRepository,BarUserService barUserService,AuditRepository auditRepository) {
        super();
        this.caseFeeDetailRepository = caseFeeDetailRepository;
        this.barUserService = barUserService;
        this.auditRepository = auditRepository;
    }

    public CaseFeeDetail saveCaseFeeDetail(CaseFeeDetailRequest caseFeeDetailRequest) {
        Optional<BarUser> optBarUser = barUserService.getBarUser();
        BarUser barUser = (optBarUser.isPresent())? optBarUser.get(): null;

        CaseFeeDetail caseFeeDetail = caseFeeDetailRepository.saveAndRefresh(CaseFeeDetail.caseFeeDetailWith()
            .amount(caseFeeDetailRequest.getAmount()).feeCode(caseFeeDetailRequest.getFeeCode())
            .feeDescription(caseFeeDetailRequest.getFeeDescription())
            .feeVersion(caseFeeDetailRequest.getFeeVersion())
            .paymentInstructionId(caseFeeDetailRequest.getPaymentInstructionId())
            .remissionAmount(caseFeeDetailRequest.getRemissionAmount())
            .remissionAuthorisation(caseFeeDetailRequest.getRemissionAuthorisation())
            .remissionBenefiter(caseFeeDetailRequest.getRemissionBenefiter())
            .refundAmount(caseFeeDetailRequest.getRefundAmount())
            .caseReference(caseFeeDetailRequest.getCaseReference()).build());

        auditRepository.trackCaseEvent("CREATE_CASE_EVENT", caseFeeDetailRequest, barUser);

        return caseFeeDetail;
    }

    public CaseFeeDetail updateCaseFeeDetail(Integer feeId, CaseFeeDetailRequest caseFeeDetailRequest) {

        Optional<CaseFeeDetail> optExistingCaseFeeDetail = caseFeeDetailRepository.findById(feeId);
        CaseFeeDetail existingCaseFeeDetail = optExistingCaseFeeDetail.orElseThrow(() ->  new CaseFeeDetailNotFoundException(feeId));
        BeanUtils.copyProperties(caseFeeDetailRequest, existingCaseFeeDetail);

        return caseFeeDetailRepository.saveAndRefresh(existingCaseFeeDetail);
    }

    public void deleteCaseFeeDetail(Integer feeId) {

        caseFeeDetailRepository.deleteById(feeId);
    }
}



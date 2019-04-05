package uk.gov.hmcts.bar.api.data.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.bar.api.audit.AuditRepository;
import uk.gov.hmcts.bar.api.data.exceptions.CaseFeeDetailNotFoundException;
import uk.gov.hmcts.bar.api.data.exceptions.PaymentInstructionNotFoundException;
import uk.gov.hmcts.bar.api.data.model.BarUser;
import uk.gov.hmcts.bar.api.data.model.CaseFeeDetail;
import uk.gov.hmcts.bar.api.data.model.CaseFeeDetailRequest;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.repository.CaseFeeDetailRepository;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionRepository;

import java.util.Optional;

@Service
@Transactional
public class CaseFeeDetailService {

    private PaymentInstructionRepository paymentInstructionRepository;
    private CaseFeeDetailRepository caseFeeDetailRepository;
    private final AuditRepository auditRepository;

    @Autowired
    public CaseFeeDetailService(PaymentInstructionRepository paymentInstructionRepository,CaseFeeDetailRepository caseFeeDetailRepository,AuditRepository auditRepository) {
        super();
        this.paymentInstructionRepository = paymentInstructionRepository;
        this.caseFeeDetailRepository = caseFeeDetailRepository;
        this.auditRepository = auditRepository;
    }

    public CaseFeeDetail saveCaseFeeDetail(BarUser barUser,CaseFeeDetailRequest caseFeeDetailRequest) {
        Optional<PaymentInstruction> optionalPaymentInstruction = paymentInstructionRepository.findByIdAndSiteId(caseFeeDetailRequest.getPaymentInstructionId(), barUser.getSelectedSiteId());
        optionalPaymentInstruction
            .orElseThrow(() -> new PaymentInstructionNotFoundException(caseFeeDetailRequest.getPaymentInstructionId(), barUser.getSelectedSiteId()));

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

    public CaseFeeDetail updateCaseFeeDetail(BarUser barUser,Integer feeId, CaseFeeDetailRequest caseFeeDetailRequest) {

        Optional<PaymentInstruction> optionalPaymentInstruction = paymentInstructionRepository.findByIdAndSiteId(caseFeeDetailRequest.getPaymentInstructionId(), barUser.getSelectedSiteId());
        optionalPaymentInstruction
            .orElseThrow(() -> new PaymentInstructionNotFoundException(caseFeeDetailRequest.getPaymentInstructionId(), barUser.getSelectedSiteId()));

        Optional<CaseFeeDetail> optExistingCaseFeeDetail = caseFeeDetailRepository.findById(feeId);
        CaseFeeDetail existingCaseFeeDetail = optExistingCaseFeeDetail.orElseThrow(() ->  new CaseFeeDetailNotFoundException(feeId));
        BeanUtils.copyProperties(caseFeeDetailRequest, existingCaseFeeDetail);

        return caseFeeDetailRepository.saveAndRefresh(existingCaseFeeDetail);
    }

    public void deleteCaseFeeDetail(Integer feeId) {

        caseFeeDetailRepository.deleteById(feeId);
    }
}



package uk.gov.hmcts.bar.api.data.service;

import org.slf4j.Logger;
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
import uk.gov.hmcts.bar.api.data.utils.Util;

import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

@Service
@Transactional
public class CaseFeeDetailService {

    private static final Logger LOG = getLogger(CaseFeeDetailService.class);
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
        BarUser barUser= optBarUser.get();

        LOG.info("Saving case details for user ='{}' , case reference ='{}' and fee code = '{}",barUserService.getCurrentUserId(),caseFeeDetailRequest.getCaseReference(),caseFeeDetailRequest.getFeeCode());

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

        CaseFeeDetail existingCaseFeeDetail = caseFeeDetailRepository.getOne(feeId);
        if (existingCaseFeeDetail == null) {
            throw new CaseFeeDetailNotFoundException(feeId);
        }

        //Disabling this right now to be able to remove remission if needed (null out all values)
        String[] nullPropertiesNamesToIgnore = Util.getNullPropertyNames(caseFeeDetailRequest);
        BeanUtils.copyProperties(caseFeeDetailRequest, existingCaseFeeDetail);

        return caseFeeDetailRepository.saveAndRefresh(existingCaseFeeDetail);
    }

    public void deleteCaseFeeDetail(Integer feeId) {

        caseFeeDetailRepository.deleteById(feeId);
    }
}



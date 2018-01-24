package uk.gov.hmcts.bar.api.data.service;


import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import uk.gov.hmcts.bar.api.data.model.CaseReference;
import uk.gov.hmcts.bar.api.data.repository.CaseReferenceRepository;

@Service
@Transactional
public class CaseReferenceService {

    private CaseReferenceRepository caseReferenceRepository;

    public CaseReferenceService(CaseReferenceRepository caseReferenceRepository){
        this.caseReferenceRepository = caseReferenceRepository;

    }

    public CaseReference saveCaseReference(CaseReference caseReference){
        return caseReferenceRepository.saveAndRefresh(caseReference);
    }

}

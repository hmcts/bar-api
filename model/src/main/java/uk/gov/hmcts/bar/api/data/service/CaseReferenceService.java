package uk.gov.hmcts.bar.api.data.service;


import org.springframework.stereotype.Service;
import uk.gov.hmcts.bar.api.data.model.CaseReference;
import uk.gov.hmcts.bar.api.data.repository.CaseReferenceRepository;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class CaseReferenceService {

    private CaseReferenceRepository caseReferenceRepository;

    public CaseReferenceService(CaseReferenceRepository caseReferenceRepository){
        this.caseReferenceRepository = caseReferenceRepository;

    }

    public Optional<CaseReference> getCaseReference(String caseReference){
        return caseReferenceRepository.findByCaseReference(caseReference);

    }

    public CaseReference saveCaseReference(String caseReference){
        return caseReferenceRepository.saveAndRefresh(new CaseReference(caseReference));
    }

}

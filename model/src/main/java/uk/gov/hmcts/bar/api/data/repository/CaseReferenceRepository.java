package uk.gov.hmcts.bar.api.data.repository;

import org.springframework.stereotype.Repository;
import uk.gov.hmcts.bar.api.data.model.CaseReference;

import java.util.Optional;

@Repository
public interface CaseReferenceRepository extends BaseRepository<CaseReference,Integer>{
    Optional<CaseReference> findByCaseReference(String caseReference);
}

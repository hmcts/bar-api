package uk.gov.hmcts.bar.api.data.repository;

import org.springframework.stereotype.Repository;
import uk.gov.hmcts.bar.api.data.model.PaymentReference;

import java.util.Optional;

@Repository
public interface PaymentReferenceRepository extends BaseRepository<PaymentReference, String> {

    Optional<PaymentReference> findById(String siteId);

}

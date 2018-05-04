package uk.gov.hmcts.bar.api.data.repository;

import org.springframework.stereotype.Repository;
import uk.gov.hmcts.bar.api.data.model.PaymentType;

@Repository
public interface PaymentTypeRepository extends BaseRepository<PaymentType, String> {
}

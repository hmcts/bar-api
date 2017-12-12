package uk.gov.hmcts.bar.api.data.repository;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.bar.api.data.model.PaymentReference;
import uk.gov.hmcts.bar.api.data.model.PaymentReferenceKey;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface PaymentReferenceRepository extends BaseRepository<PaymentReference, PaymentReferenceKey> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PaymentReference> findByPaymentReferenceKey(PaymentReferenceKey paymentReferenceKey);

}

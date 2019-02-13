package uk.gov.hmcts.bar.api.data.repository;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.bar.api.data.model.PaymentReference;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface PaymentReferenceRepository extends BaseRepository<PaymentReference, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select pr from PaymentReference pr where pr.siteId = :siteId")
    Optional<PaymentReference> findOneForUpdate(@Param("siteId") String siteId);

}

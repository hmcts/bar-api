package uk.gov.hmcts.bar.api.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.bar.api.data.model.PaymentType;

@Repository
public interface PaymentTypeRepository extends JpaRepository<PaymentType, Integer> {
}

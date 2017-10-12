package uk.gov.hmcts.bar.api.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByCreatedByUserIdAndPaymentDateBetween(String createdByUserId ,LocalDateTime fromDate , LocalDateTime toDate);
}

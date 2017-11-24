package uk.gov.hmcts.bar.api.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentInstructionRepository extends JpaRepository<PaymentInstruction, Integer> {
    List<PaymentInstruction> findBySiteIdAndPaymentDateIsAfter(String siteId,LocalDateTime paymentDate);
}

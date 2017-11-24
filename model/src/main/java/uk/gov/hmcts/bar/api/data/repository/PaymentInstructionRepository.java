package uk.gov.hmcts.bar.api.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;

@Repository
public interface PaymentInstructionRepository extends JpaRepository<PaymentInstruction, Integer> {

}

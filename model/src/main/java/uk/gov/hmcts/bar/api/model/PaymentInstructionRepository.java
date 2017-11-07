package uk.gov.hmcts.bar.api.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentInstructionRepository extends JpaRepository<PaymentInstruction, Integer> {

}

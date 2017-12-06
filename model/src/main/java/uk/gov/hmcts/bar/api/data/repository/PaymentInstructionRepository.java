package uk.gov.hmcts.bar.api.data.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;

@Repository
public interface PaymentInstructionRepository extends JpaRepository<PaymentInstruction, Integer>, JpaSpecificationExecutor<PaymentInstruction> {
    void deletePaymentInstructionByIdAndStatusAndPaymentDateAfter(Integer id, String paymentInstructionStatus,LocalDateTime paymentDate ); 
}

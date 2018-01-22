package uk.gov.hmcts.bar.api.data.repository;

import java.util.Optional;

import uk.gov.hmcts.bar.api.data.model.PaymentInstructionCaseReference;

public interface PaymentInstructionCaseReferenceRepository extends BaseRepository<PaymentInstructionCaseReference, Integer>{
	Optional<PaymentInstructionCaseReference> findBypaymentInstructionIdAndCaseReferenceId(Integer paymentId, Integer caseRefId);
}

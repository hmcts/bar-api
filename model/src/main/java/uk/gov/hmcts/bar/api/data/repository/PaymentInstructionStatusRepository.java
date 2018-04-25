package uk.gov.hmcts.bar.api.data.repository;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import uk.gov.hmcts.bar.api.data.model.PaymentInstructionStatus;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionStatusReferenceKey;

@Repository
public interface PaymentInstructionStatusRepository
		extends BaseRepository<PaymentInstructionStatus, PaymentInstructionStatusReferenceKey> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<PaymentInstructionStatus> findByPaymentInstructionStatusReferenceKey(
			PaymentInstructionStatusReferenceKey paymentInstructionStatusReferenceKey);
}

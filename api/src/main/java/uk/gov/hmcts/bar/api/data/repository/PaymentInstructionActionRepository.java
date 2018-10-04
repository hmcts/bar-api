package uk.gov.hmcts.bar.api.data.repository;

import org.springframework.stereotype.Repository;

import uk.gov.hmcts.bar.api.data.model.PaymentInstructionAction;

@Repository
public interface PaymentInstructionActionRepository extends BaseRepository<PaymentInstructionAction, String> {

}

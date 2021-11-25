package uk.gov.hmcts.bar.api.data.service;

import com.google.common.primitives.Chars;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.bar.api.data.model.PaymentReference;
import uk.gov.hmcts.bar.api.data.repository.PaymentReferenceRepository;

import java.util.Optional;


@Service
@Transactional
public class PaymentReferenceService {
    private static final char[] SEQUENCE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private PaymentReferenceRepository paymentReferenceRepository;

    public PaymentReferenceService(PaymentReferenceRepository paymentReferenceRepository) {
        this.paymentReferenceRepository = paymentReferenceRepository;

    }

    @Transactional
    public PaymentReference getNextPaymentReference(String siteId) {

        PaymentReference nextPaymentReference;

        Optional<PaymentReference> optionalCurrentPaymentReference = paymentReferenceRepository.findOneForUpdate(siteId);
        if (optionalCurrentPaymentReference.isPresent()) {
            PaymentReference currentPaymentReference = optionalCurrentPaymentReference.get();
            nextPaymentReference = constructNextPaymentReference(currentPaymentReference);
        } else {
            nextPaymentReference = new PaymentReference(siteId,1,SEQUENCE_CHARACTERS[0]);

        }

        paymentReferenceRepository.save(nextPaymentReference);
        return nextPaymentReference;
    }

    private PaymentReference constructNextPaymentReference(PaymentReference paymentReference) {

        int currentSequenceId = paymentReference.getSequenceId();
        int nextSequenceId = (currentSequenceId == 9999) ?  1 : currentSequenceId + 1;
        char currentSequenceCharacter = paymentReference.getSequenceCharacter();

        char nextSequenceCharacter = currentSequenceCharacter;
        if (currentSequenceCharacter == SEQUENCE_CHARACTERS[25] && currentSequenceId == 9999) {
            nextSequenceCharacter = SEQUENCE_CHARACTERS[0];

        } else if (currentSequenceId == 9999 && currentSequenceCharacter != SEQUENCE_CHARACTERS[25]) {
            int index = Chars.indexOf(SEQUENCE_CHARACTERS,currentSequenceCharacter);
            nextSequenceCharacter = SEQUENCE_CHARACTERS[index + 1];
        }

        return new PaymentReference(paymentReference.getSiteId(),nextSequenceId,nextSequenceCharacter);
    }


}

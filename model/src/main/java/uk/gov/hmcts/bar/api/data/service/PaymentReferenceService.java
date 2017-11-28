package uk.gov.hmcts.bar.api.data.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.bar.api.data.model.PaymentReference;
import uk.gov.hmcts.bar.api.data.model.PaymentReferenceKey;
import uk.gov.hmcts.bar.api.data.repository.PaymentReferenceRepository;

import java.time.LocalDate;
import java.util.Optional;


@Service
@Transactional
public class PaymentReferenceService {

    private PaymentReferenceRepository paymentReferenceRepository;

    public PaymentReferenceService(PaymentReferenceRepository paymentReferenceRepository) {
        this.paymentReferenceRepository= paymentReferenceRepository;

    }

    public PaymentReference getNextPaymentReferenceSequenceBySite(String siteId){

        PaymentReference nextPaymentReference = null;
        PaymentReferenceKey paymentReferenceKey= new PaymentReferenceKey(siteId, LocalDate.now());

        Optional<PaymentReference> optionalCurrentPaymentReference = paymentReferenceRepository.findByPaymentReferenceKey(paymentReferenceKey);
        if (optionalCurrentPaymentReference.isPresent())
        {
            PaymentReference currentPaymentReference = optionalCurrentPaymentReference.get();
            currentPaymentReference.incrementDailySequenceIdByOne();
            nextPaymentReference = currentPaymentReference;
        }
        else {
            nextPaymentReference = new PaymentReference(paymentReferenceKey,1);

        }

        paymentReferenceRepository.save(nextPaymentReference);
        return nextPaymentReference;
    }



}

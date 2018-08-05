package uk.gov.hmcts.bar.api.data.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.bar.api.data.model.PaymentReference;
import uk.gov.hmcts.bar.api.data.model.PaymentReferenceKey;
import uk.gov.hmcts.bar.api.data.repository.PaymentReferenceRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class PaymentReferenceServiceTest {

    @InjectMocks
    private PaymentReferenceService paymentReferenceService;

    @Mock
    private PaymentReferenceRepository paymentReferenceRepository;

    @Mock
    private PaymentReferenceKey paymentReferenceKey;

    @Mock
    private PaymentReference paymentReference;



    @Before
    public void setupMock() {
        MockitoAnnotations.initMocks(this);
        paymentReferenceService = new PaymentReferenceService(paymentReferenceRepository);
        paymentReference = new PaymentReference(new PaymentReferenceKey("Y431", LocalDate.now()),1);

    }

    @Test
    public void shouldReturnPaymentReference_whenGetNextPaymentReferenceSequenceBySiteIsCalled() throws Exception {

        when(paymentReferenceRepository.findByPaymentReferenceKey(any(PaymentReferenceKey.class))).thenReturn(Optional.empty());

       PaymentReference retrievedPaymentReference = paymentReferenceService.getNextPaymentReferenceSequenceBySite("Y431");

        assertThat(retrievedPaymentReference).isEqualTo (paymentReference);

    }

    @Test
    public void shouldReturnPaymentReference_whenGetNextPaymentReferenceSequenceBySiteIsCalled1() throws Exception {

        when(paymentReferenceRepository.findByPaymentReferenceKey(any(PaymentReferenceKey.class))).thenReturn(Optional.of(paymentReference));

        PaymentReference retrievedPaymentReference = paymentReferenceService.getNextPaymentReferenceSequenceBySite("Y431");

        assertThat(retrievedPaymentReference).isEqualTo (paymentReference);

    }


}

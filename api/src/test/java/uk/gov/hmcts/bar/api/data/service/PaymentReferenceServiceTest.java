package uk.gov.hmcts.bar.api.data.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.bar.api.data.model.PaymentReference;
import uk.gov.hmcts.bar.api.data.repository.PaymentReferenceRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class PaymentReferenceServiceTest {

    @InjectMocks
    private PaymentReferenceService paymentReferenceService;

    @Mock
    private PaymentReferenceRepository paymentReferenceRepository;

    @Mock
    private PaymentReference paymentReference1;

    @Mock
    private PaymentReference paymentReference2;

    @Mock
    private PaymentReference paymentReference3;

    @Mock
    private PaymentReference paymentReference4;


    @Mock
    private PaymentReference paymentReference5;
    @Before
    public void setupMock() {
        MockitoAnnotations.initMocks(this);
        paymentReferenceService = new PaymentReferenceService(paymentReferenceRepository);
        paymentReference1 = new PaymentReference("Y431",1, 'A' );
        paymentReference2 = new PaymentReference("Y431",2, 'A' );
        paymentReference3 = new PaymentReference("Y431",9999, 'A' );
        paymentReference4 = new PaymentReference("Y431",1, 'B' );
        paymentReference5 = new PaymentReference("Y431",9999, 'Z' );
    }

    @Test
    public void shouldReturnPaymentReference_whenGetNextPaymentReferenceIsCalled()  {

        when(paymentReferenceRepository.findById(anyString())).thenReturn(Optional.empty());

        PaymentReference retrievedPaymentReference = paymentReferenceService.getNextPaymentReference("Y431");

        assertThat(retrievedPaymentReference).isEqualTo (paymentReference1);

    }

    @Test
    public void shouldReturnPaymentReference_whenSequenceIdIs_1()  {

        when(paymentReferenceRepository.findById(anyString())).thenReturn(Optional.of(paymentReference1));

        PaymentReference retrievedPaymentReference = paymentReferenceService.getNextPaymentReference("Y431");

        assertThat(retrievedPaymentReference).isEqualTo (paymentReference2);

    }


    @Test
    public void shouldReturnPaymentReference_whenSequenceIdIs_9999()  {

        when(paymentReferenceRepository.findById(anyString())).thenReturn(Optional.of(paymentReference3));

        PaymentReference retrievedPaymentReference = paymentReferenceService.getNextPaymentReference("Y431");

        assertThat(retrievedPaymentReference).isEqualTo (paymentReference4);

    }

    @Test
    public void shouldReturnPaymentReference_whenSequenceIdIs9999_And_Z()  {

        when(paymentReferenceRepository.findById(anyString())).thenReturn(Optional.of(paymentReference5));

        PaymentReference retrievedPaymentReference = paymentReferenceService.getNextPaymentReference("Y431");

        assertThat(retrievedPaymentReference).isEqualTo (paymentReference1);

    }


}

package uk.gov.hmcts.bar.api.data.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.bar.api.data.model.PaymentType;
import uk.gov.hmcts.bar.api.data.repository.PaymentTypeRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class PaymentTypeServiceTest {

    private PaymentTypeService paymentTypeService;

    @Mock
    private PaymentTypeRepository paymentTypeRepository;

    @Mock
    private List<PaymentType> paymentTypes;

    @Mock
    private PaymentType paymentType;


    @Before
    public void setupMock() {
        MockitoAnnotations.initMocks(this);
        paymentTypeService = new PaymentTypeService(paymentTypeRepository);

    }

    @Test
    public void shouldReturnPaymentTypes_whenGetAllPaymentTypesIsCalled() throws Exception {

        when(paymentTypeRepository.findAll()).thenReturn(paymentTypes);

        List<PaymentType> retrievedPaymentTypes = paymentTypeService.getAllPaymentTypes();

        assertThat(retrievedPaymentTypes).isEqualTo (paymentTypes);

    }


    @Test
    public void shouldReturnPaymentType_whenGetPaymentTypeByIdIsCalled() throws Exception {

        when(paymentTypeRepository.findOne(1)).thenReturn(paymentType);

        PaymentType retrievedPaymentType = paymentTypeService.getPaymentTypeById(1);

        assertThat(retrievedPaymentType).isEqualTo(paymentType);
    }



}

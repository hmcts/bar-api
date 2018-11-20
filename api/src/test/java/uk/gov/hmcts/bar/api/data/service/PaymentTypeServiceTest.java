package uk.gov.hmcts.bar.api.data.service;

import org.assertj.core.api.Assertions;
import org.ff4j.FF4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionAction;
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
    private List<PaymentInstructionAction> paymentInstructionActions;

    @Mock
    private PaymentType paymentType;

    @Mock
    private FF4j ff4j;


	@Before
	public void setupMock() {
		MockitoAnnotations.initMocks(this);
		paymentTypeService = new PaymentTypeService(paymentTypeRepository,ff4j);

	}

    @Test
    public void shouldReturnPaymentTypes_whenGetAllPaymentTypesIsCalled() throws Exception {

        when(paymentTypeRepository.findAll()).thenReturn(paymentTypes);

        List<PaymentType> retrievedPaymentTypes = paymentTypeService.getAllPaymentTypes();

        Assertions.assertThat(retrievedPaymentTypes).isEqualTo (paymentTypes);

    }

    @Test
    public void shouldReturnPaymentType_whenGetPaymentTypeByIdIsCalled() throws Exception {

        when(paymentTypeRepository.getOne("cash")).thenReturn(paymentType);

        PaymentType retrievedPaymentType = paymentTypeService.getPaymentTypeById("cash");

        assertThat(retrievedPaymentType).isEqualTo(paymentType);
    }



}

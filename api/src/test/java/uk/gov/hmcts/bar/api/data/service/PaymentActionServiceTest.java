package uk.gov.hmcts.bar.api.data.service;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.ff4j.FF4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import uk.gov.hmcts.bar.api.data.model.PaymentInstructionAction;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionActionRepository;

public class PaymentActionServiceTest {

	private PaymentActionService paymentActionService;

	@Mock
	private PaymentInstructionActionRepository paymentInstructionActionRepository;

	@Mock
	private FF4j ff4j;
	
	private PaymentInstructionAction pia = new PaymentInstructionAction("Process");
	private List<PaymentInstructionAction> piaList = new ArrayList<>(Arrays.asList(pia));

	@Before
	public void setupMock() {
		MockitoAnnotations.initMocks(this);
		paymentActionService = new PaymentActionService(paymentInstructionActionRepository, ff4j);

	}
	
	@Test
	public void whenGetAllPaymentInstructionMethodCalled_thenItShouldReturnActionList() {
		when(paymentInstructionActionRepository.findAll()).thenReturn(piaList);
		when(ff4j.check(Mockito.anyString())).thenReturn(true);
		List<PaymentInstructionAction> resultList = paymentActionService.getAllPaymentInstructionAction();
		Assertions.assertThat(resultList).isEqualTo (piaList);
	}

}

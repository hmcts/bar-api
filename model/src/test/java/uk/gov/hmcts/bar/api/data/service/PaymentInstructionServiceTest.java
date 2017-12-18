package uk.gov.hmcts.bar.api.data.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;

import com.google.common.collect.Lists;

import uk.gov.hmcts.bar.api.data.exceptions.PaymentInstructionNotFoundException;
import uk.gov.hmcts.bar.api.data.model.AllPayPaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.CashPaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.ChequePaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionRequest;
import uk.gov.hmcts.bar.api.data.model.PaymentReference;
import uk.gov.hmcts.bar.api.data.model.PostalOrderPaymentInstruction;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionRepository;
import uk.gov.hmcts.bar.api.data.utils.Util;

@SuppressWarnings("unchecked")
public class PaymentInstructionServiceTest {

    @InjectMocks
    private PaymentInstructionService paymentInstructionServiceMock;

    @Mock
    private PaymentInstructionRepository paymentInstructionRepository;

    @Mock
    private PaymentReferenceService paymentReferenceService;

    @Mock
    private PaymentReference paymentReferenceMock;

    @Mock
    private Page<PaymentInstruction> piPageMock;

    @Mock
    private Iterator<PaymentInstruction> piIteratorMock;

    @Mock
    private PaymentInstruction paymentInstructionMock;
    
    @Mock
    private ChequePaymentInstruction chequePaymentInstructionMock;

    @Mock
    private AllPayPaymentInstruction allpayPaymentInstructionMock;

    @Mock
    private PostalOrderPaymentInstruction postalOrderPaymentInstructionMock;


    @Mock
    private CashPaymentInstruction cashPaymentInstructionMock;

    @Mock
    private PaymentInstructionRequest paymentRequestMock;

    @Mock
    private Util utilMock;
    
    private String[] propertyNames = {"chequeNumber"};

    private PaymentInstructionService paymentInstructionService;

    @Before
    public void setupMock() {
        MockitoAnnotations.initMocks(this);
       paymentInstructionService = new PaymentInstructionService(paymentReferenceService,paymentInstructionRepository);
    }

    @Test
    public void shouldReturnPaymentInstruction_whenSavePaymentInstructionForGivenChequeInstructionIsCalled() throws Exception {

        when(paymentReferenceService.getNextPaymentReferenceSequenceBySite(anyString())).thenReturn(paymentReferenceMock);
        when(paymentInstructionRepository.saveAndFlush(any(ChequePaymentInstruction.class))).thenReturn(chequePaymentInstructionMock);
        when(paymentInstructionRepository.findOne(anyInt())).thenReturn(any(AllPayPaymentInstruction.class));
        PaymentInstruction createdPaymentInstruction = paymentInstructionServiceMock.createPaymentInstruction(chequePaymentInstructionMock);
        verify(paymentReferenceService,times(1)).getNextPaymentReferenceSequenceBySite(anyString());
        verify(paymentInstructionRepository, times(1)).saveAndFlush(chequePaymentInstructionMock);
        verify(paymentInstructionRepository, times(1)).refresh(chequePaymentInstructionMock);

    }

    @Test
    public void shouldReturnPaymentInstruction_whenSavePaymentInstructionForGivenCashInstructionIsCalled() throws Exception {

        when(paymentReferenceService.getNextPaymentReferenceSequenceBySite(anyString())).thenReturn(paymentReferenceMock);
        when(paymentInstructionRepository.saveAndFlush(any(CashPaymentInstruction.class))).thenReturn(cashPaymentInstructionMock);
        PaymentInstruction createdPaymentInstruction = paymentInstructionServiceMock.createPaymentInstruction(cashPaymentInstructionMock);
        verify(paymentReferenceService,times(1)).getNextPaymentReferenceSequenceBySite(anyString());
        verify(paymentInstructionRepository, times(1)).saveAndFlush(cashPaymentInstructionMock);
        verify(paymentInstructionRepository, times(1)).refresh(cashPaymentInstructionMock);


    }

    @Test
    public void shouldReturnPaymentInstruction_whenSavePaymentInstructionForGivenPostalOrderInstructionIsCalled() throws Exception {

        when(paymentReferenceService.getNextPaymentReferenceSequenceBySite(anyString())).thenReturn(paymentReferenceMock);
        when(paymentInstructionRepository.saveAndFlush(any(PostalOrderPaymentInstruction.class))).thenReturn(postalOrderPaymentInstructionMock);
        PaymentInstruction createdPaymentInstruction = paymentInstructionServiceMock.createPaymentInstruction(postalOrderPaymentInstructionMock);
        verify(paymentReferenceService,times(1)).getNextPaymentReferenceSequenceBySite(anyString());
        verify(paymentInstructionRepository, times(1)).saveAndFlush(postalOrderPaymentInstructionMock);
        verify(paymentInstructionRepository, times(1)).refresh(postalOrderPaymentInstructionMock);


    }

    @Test
    public void shouldReturnPaymentInstruction_whenSavePaymentInstructionForGivenAllPayInstructionIsCalled() throws Exception {

        when(paymentReferenceService.getNextPaymentReferenceSequenceBySite(anyString())).thenReturn(paymentReferenceMock);
        when(paymentInstructionRepository.saveAndFlush(any(AllPayPaymentInstruction.class))).thenReturn(allpayPaymentInstructionMock);
        PaymentInstruction createdPaymentInstruction = paymentInstructionServiceMock.createPaymentInstruction(allpayPaymentInstructionMock);
        verify(paymentReferenceService,times(1)).getNextPaymentReferenceSequenceBySite(anyString());
        verify(paymentInstructionRepository, times(1)).saveAndFlush(allpayPaymentInstructionMock);
        verify(paymentInstructionRepository, times(1)).refresh(allpayPaymentInstructionMock);


    }

    @Test
    public void shouldDeletePaymentInstruction_whenDeletePaymentInstructionIsCalled() throws Exception {

       paymentInstructionServiceMock.deletePaymentInstruction(1);

        verify(paymentInstructionRepository, times(1)).delete(1);
    }


    @Test(expected = PaymentInstructionNotFoundException.class)
    public void shouldThrowPaymentInstructionNotFoundException_whenDeletePaymentInstructionIsCalledAndNotFound() throws Exception {
        PaymentInstructionService service = mock(PaymentInstructionService.class);
        doThrow(PaymentInstructionNotFoundException.class).when(service).deletePaymentInstruction(1);
        service.deletePaymentInstruction(1);

    }

    @Test
    public void shouldDeleteDraftPaymentInstruction_whenDeletePaymentInstructionIsCalled() {

        ArgumentCaptor<Integer> idCapture = ArgumentCaptor.forClass(Integer.class);

        paymentInstructionServiceMock.deletePaymentInstruction(1);
        verify(paymentInstructionRepository, times(1)).delete(idCapture.capture());

    }


	@Test
	public void shouldReturnPaymentInstructionList_whenGetAllPaymentInstructionsIsCalledWithNoParams() throws Exception {

		when(paymentInstructionRepository.findAll(Mockito.any(Specifications.class),Mockito.any(Pageable.class))).thenReturn(piPageMock);
		when(piPageMock.iterator()).thenReturn(piIteratorMock);

		List<PaymentInstruction> retrievedPaymentInstructionList = paymentInstructionService
				.getAllPaymentInstructions(null, null, null);
		assertEquals(Lists.newArrayList(piIteratorMock), retrievedPaymentInstructionList);
	}

	@Test
	public void shouldReturnPaymentInstructionList_whenGetAllPaymentInstructionsIsCalledWithAllParams() throws Exception {

		when(paymentInstructionRepository.findAll(Mockito.any(Specifications.class),Mockito.any(Pageable.class))).thenReturn(piPageMock);
		when(piPageMock.iterator()).thenReturn(piIteratorMock);

		List<PaymentInstruction> retrievedPaymentInstructionList = paymentInstructionService
				.getAllPaymentInstructions("BR01", new Date(), new Date());
		assertEquals(Lists.newArrayList(piIteratorMock), retrievedPaymentInstructionList);
	}

	@Test
	public void shouldReturnPaymentInstructionList_whenGetAllPaymentInstructionsIsCalledWithOnlyStatus() throws Exception {

		when(paymentInstructionRepository.findAll(Mockito.any(Specifications.class),Mockito.any(Pageable.class))).thenReturn(piPageMock);
		when(piPageMock.iterator()).thenReturn(piIteratorMock);

		List<PaymentInstruction> retrievedPaymentInstructionList = paymentInstructionService
				.getAllPaymentInstructions("BR01", null, null);
		assertEquals(Lists.newArrayList(piIteratorMock), retrievedPaymentInstructionList);
	}

	@Test
	public void shouldReturnPaymentInstructionList_whenGetAllPaymentInstructionsIsCalledWithOnlyStartDate() throws Exception {

		when(paymentInstructionRepository.findAll(Mockito.any(Specifications.class),Mockito.any(Pageable.class))).thenReturn(piPageMock);
		when(piPageMock.iterator()).thenReturn(piIteratorMock);

		List<PaymentInstruction> retrievedPaymentInstructionList = paymentInstructionService
				.getAllPaymentInstructions(null, new Date(), null);
		assertEquals(Lists.newArrayList(piIteratorMock), retrievedPaymentInstructionList);
	}

	@Test
	public void shouldReturnPaymentInstructionList_whenGetAllPaymentInstructionsIsCalledWithOnlyEndDate() throws Exception {

		when(paymentInstructionRepository.findAll(Mockito.any(Specifications.class),Mockito.any(Pageable.class))).thenReturn(piPageMock);
		when(piPageMock.iterator()).thenReturn(piIteratorMock);

		List<PaymentInstruction> retrievedPaymentInstructionList = paymentInstructionService
				.getAllPaymentInstructions(null, null, new Date());
		assertEquals(Lists.newArrayList(piIteratorMock), retrievedPaymentInstructionList);
	}

	@Test
	public void shouldReturnPaymentInstructionList_whenGetAllPaymentInstructionsIsCalledWithOnlyStartDateAndEndDate() throws Exception {

		when(paymentInstructionRepository.findAll(Mockito.any(Specifications.class),Mockito.any(Pageable.class))).thenReturn(piPageMock);
		when(piPageMock.iterator()).thenReturn(piIteratorMock);

		List<PaymentInstruction> retrievedPaymentInstructionList = paymentInstructionService
				.getAllPaymentInstructions(null, new Date(), new Date());
		assertEquals(Lists.newArrayList(piIteratorMock), retrievedPaymentInstructionList);
	}

	@Test
	public void shouldReturnPaymentInstructionList_whenGetAllPaymentInstructionsIsCalledWithOnlyStatusAndEndDate() throws Exception {

		when(paymentInstructionRepository.findAll(Mockito.any(Specifications.class),Mockito.any(Pageable.class))).thenReturn(piPageMock);
		when(piPageMock.iterator()).thenReturn(piIteratorMock);

		List<PaymentInstruction> retrievedPaymentInstructionList = paymentInstructionService
				.getAllPaymentInstructions("BR01", null, new Date());
		assertEquals(Lists.newArrayList(piIteratorMock), retrievedPaymentInstructionList);
	}

	@Test
	public void shouldReturnPaymentInstructionList_whenGetAllPaymentInstructionsIsCalledWithOnlyStatusAndStartDate() throws Exception {

		when(paymentInstructionRepository.findAll(Mockito.any(Specifications.class),Mockito.any(Pageable.class))).thenReturn(piPageMock);
		when(piPageMock.iterator()).thenReturn(piIteratorMock);

		List<PaymentInstruction> retrievedPaymentInstructionList = paymentInstructionService
				.getAllPaymentInstructions("BR01", new Date(), null);
		assertEquals(Lists.newArrayList(piIteratorMock), retrievedPaymentInstructionList);
	}

	@Test
	public void shouldReturnPaymentInstruction_whenGetPaymentInstructionIsCalledForId() {
		when(paymentInstructionRepository.findOne(Mockito.any(Integer.class))).thenReturn(paymentInstructionMock);

		PaymentInstruction pi = paymentInstructionService.getPaymentInstruction(3);
		assertEquals(paymentInstructionMock, pi);
	}

	@Test
	public void shouldReturnNull_whenGetPaymentInstructionIsCalledForWrongId() {
		when(paymentInstructionRepository.findOne(Mockito.any(Integer.class))).thenReturn(null);

		PaymentInstruction pi = paymentInstructionService.getPaymentInstruction(3);
		assertEquals(null, pi);
	}

	@Ignore
    @Test
    public void shouldReturnUpdatedPaymentInstruction_whenUpdatePaymentInstructionForGivenPaymentInstructionIsCalled() throws Exception {

        when(paymentInstructionRepository.findById(anyInt())).thenReturn(Optional.of(paymentInstructionMock));
        when(paymentInstructionRepository.saveAndFlush(any(PaymentInstruction.class))).thenReturn(paymentInstructionMock);
     //   when(paymentInstructionServiceMock.updatePaymentInstruction(1,paymentRequestMock)).thenReturn(paymentInstructionMock);
        when(Util.getNullPropertyNames(paymentRequestMock)).thenReturn(propertyNames);
        PaymentInstruction updatedPaymentInstruction = paymentInstructionServiceMock.updatePaymentInstruction(1,paymentRequestMock);
        verify(paymentInstructionRepository, times(1)).saveAndFlush(paymentInstructionMock);
        verify(paymentInstructionRepository, times(1)).refresh(paymentInstructionMock);


    }


}


package uk.gov.hmcts.bar.api.data.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
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

import uk.gov.hmcts.bar.api.data.enums.PaymentStatusEnum;
import uk.gov.hmcts.bar.api.data.exceptions.PaymentInstructionNotFoundException;
import uk.gov.hmcts.bar.api.data.model.AllPayPaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.CashPaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.ChequePaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentReference;
import uk.gov.hmcts.bar.api.data.model.PaymentReferenceKey;
import uk.gov.hmcts.bar.api.data.model.PostalOrderPaymentInstruction;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionRepository;

@SuppressWarnings("unchecked")
public class PaymentInstructionServiceTest {

    @InjectMocks
    private PaymentInstructionService paymentInstructionServiceMock;

    @Mock
    private PaymentInstructionRepository paymentInstructionRepository;

    @Mock
    private PaymentReferenceService paymentReferenceService;

    @Mock
    private ArrayList<PaymentInstruction> paymentInstructionList;
    
    @Mock
    private Page<PaymentInstruction> piPageMock;
    
    @Mock
    private Iterator<PaymentInstruction> piIteratorMock;
    
    @Mock
    private PaymentInstruction paymentInstructionMock;
    
    private PaymentInstructionService paymentInstructionService;
    
    @Before
    public void setupMock() {
        MockitoAnnotations.initMocks(this);
       paymentInstructionService = new PaymentInstructionService(paymentReferenceService,paymentInstructionRepository);
    }

    @Test
    public void shouldReturnPaymentInstruction_whenSavePaymentInstructionForGivenChequeInstructionIsCalled() throws Exception {

        PaymentInstruction savedChequePaymentInstruction = ChequePaymentInstruction.chequePaymentInstructionWith()
            .amount(200).currency("GBP").chequeNumber("000000").payerName("Mr Payer Payer")
            .build();

        PaymentReference paymentReference = new PaymentReference(new PaymentReferenceKey("BR01", LocalDate.now()),1);

        savedChequePaymentInstruction.setStatus(PaymentStatusEnum.DRAFT.dbKey());
        when(paymentReferenceService.getNextPaymentReferenceSequenceBySite(paymentReference.getPaymentReferenceKey().getSiteId())).thenReturn(paymentReference);
        when(paymentInstructionRepository.save(savedChequePaymentInstruction)).thenReturn(savedChequePaymentInstruction);
        PaymentInstruction createdPaymentInstruction = paymentInstructionServiceMock.createPaymentInstruction(savedChequePaymentInstruction);

        assertEquals(savedChequePaymentInstruction,createdPaymentInstruction);


    }

    @Test
    public void shouldReturnPaymentInstruction_whenSavePaymentInstructionForGivenCashInstructionIsCalled() throws Exception {

        PaymentInstruction savedCashPaymentInstruction = CashPaymentInstruction.cashPaymentInstructionWith()
            .amount(200).currency("GBP").payerName("Mr Payer Payer").build();

        PaymentReference paymentReference = new PaymentReference(new PaymentReferenceKey("BR01", LocalDate.now()),1);

        savedCashPaymentInstruction.setStatus(PaymentStatusEnum.DRAFT.dbKey());
        when(paymentReferenceService.getNextPaymentReferenceSequenceBySite(paymentReference.getPaymentReferenceKey().getSiteId())).thenReturn(paymentReference);
        when(paymentInstructionRepository.save(savedCashPaymentInstruction)).thenReturn(savedCashPaymentInstruction);

        PaymentInstruction createdPaymentInstruction = paymentInstructionServiceMock.createPaymentInstruction(savedCashPaymentInstruction);

        assertEquals(savedCashPaymentInstruction,createdPaymentInstruction);


    }



    @Test
    public void shouldReturnPaymentInstruction_whenSavePaymentInstructionForGivenPostalOrderInstructionIsCalled() throws Exception {

        PaymentInstruction savedPostalOrderPaymentInstruction = PostalOrderPaymentInstruction.postalOrderPaymentInstructionWith()
            .amount(200).currency("GBP").payerName("Mr Payer Payer").postalOrderNumber("000000").build();

        PaymentReference paymentReference = new PaymentReference(new PaymentReferenceKey("BR01", LocalDate.now()),1);
        savedPostalOrderPaymentInstruction.setStatus(PaymentStatusEnum.DRAFT.dbKey());

        when(paymentReferenceService.getNextPaymentReferenceSequenceBySite(paymentReference.getPaymentReferenceKey().getSiteId())).thenReturn(paymentReference);
        when(paymentInstructionRepository.save(savedPostalOrderPaymentInstruction)).thenReturn(savedPostalOrderPaymentInstruction);

        PaymentInstruction createdPaymentInstruction = paymentInstructionServiceMock.createPaymentInstruction(savedPostalOrderPaymentInstruction);

        assertEquals(savedPostalOrderPaymentInstruction,createdPaymentInstruction);


    }

    @Test
    public void shouldReturnPaymentInstruction_whenSavePaymentInstructionForGivenAllPayInstructionIsCalled() throws Exception {

        PaymentInstruction savedAllPayPaymentInstruction = AllPayPaymentInstruction.allPayPaymentInstructionWith()
            .amount(200).currency("GBP").payerName("Mr Payer Payer").allPayTransactionId("allpayid").build();

        PaymentReference paymentReference = new PaymentReference(new PaymentReferenceKey("BR01", LocalDate.now()),1);
        savedAllPayPaymentInstruction.setStatus(PaymentStatusEnum.DRAFT.dbKey());

        when(paymentReferenceService.getNextPaymentReferenceSequenceBySite(paymentReference.getPaymentReferenceKey().getSiteId())).thenReturn(paymentReference);
        when(paymentInstructionRepository.save(savedAllPayPaymentInstruction)).thenReturn(savedAllPayPaymentInstruction);

        PaymentInstruction createdPaymentInstruction = paymentInstructionServiceMock.createPaymentInstruction(savedAllPayPaymentInstruction);

        assertEquals(savedAllPayPaymentInstruction,createdPaymentInstruction);


    }


    @Test
    public void shouldDeletePaymentInstruction_whenDeletePaymentInstructionIsCalled() throws Exception {

       paymentInstructionServiceMock.deleteCurrentPaymentInstructionWithDraftStatus(1);

        verify(paymentInstructionRepository, times(1)).deletePaymentInstructionByIdAndStatusAndPaymentDateAfter(1,PaymentStatusEnum.DRAFT.dbKey(),LocalDateTime.now().truncatedTo(ChronoUnit.DAYS));
    }


    @Test(expected = PaymentInstructionNotFoundException.class)
    public void shouldThrowPaymentInstructionNotFoundException_whenDeletePaymentInstructionIsCalledAndNotFound() throws Exception {
        PaymentInstructionService service = mock(PaymentInstructionService.class);
        doThrow(PaymentInstructionNotFoundException.class).when(service).deleteCurrentPaymentInstructionWithDraftStatus(1);
        service.deleteCurrentPaymentInstructionWithDraftStatus(1);

    }

    @Test
    public void shouldDeleteDraftPaymentInstruction_whenDeletePaymentInstructionIsCalled() {

        ArgumentCaptor<Integer> idCapture = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> statusCapture = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<LocalDateTime> dateTimeCapture = ArgumentCaptor.forClass(LocalDateTime.class);

        paymentInstructionServiceMock.deleteCurrentPaymentInstructionWithDraftStatus(1);
        verify(paymentInstructionRepository, times(1)).deletePaymentInstructionByIdAndStatusAndPaymentDateAfter(idCapture.capture(),statusCapture.capture(),dateTimeCapture.capture());

        assertEquals("D", statusCapture.getValue());
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


}


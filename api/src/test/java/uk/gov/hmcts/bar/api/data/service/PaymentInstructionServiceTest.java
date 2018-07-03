package uk.gov.hmcts.bar.api.data.service;

import com.google.common.collect.Lists;
import org.apache.commons.collections.MultiMap;
import org.ff4j.FF4j;
import org.ff4j.exception.FeatureAccessException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import uk.gov.hmcts.bar.api.data.enums.PaymentActionEnum;
import uk.gov.hmcts.bar.api.data.exceptions.PaymentInstructionNotFoundException;
import uk.gov.hmcts.bar.api.data.model.*;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionSearchCriteriaDto.PaymentInstructionSearchCriteriaDtoBuilder;
import uk.gov.hmcts.bar.api.data.repository.BankGiroCreditRepository;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionRepository;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionStatusRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

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
    private BarUserService barUserServiceMock;
    
    @Mock
    private BarUser barUserMock;

    @Mock
    private FF4j ff4jMock;

    @Mock
    private BankGiroCreditRepository bankGiroCreditRepositoryMock;

    @Mock
    private PaymentInstructionStatusRepository paymentInstructionStatusRepositoryMock;

    private PaymentInstructionStatus paymentInstructionStatus;

    private PaymentInstructionStatusReferenceKey paymentInstructionStatusReferenceKey;

    private PaymentInstructionService paymentInstructionService;

    private PaymentInstructionSearchCriteriaDtoBuilder paymentInstructionSearchCriteriaDtoBuilder;

    @Before
    public void setupMock() {
        MockitoAnnotations.initMocks(this);
        paymentInstructionService = new PaymentInstructionService(paymentReferenceService,
            paymentInstructionRepository, barUserServiceMock,paymentInstructionStatusRepositoryMock, ff4jMock, bankGiroCreditRepositoryMock);
        paymentInstructionSearchCriteriaDtoBuilder = PaymentInstructionSearchCriteriaDto.paymentInstructionSearchCriteriaDto()
            .siteId("BR01");
        paymentInstructionStatusReferenceKey = new PaymentInstructionStatusReferenceKey(0, "status");
        paymentInstructionStatus = new PaymentInstructionStatus(paymentInstructionStatusReferenceKey, null);
    }

    @Test
    public void shouldReturnPaymentInstruction_whenSavePaymentInstructionForGivenChequeInstructionIsCalled()
        throws Exception {

        when(paymentReferenceService.getNextPaymentReferenceSequenceBySite(anyString()))
            .thenReturn(paymentReferenceMock);
        when(paymentInstructionRepository.saveAndFlush(any(ChequePaymentInstruction.class)))
            .thenReturn(chequePaymentInstructionMock);
        when(paymentInstructionRepository.saveAndRefresh(any(ChequePaymentInstruction.class)))
            .thenReturn(paymentInstructionMock);
        when(paymentInstructionMock.getStatus()).thenReturn("status");
        PaymentInstruction createdPaymentInstruction = paymentInstructionServiceMock
            .createPaymentInstruction(chequePaymentInstructionMock);
        verify(paymentReferenceService, times(1)).getNextPaymentReferenceSequenceBySite(anyString());
        verify(paymentInstructionRepository, times(1)).saveAndRefresh(chequePaymentInstructionMock);
        verify(paymentInstructionStatusRepositoryMock, times(1)).save(paymentInstructionStatus);
    }

    @Test
    public void shouldReturnPaymentInstruction_whenSavePaymentInstructionForGivenCashInstructionIsCalled()
        throws Exception {

        when(paymentReferenceService.getNextPaymentReferenceSequenceBySite(anyString()))
            .thenReturn(paymentReferenceMock);
        when(paymentInstructionRepository.saveAndFlush(any(CashPaymentInstruction.class)))
            .thenReturn(cashPaymentInstructionMock);
        when(paymentInstructionRepository.saveAndRefresh(any(CashPaymentInstruction.class)))
            .thenReturn(paymentInstructionMock);
        when(paymentInstructionMock.getStatus()).thenReturn("status");
        PaymentInstruction createdPaymentInstruction = paymentInstructionServiceMock
            .createPaymentInstruction(cashPaymentInstructionMock);
        verify(paymentReferenceService, times(1)).getNextPaymentReferenceSequenceBySite(anyString());
        verify(paymentInstructionRepository, times(1)).saveAndRefresh(cashPaymentInstructionMock);
        verify(paymentInstructionStatusRepositoryMock, times(1)).save(paymentInstructionStatus);

    }

    @Test
    public void shouldReturnPaymentInstruction_whenSavePaymentInstructionForGivenPostalOrderInstructionIsCalled()
        throws Exception {

        when(paymentReferenceService.getNextPaymentReferenceSequenceBySite(anyString()))
            .thenReturn(paymentReferenceMock);
        when(paymentInstructionRepository.saveAndFlush(any(PostalOrderPaymentInstruction.class)))
            .thenReturn(postalOrderPaymentInstructionMock);
        when(paymentInstructionRepository.saveAndRefresh(any(PostalOrderPaymentInstruction.class)))
            .thenReturn(paymentInstructionMock);
        when(paymentInstructionMock.getStatus()).thenReturn("status");
        PaymentInstruction createdPaymentInstruction = paymentInstructionServiceMock
            .createPaymentInstruction(postalOrderPaymentInstructionMock);
        verify(paymentReferenceService, times(1)).getNextPaymentReferenceSequenceBySite(anyString());
        verify(paymentInstructionRepository, times(1)).saveAndRefresh(postalOrderPaymentInstructionMock);
        verify(paymentInstructionStatusRepositoryMock, times(1)).save(paymentInstructionStatus);
    }

    @Test
    public void shouldReturnPaymentInstruction_whenSavePaymentInstructionForGivenAllPayInstructionIsCalled()
        throws Exception {

        when(paymentReferenceService.getNextPaymentReferenceSequenceBySite(anyString()))
            .thenReturn(paymentReferenceMock);
        when(paymentInstructionRepository.saveAndFlush(any(AllPayPaymentInstruction.class)))
            .thenReturn(allpayPaymentInstructionMock);
        when(paymentInstructionRepository.saveAndRefresh(any(AllPayPaymentInstruction.class)))
            .thenReturn(paymentInstructionMock);
        when(paymentInstructionMock.getStatus()).thenReturn("status");
        PaymentInstruction createdPaymentInstruction = paymentInstructionServiceMock
            .createPaymentInstruction(allpayPaymentInstructionMock);
        verify(paymentReferenceService, times(1)).getNextPaymentReferenceSequenceBySite(anyString());
        verify(paymentInstructionRepository, times(1)).saveAndRefresh(allpayPaymentInstructionMock);
        verify(paymentInstructionStatusRepositoryMock, times(1)).save(paymentInstructionStatus);
    }

    @Test
    public void shouldDeletePaymentInstruction_whenDeletePaymentInstructionIsCalled() throws Exception {

        paymentInstructionServiceMock.deletePaymentInstruction(1);

        verify(paymentInstructionRepository, times(1)).deleteById(1);
    }

    @Test(expected = PaymentInstructionNotFoundException.class)
    public void shouldThrowPaymentInstructionNotFoundException_whenDeletePaymentInstructionIsCalledAndNotFound()
        throws Exception {
        PaymentInstructionService service = mock(PaymentInstructionService.class);
        doThrow(PaymentInstructionNotFoundException.class).when(service).deletePaymentInstruction(1);
        service.deletePaymentInstruction(1);

    }

    @Test
    public void shouldDeleteDraftPaymentInstruction_whenDeletePaymentInstructionIsCalled() {

        ArgumentCaptor<Integer> idCapture = ArgumentCaptor.forClass(Integer.class);

        paymentInstructionServiceMock.deletePaymentInstruction(1);
        verify(paymentInstructionRepository, times(1)).deleteById(idCapture.capture());

    }

    @Test
    public void shouldReturnPaymentInstructionList_whenGetAllPaymentInstructionsIsCalledWithNoParams()
        throws Exception {

        when(paymentInstructionRepository.findAll(Mockito.any(Specifications.class), Mockito.any(Pageable.class)))
            .thenReturn(piPageMock);
        when(piPageMock.iterator()).thenReturn(piIteratorMock);

        PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto = PaymentInstructionSearchCriteriaDto
            .paymentInstructionSearchCriteriaDto().build();

        List<PaymentInstruction> retrievedPaymentInstructionList = paymentInstructionService
            .getAllPaymentInstructions(paymentInstructionSearchCriteriaDto);
        assertEquals(Lists.newArrayList(piIteratorMock), retrievedPaymentInstructionList);
    }

    @Test
    public void shouldReturnPaymentInstructionList_whenGetAllPaymentInstructionsIsCalledWithAllParams()
        throws Exception {

        when(paymentInstructionRepository.findAll(Mockito.any(Specifications.class), Mockito.any(Pageable.class)))
            .thenReturn(piPageMock);
        when(piPageMock.iterator()).thenReturn(piIteratorMock);

        PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto = paymentInstructionSearchCriteriaDtoBuilder
            .status("D").action("Suspense").startDate(LocalDate.now().atStartOfDay()).endDate(LocalDate.now().atTime(LocalTime.now()))
            .build();

        List<PaymentInstruction> retrievedPaymentInstructionList = paymentInstructionService
            .getAllPaymentInstructions(paymentInstructionSearchCriteriaDto);
        assertEquals(Lists.newArrayList(piIteratorMock), retrievedPaymentInstructionList);
    }

    @Test
    public void shouldReturnPaymentInstructionList_whenGetAllPaymentInstructionsIsCalledWithOnlyStatus()
        throws Exception {

        when(paymentInstructionRepository.findAll(Mockito.any(Specifications.class), Mockito.any(Pageable.class)))
            .thenReturn(piPageMock);
        when(piPageMock.iterator()).thenReturn(piIteratorMock);

        PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto = paymentInstructionSearchCriteriaDtoBuilder
            .status("D").build();

        List<PaymentInstruction> retrievedPaymentInstructionList = paymentInstructionService
            .getAllPaymentInstructions(paymentInstructionSearchCriteriaDto);
        assertEquals(Lists.newArrayList(piIteratorMock), retrievedPaymentInstructionList);
    }

    @Test
    public void shouldReturnPaymentInstructionList_whenGetAllPaymentInstructionsIsCalledWithOnlyStartDate()
        throws Exception {

        when(paymentInstructionRepository.findAll(Mockito.any(Specifications.class), Mockito.any(Pageable.class)))
            .thenReturn(piPageMock);
        when(piPageMock.iterator()).thenReturn(piIteratorMock);

        PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto = paymentInstructionSearchCriteriaDtoBuilder
            .startDate(LocalDate.now().atStartOfDay()).build();

        List<PaymentInstruction> retrievedPaymentInstructionList = paymentInstructionService
            .getAllPaymentInstructions(paymentInstructionSearchCriteriaDto);
        assertEquals(Lists.newArrayList(piIteratorMock), retrievedPaymentInstructionList);
    }

    @Test
    public void shouldReturnPaymentInstructionList_whenGetAllPaymentInstructionsIsCalledWithOnlyEndDate()
        throws Exception {

        when(paymentInstructionRepository.findAll(Mockito.any(Specifications.class), Mockito.any(Pageable.class)))
            .thenReturn(piPageMock);
        when(piPageMock.iterator()).thenReturn(piIteratorMock);

        PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto = paymentInstructionSearchCriteriaDtoBuilder
            .endDate(LocalDate.now().atTime(LocalTime.now())).build();

        List<PaymentInstruction> retrievedPaymentInstructionList = paymentInstructionService
            .getAllPaymentInstructions(paymentInstructionSearchCriteriaDto);
        assertEquals(Lists.newArrayList(piIteratorMock), retrievedPaymentInstructionList);
    }

    @Test
    public void shouldReturnPaymentInstructionList_whenGetAllPaymentInstructionsIsCalledWithOnlyPayerName()
        throws Exception {

        when(paymentInstructionRepository.findAll(Mockito.any(Specifications.class), Mockito.any(Pageable.class)))
            .thenReturn(piPageMock);
        when(piPageMock.iterator()).thenReturn(piIteratorMock);

        PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto = paymentInstructionSearchCriteriaDtoBuilder
            .payerName("Mr Payer Payer").build();

        List<PaymentInstruction> retrievedPaymentInstructionList = paymentInstructionService
            .getAllPaymentInstructions(paymentInstructionSearchCriteriaDto);
        assertEquals(Lists.newArrayList(piIteratorMock), retrievedPaymentInstructionList);
    }

    @Test
    public void shouldReturnPaymentInstructionList_whenGetAllPaymentInstructionsIsCalledWithOnlyChequeNumber()
        throws Exception {

        when(paymentInstructionRepository.findAll(Mockito.any(Specifications.class), Mockito.any(Pageable.class)))
            .thenReturn(piPageMock);
        when(piPageMock.iterator()).thenReturn(piIteratorMock);

        PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto = paymentInstructionSearchCriteriaDtoBuilder
            .chequeNumber("000000").build();

        List<PaymentInstruction> retrievedPaymentInstructionList = paymentInstructionService
            .getAllPaymentInstructions(paymentInstructionSearchCriteriaDto);
        assertEquals(Lists.newArrayList(piIteratorMock), retrievedPaymentInstructionList);
    }


    @Test
    public void shouldReturnPaymentInstructionList_whenGetAllPaymentInstructionsIsCalledWithOnlyStartDateAndEndDate()
        throws Exception {

        when(paymentInstructionRepository.findAll(Mockito.any(Specifications.class), Mockito.any(Pageable.class)))
            .thenReturn(piPageMock);
        when(piPageMock.iterator()).thenReturn(piIteratorMock);

        PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto = paymentInstructionSearchCriteriaDtoBuilder
            .startDate(LocalDate.now().atStartOfDay()).endDate(LocalDate.now().atTime(LocalTime.now())).build();

        List<PaymentInstruction> retrievedPaymentInstructionList = paymentInstructionService
            .getAllPaymentInstructions(paymentInstructionSearchCriteriaDto);
        assertEquals(Lists.newArrayList(piIteratorMock), retrievedPaymentInstructionList);
    }

    @Test
    public void shouldReturnPaymentInstructionList_whenGetAllPaymentInstructionsIsCalledWithOnlyStatusAndEndDate()
        throws Exception {

        when(paymentInstructionRepository.findAll(Mockito.any(Specifications.class), Mockito.any(Pageable.class)))
            .thenReturn(piPageMock);
        when(piPageMock.iterator()).thenReturn(piIteratorMock);

        PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto = paymentInstructionSearchCriteriaDtoBuilder
            .endDate(LocalDate.now().atTime(LocalTime.now())).build();

        List<PaymentInstruction> retrievedPaymentInstructionList = paymentInstructionService
            .getAllPaymentInstructions(paymentInstructionSearchCriteriaDto);
        assertEquals(Lists.newArrayList(piIteratorMock), retrievedPaymentInstructionList);
    }

    @Test
    public void shouldReturnPaymentInstructionList_whenGetAllPaymentInstructionsIsCalledWithOnlyStatusAndStartDate()
        throws Exception {

        when(paymentInstructionRepository.findAll(Mockito.any(Specifications.class), Mockito.any(Pageable.class)))
            .thenReturn(piPageMock);
        when(piPageMock.iterator()).thenReturn(piIteratorMock);

        PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto = paymentInstructionSearchCriteriaDtoBuilder
            .startDate(LocalDate.now().atStartOfDay()).build();

        List<PaymentInstruction> retrievedPaymentInstructionList = paymentInstructionService
            .getAllPaymentInstructions(paymentInstructionSearchCriteriaDto);
        assertEquals(Lists.newArrayList(piIteratorMock), retrievedPaymentInstructionList);
    }

    @Test
    public void shouldReturnPaymentInstructionList_whenGetAllPaymentInstructionsIsCalledWithOnlyDailySequenceId()
        throws Exception {

        when(paymentInstructionRepository.findAll(Mockito.any(Specifications.class), Mockito.any(Pageable.class)))
            .thenReturn(piPageMock);
        when(piPageMock.iterator()).thenReturn(piIteratorMock);

        PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto = paymentInstructionSearchCriteriaDtoBuilder
            .dailySequenceId(1).build();

        List<PaymentInstruction> retrievedPaymentInstructionList = paymentInstructionService
            .getAllPaymentInstructions(paymentInstructionSearchCriteriaDto);
        assertEquals(Lists.newArrayList(piIteratorMock), retrievedPaymentInstructionList);
    }

    @Test
    public void shouldReturnPaymentInstruction_whenGetPaymentInstructionIsCalledForId() {
        Optional<PaymentInstruction> op = Optional.of(paymentInstructionMock);
        when(paymentInstructionRepository.findById(Mockito.any(Integer.class))).thenReturn(op);

        PaymentInstruction pi = paymentInstructionService.getPaymentInstruction(3);
        assertEquals(paymentInstructionMock, pi);
    }

    @Test
    public void shouldReturnNull_whenGetPaymentInstructionIsCalledForWrongId() {
        when(paymentInstructionRepository.getOne(Mockito.any(Integer.class))).thenReturn(null);

        PaymentInstruction pi = paymentInstructionService.getPaymentInstruction(3);
        assertEquals(null, pi);
    }

    @Test
    public void shouldReturnSubmittedPaymentInstruction_whenSubmitPaymentInstructionForGivenPaymentInstructionIsCalled()
        throws Exception {
        PaymentInstructionUpdateRequest pir = PaymentInstructionUpdateRequest.paymentInstructionUpdateRequestWith()
            .status("D").build();
        when(paymentInstructionRepository.findById(anyInt())).thenReturn(Optional.of(paymentInstructionMock));
        when(paymentInstructionRepository.saveAndRefresh(any(PaymentInstruction.class)))
            .thenReturn(paymentInstructionMock);
        PaymentInstruction updatedPaymentInstruction = paymentInstructionService.submitPaymentInstruction(1, pir);
        verify(paymentInstructionRepository, times(1)).findById(anyInt());
        verify(paymentInstructionRepository, times(1)).saveAndRefresh(paymentInstructionMock);

    }
    @Test
    public void shouldReturnSubmittedPaymentInstructionWithAction_whenSubmitPaymentInstructionForGivenPaymentInstructionIsCalledWithAction()
        throws Exception {
        when(ff4jMock.check(PaymentActionEnum.SUSPENSE.featureKey())).thenReturn(true);
        PaymentInstructionUpdateRequest pir = PaymentInstructionUpdateRequest.paymentInstructionUpdateRequestWith()
            .status("D").action("Suspense").build();
        when(paymentInstructionRepository.findById(anyInt())).thenReturn(Optional.of(paymentInstructionMock));
        when(paymentInstructionRepository.saveAndRefresh(any(PaymentInstruction.class)))
            .thenReturn(paymentInstructionMock);
        PaymentInstruction updatedPaymentInstruction = paymentInstructionService.submitPaymentInstruction(1, pir);
        verify(paymentInstructionRepository, times(1)).findById(anyInt());
        verify(paymentInstructionRepository, times(1)).saveAndRefresh(paymentInstructionMock);

        PaymentInstructionUpdateRequest anotherPir = PaymentInstructionUpdateRequest.paymentInstructionUpdateRequestWith()
            .status("D").action("Process").build();
        try {
            paymentInstructionService.submitPaymentInstruction(1, anotherPir);
            fail("should fail here");
        } catch (FeatureAccessException fae){
            assertEquals("Process is not allowed", fae.getMessage());
        }
    }

    @Test
    public void shouldRefuseSubmittedPaymentInstructionWithAction_whenSubmitPaymentInstructionForGivenPaymentInstructionIsCalledWithAction() {
        when(ff4jMock.check(anyString())).thenReturn(false);
        when(paymentInstructionRepository.findById(anyInt())).thenReturn(Optional.of(paymentInstructionMock));
        when(paymentInstructionRepository.saveAndRefresh(any(PaymentInstruction.class)))
            .thenReturn(paymentInstructionMock);
        try {
            PaymentInstructionUpdateRequest pir = PaymentInstructionUpdateRequest.paymentInstructionUpdateRequestWith()
                .status("D").action("Suspense").build();
            PaymentInstruction updatedPaymentInstruction = paymentInstructionService.submitPaymentInstruction(1, pir);
            fail("should fail here");
        } catch (FeatureAccessException fae){
            assertEquals("Suspense is not allowed", fae.getMessage());
        }
    }
    @Test
    public void shouldReturn200_whenUpdatePaymentInstructionForGivenPaymentInstructionIsCalled()
        throws Exception {
        PaymentInstructionRequest pir = PaymentInstructionRequest.paymentInstructionRequestWith()
            .amount(200)
            .payerName("Payer Name")
            .currency("GBP").build();
        when(paymentInstructionRepository.findById(anyInt())).thenReturn(Optional.of(paymentInstructionMock));
        when(paymentInstructionRepository.saveAndRefresh(any(PaymentInstruction.class)))
            .thenReturn(paymentInstructionMock);
        when(paymentInstructionMock.getStatus()).thenReturn("status");
        PaymentInstruction updatedPaymentInstruction = paymentInstructionService.updatePaymentInstruction(1, pir);
        verify(paymentInstructionRepository, times(1)).findById(anyInt());
        verify(paymentInstructionRepository, times(1)).saveAndRefresh(paymentInstructionMock);

    }

    @Test
    public void shouldReturn200andBgcUpdated_whenUpdatePostalInstructionWithBGCForGivenPaymentInstructionIsCalled() {
        PaymentInstruction pi = new PostalOrderPaymentInstruction();
        PaymentInstructionRequest pir = PostalOrder.postalOrderPaymentInstructionRequestWith()
            .amount(200)
            .payerName("Payer Name")
            .currency("GBP")
            .bgcNumber("12345").build();
        when(paymentInstructionRepository.findById(anyInt())).thenReturn(Optional.of(pi));
        when(paymentInstructionRepository.saveAndRefresh(any(PaymentInstruction.class)))
            .thenAnswer(i -> i.getArguments()[0]);
        when(bankGiroCreditRepositoryMock.save(any(BankGiroCredit.class))).thenAnswer(i -> i.getArguments()[0]);
        // when(paymentInstructionMock.getStatus()).thenReturn("status");
        PaymentInstruction updatedPaymentInstruction = paymentInstructionService.updatePaymentInstruction(1, pir);
        assertEquals(pir.getBgcNumber(), updatedPaymentInstruction.getBgcNumber());
        verify(paymentInstructionRepository, times(1)).findById(anyInt());
        verify(paymentInstructionRepository, times(1)).saveAndRefresh(pi);

    }

    @Test
    public void shouldReturn200andBgcNotUpdated_whenUpdateCardInstructionWithBGCForGivenPaymentInstructionIsCalled(){
        PaymentInstruction pi = new CardPaymentInstruction();
        PaymentInstructionRequest pir = PostalOrder.postalOrderPaymentInstructionRequestWith()
            .amount(200)
            .payerName("Payer Name")
            .currency("GBP")
            .bgcNumber("12345").build();
        when(paymentInstructionRepository.findById(anyInt())).thenReturn(Optional.of(pi));
        when(paymentInstructionRepository.saveAndRefresh(any(PaymentInstruction.class)))
            .thenAnswer(i -> i.getArguments()[0]);
        when(bankGiroCreditRepositoryMock.save(any(BankGiroCredit.class))).thenAnswer(i -> i.getArguments()[0]);
        // when(paymentInstructionMock.getStatus()).thenReturn("status");
        PaymentInstruction updatedPaymentInstruction = paymentInstructionService.updatePaymentInstruction(1, pir);
        assertNull(updatedPaymentInstruction.getBgcNumber());
        verify(paymentInstructionRepository, times(1)).findById(anyInt());
        verify(paymentInstructionRepository, times(1)).saveAndRefresh(pi);
    }

    @Test
    public void shouldReturn200_whenUpdatePaymentInstructionOverviewIsCalled()
        throws Exception {
        when(paymentInstructionStatusRepositoryMock.getPaymentOverviewStats(anyString())).thenReturn(new ArrayList<PaymentInstructionOverview>());
        when(barUserServiceMock.getBarUser()).thenReturn(barUserMock);
        when(barUserMock.getRoles()).thenReturn("bar-senior-clerk");
        Map<String, MultiMap> combinedPaymentInstructionOverviewMap = paymentInstructionService.getPaymentInstructionStats("", "");
        verify(paymentInstructionStatusRepositoryMock, times(1)).getPaymentOverviewStats(anyString());
        verify(paymentInstructionStatusRepositoryMock, times(1)).getPaymentInstructionsByStatusByUserGroup(anyString(), anyString());
        verify(paymentInstructionStatusRepositoryMock, times(1)).getPaymentInstructionsRejectedByDM(anyString());
    }
    
    @Test
    public void shouldReturn200_whenUpdatePaymentInstructionOverviewIsCalledByFeeClerk()
        throws Exception {
        when(paymentInstructionStatusRepositoryMock.getPaymentOverviewStats(anyString())).thenReturn(new ArrayList<PaymentInstructionOverview>());
        when(barUserServiceMock.getBarUser()).thenReturn(barUserMock);
        when(barUserMock.getRoles()).thenReturn("bar-fee-clerk");
        Map<String, MultiMap> combinedPaymentInstructionOverviewMap = paymentInstructionService.getPaymentInstructionStats("", "");
        verify(paymentInstructionStatusRepositoryMock, times(1)).getPaymentOverviewStats(anyString());
        verify(paymentInstructionStatusRepositoryMock, times(1)).getPaymentInstructionsByStatusByUserGroup(anyString(), anyString());
        verify(paymentInstructionStatusRepositoryMock, times(0)).getPaymentInstructionsRejectedByDM(anyString());
    }


    @Test
    public void shouldReturnEmptyPaymentInstructionList_whengetAllPaymentInstructionsByTTBWithIncorrectDates()
        throws Exception {

        List<PaymentInstruction> paymentInstructionList = paymentInstructionService.getAllPaymentInstructionsByTTB(LocalDate.now(), LocalDate.now().minusDays(1));
        assertTrue(paymentInstructionList.isEmpty());
    }

}

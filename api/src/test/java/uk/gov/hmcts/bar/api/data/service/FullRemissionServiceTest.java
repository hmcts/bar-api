package uk.gov.hmcts.bar.api.data.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.bar.api.audit.AuditRepository;
import uk.gov.hmcts.bar.api.data.model.BarUser;
import uk.gov.hmcts.bar.api.data.model.FullRemission;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionRepository;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionStatusRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class FullRemissionServiceTest {

    @InjectMocks
    private FullRemissionService fullRemissionServiceMock;

    @Mock
    private PaymentInstructionRepository paymentInstructionRepository;

    @Mock
    private PaymentInstruction paymentInstructionMock;

    @Mock
    private BarUserService barUserServiceMock;

    @Mock
    private BarUser barUserMock;

    @Mock
    private PaymentInstructionStatusRepository paymentInstructionStatusRepositoryMock;

    @Mock
    private AuditRepository auditRepository;

    private FullRemissionService fullRemissionService;

    @Before
    public void setupMock() {
        MockitoAnnotations.initMocks(this);
        fullRemissionService = new FullRemissionService(paymentInstructionRepository,
            barUserServiceMock,paymentInstructionStatusRepositoryMock,auditRepository);
    }


    @Test
    public void shouldReturn200_whenUpdatePaymentInstructionForGivenPaymentInstructionIsCalled()
        throws Exception {
        FullRemission fullRemission = FullRemission.fullRemissionWith()
            .payerName("Payer Name")
            .status("P").build();
        when(barUserServiceMock.getBarUser()).thenReturn(Optional.of(barUserMock));
        when(paymentInstructionRepository.findById(anyInt())).thenReturn(Optional.of(paymentInstructionMock));
        when(paymentInstructionRepository.saveAndRefresh(any(PaymentInstruction.class)))
            .thenReturn(paymentInstructionMock);
        PaymentInstruction updatedPaymentInstruction = fullRemissionService.updateFullRemission(1,fullRemission);
        verify(paymentInstructionRepository, times(1)).findById(anyInt());
        verify(paymentInstructionRepository, times(1)).saveAndRefresh(paymentInstructionMock);
        verify(auditRepository,times(1)).trackPaymentInstructionEvent("FULL_REMISSION_PI_UPDATE_EVENT",paymentInstructionMock,barUserMock);

    }

}

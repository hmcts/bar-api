package uk.gov.hmcts.bar.api.data.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.bar.api.audit.AuditRepository;
import uk.gov.hmcts.bar.api.data.TestUtils;
import uk.gov.hmcts.bar.api.data.exceptions.PaymentInstructionNotFoundException;
import uk.gov.hmcts.bar.api.data.model.BarUser;
import uk.gov.hmcts.bar.api.data.model.CaseFeeDetail;
import uk.gov.hmcts.bar.api.data.model.CaseFeeDetailRequest;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.repository.CaseFeeDetailRepository;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class CaseFeeDetailServiceTest {
    @Mock
    private PaymentInstructionRepository paymentInstructionRepository;
    @Mock
    private CaseFeeDetailRepository caseFeeDetailRepository;
    @Mock
    private BarUserService barUserService;
    @Mock
    private AuditRepository auditRepository;
    @Mock
    private BarUser barUserMock;
    @Mock
    private CaseFeeDetailRequest caseFeeDetailRequest;

    private CaseFeeDetailService caseFeeDetailService;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        caseFeeDetailService = new CaseFeeDetailService(paymentInstructionRepository,caseFeeDetailRepository, auditRepository);
        when(barUserMock.getSelectedSiteId()).thenReturn("Y431");
    }

    @Test
    public void testCreateCaseFeeDetail() {
        PaymentInstruction pi = TestUtils.createPaymentInstructions("CASH",10000);
        when(paymentInstructionRepository.findByIdAndSiteId(anyInt(), eq("Y431"))).thenReturn(Optional.of(pi));
        CaseFeeDetailRequest request = CaseFeeDetailRequest.caseFeeDetailRequestWith()
            .caseReference("123456")
            .amount(55000)
            .feeCode("X012")
            .feeVersion("1")
            .paymentInstructionId(1)
            .build();
        caseFeeDetailService.saveCaseFeeDetail(barUserMock, request);
        verify(caseFeeDetailRepository, times(1))
            .saveAndRefresh(any(CaseFeeDetail.class));
    }


    @Test
    public void testUpdateCaseFeeDetail() {
        PaymentInstruction pi = TestUtils.createPaymentInstructions("CASH",10000);
        when(paymentInstructionRepository.findByIdAndSiteId(anyInt(), eq("Y431"))).thenReturn(Optional.of(pi));
        when(caseFeeDetailRepository.findById(anyInt())).thenReturn(Optional.of(new CaseFeeDetail()));
        CaseFeeDetailRequest request = CaseFeeDetailRequest.caseFeeDetailRequestWith()
            .caseReference("123456")
            .amount(55000)
            .feeCode("X012")
            .feeVersion("1")
            .paymentInstructionId(1)
            .build();
        caseFeeDetailService.updateCaseFeeDetail(barUserMock,1, request);
        verify(caseFeeDetailRepository, times(1))
            .findById(1);
        verify(caseFeeDetailRepository, times(1))
            .saveAndRefresh(any(CaseFeeDetail.class));
    }

    @Test(expected = PaymentInstructionNotFoundException.class)
    public void shouldThrowException_whenPaymentInstructionIsNotFoundInCreate() {
        when(paymentInstructionRepository.findByIdAndSiteId(anyInt(), eq("Y431"))).thenReturn(Optional.empty());
        CaseFeeDetailRequest request = CaseFeeDetailRequest.caseFeeDetailRequestWith()
            .caseReference("123456")
            .amount(55000)
            .feeCode("X012")
            .feeVersion("1")
            .paymentInstructionId(1)
            .build();
        caseFeeDetailService.saveCaseFeeDetail(barUserMock, request);
    }

    @Test(expected = PaymentInstructionNotFoundException.class)
    public void shouldThrowException_whenPaymentInstructionIsNotFoundInUpdate() {
        when(paymentInstructionRepository.findByIdAndSiteId(anyInt(), eq("Y431"))).thenReturn(Optional.empty());
        CaseFeeDetailRequest request = CaseFeeDetailRequest.caseFeeDetailRequestWith()
            .caseReference("123456")
            .amount(55000)
            .feeCode("X012")
            .feeVersion("1")
            .paymentInstructionId(1)
            .build();
        caseFeeDetailService.updateCaseFeeDetail(barUserMock, 1,request);
    }


    @Test
    public void testDeleteCaseFeeDetail() {
        caseFeeDetailService.deleteCaseFeeDetail(1);
        verify(caseFeeDetailRepository, times(1))
            .deleteById(1);
    }


}

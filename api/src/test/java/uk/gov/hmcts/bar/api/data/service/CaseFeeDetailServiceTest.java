package uk.gov.hmcts.bar.api.data.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.bar.api.audit.AuditRepository;
import uk.gov.hmcts.bar.api.data.model.CaseFeeDetail;
import uk.gov.hmcts.bar.api.data.model.CaseFeeDetailRequest;
import uk.gov.hmcts.bar.api.data.repository.CaseFeeDetailRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class CaseFeeDetailServiceTest {

    @Mock
    private CaseFeeDetailRepository caseFeeDetailRepository;
    @Mock
    private BarUserService barUserService;
    @Mock
    private AuditRepository auditRepository;

    private CaseFeeDetailService caseFeeDetailService;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        caseFeeDetailService = new CaseFeeDetailService(caseFeeDetailRepository, barUserService, auditRepository);
    }

    @Test
    public void testUpdateCaseFeeDetail() {
        when(caseFeeDetailRepository.findById(anyInt())).thenReturn(Optional.of(new CaseFeeDetail()));
        CaseFeeDetailRequest request = CaseFeeDetailRequest.caseFeeDetailRequestWith()
            .caseReference("123456")
            .amount(55000)
            .feeCode("X012")
            .feeVersion("1")
            .build();
        caseFeeDetailService.updateCaseFeeDetail(1, request);
        verify(caseFeeDetailRepository, times(1))
            .findById(1);
        verify(caseFeeDetailRepository, times(1))
            .saveAndRefresh(any(CaseFeeDetail.class));
    }

    @Test
    public void testDeleteCaseFeeDetail() {
        caseFeeDetailService.deleteCaseFeeDetail(1);
        verify(caseFeeDetailRepository, times(1))
            .deleteById(1);
    }
}

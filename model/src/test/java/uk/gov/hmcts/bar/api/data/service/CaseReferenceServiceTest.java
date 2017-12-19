package uk.gov.hmcts.bar.api.data.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.bar.api.data.model.CaseReference;
import uk.gov.hmcts.bar.api.data.repository.CaseReferenceRepository;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CaseReferenceServiceTest {

    @InjectMocks
    private CaseReferenceService caseReferenceServiceMock;

    @Mock
    private CaseReferenceRepository caseReferenceRepository;

    @Before
    public void setupMock() {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void shouldSaveCaseReference_whenSavecaseReferenceIsCalled() {
        caseReferenceServiceMock.saveCaseReference(anyString());
        verify(caseReferenceRepository, times(1)).saveAndRefresh(any(CaseReference.class));

    }

    @Test
    public void shouldReturnCaseReference_whenGetCaseReferenceIsCalled() {
        caseReferenceServiceMock.getCaseReference(anyString());
        verify(caseReferenceRepository, times(1)).findByCaseReference((anyString()));

    }

}

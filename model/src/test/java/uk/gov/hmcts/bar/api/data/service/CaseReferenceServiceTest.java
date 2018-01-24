package uk.gov.hmcts.bar.api.data.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import uk.gov.hmcts.bar.api.data.model.CaseReference;
import uk.gov.hmcts.bar.api.data.repository.CaseReferenceRepository;

public class CaseReferenceServiceTest {

    @InjectMocks
    private CaseReferenceService caseReferenceServiceMock;

    @Mock
    private CaseReferenceRepository caseReferenceRepository;
    
    @Mock
    private CaseReference cr;

    @Before
    public void setupMock() {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void shouldSaveCaseReference_whenSavecaseReferenceIsCalled() {
        caseReferenceServiceMock.saveCaseReference(cr);
        verify(caseReferenceRepository, times(1)).saveAndRefresh(any(CaseReference.class));

    }

}

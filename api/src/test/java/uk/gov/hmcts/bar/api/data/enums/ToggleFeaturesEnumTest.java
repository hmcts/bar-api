package uk.gov.hmcts.bar.api.data.enums;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ToggleFeaturesEnumTest {

    private static final ToggleFeaturesEnum makePageReadOnly = ToggleFeaturesEnum.MAKEPAGEREADONLY;


    @Test
    public void shouldReturnFeatureKeyValue_whenFeatureKeyMethodIsCalledOnMakePageReadOnlyEnum() {
        assertEquals("make-editpage-readonly", makePageReadOnly.featureKey());
    }

    @Test
    public void shouldReturnDisplayValue_whenDisplayValueMethodIsCalledOnMakePageReadOnlyEnum() {
        assertEquals("Make page read only", makePageReadOnly.displayValue());
    }

}

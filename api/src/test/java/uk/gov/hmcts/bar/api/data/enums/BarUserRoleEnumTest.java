package uk.gov.hmcts.bar.api.data.enums;

import org.junit.Assert;
import org.junit.Test;

public class BarUserRoleEnumTest {

    @Test
    public void testGetProperIdamRole() {
        Assert.assertEquals("bar-delivery-manager", BarUserRoleEnum.BAR_DELIVERY_MANAGER.getIdamRole());
        Assert.assertEquals("bar-senior-clerk", BarUserRoleEnum.BAR_SENIOR_CLERK.getIdamRole());
        Assert.assertEquals("bar-fee-clerk", BarUserRoleEnum.BAR_FEE_CLERK.getIdamRole());
        Assert.assertEquals("bar-post-clerk", BarUserRoleEnum.BAR_POST_CLERK.getIdamRole());
        Assert.assertEquals("super", BarUserRoleEnum.BAR_SUPER_USER.getIdamRole());
    }
}

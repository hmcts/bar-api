package uk.gov.hmcts.bar.api.data.enums;

import lombok.Getter;

public enum BarUserRoleEnum {
    BAR_DELIVERY_MANAGER("bar-delivery-manager"),
    BAR_SENIOR_CLERK("bar-senior_clerk"),
    BAR_FEE_CLERK("bar-fee-clerk"),
    BAR_POST_CLERK("bar-post-clerk"),
    BAR_SUPER_USER("super");


    @Getter
    private String idamRole;

    BarUserRoleEnum(String idamRole) {
        this.idamRole = idamRole;
    }
}

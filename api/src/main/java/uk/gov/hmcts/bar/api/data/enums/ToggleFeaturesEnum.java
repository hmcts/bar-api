package uk.gov.hmcts.bar.api.data.enums;

public enum ToggleFeaturesEnum {

    MAKEPAGEREADONLY("make-editpage-readonly", "Make page read only");

    private String featureKey = null;

    private String displayValue = null;

    ToggleFeaturesEnum(String featureKey, String displayValue) {
        this.featureKey = featureKey;
        this.displayValue = displayValue;
    }

    public String featureKey() {
        return this.featureKey;
    }

    public String displayValue() {
        return this.displayValue;
    }

}

package com.tenforce.lodms.transformers;

public class Mapping {
    private String originalValue;
    private String harmonizedValue;

    public Mapping() {
        this("","http://example.org/me#");
    }
    public Mapping(String originalValue,String harmonizedValue) {
        this.originalValue = originalValue;
        this.harmonizedValue = harmonizedValue;
    }

    public String getHarmonizedValue() {
        return harmonizedValue;
    }
    public void setHarmonizedValue(String harmonizedValue) {
        this.harmonizedValue = harmonizedValue;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(String originalValue) {
        this.originalValue = originalValue;
    }
}

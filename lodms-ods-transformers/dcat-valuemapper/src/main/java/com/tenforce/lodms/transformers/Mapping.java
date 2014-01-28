package com.tenforce.lodms.transformers;

public class Mapping {
  private String originalValue;
  private String harmonizedValue;
  private boolean validMapping = true;

  public Mapping() {
    this("", "http://example.org/me#");
  }

  public Mapping(String originalValue, String harmonizedValue) {
    this.originalValue = originalValue;
    this.harmonizedValue = harmonizedValue;
  }

  public String getHarmonizedValue() {
    return harmonizedValue;
  }

  public void setHarmonizedValue(String harmonizedValue) {
    this.harmonizedValue = harmonizedValue.trim();
  }

  public String getOriginalValue() {
    return originalValue;
  }

  public void setOriginalValue(String originalValue) {
    this.originalValue = originalValue;
  }

  public boolean isValidMapping() {
    return validMapping;
  }

  public void setValidMapping(boolean validMapping) {
    this.validMapping = validMapping;
  }
}

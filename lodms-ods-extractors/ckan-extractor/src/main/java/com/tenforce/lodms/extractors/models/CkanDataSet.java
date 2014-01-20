package com.tenforce.lodms.extractors.models;

import java.util.HashMap;

public class CkanDataSet extends JSONModel {
  private HashMap result;

  public HashMap getResult() {
    return result;
  }

  public void setResult(HashMap result) {
    this.result = result;
  }
}

package com.tenforce.lodms.transformers.translators;

public class TranslationException extends Exception {
  public TranslationException(Exception e) {
    super(e);
  }

  public TranslationException(String message, Exception e) {
    super(message, e);
  }

  public TranslationException(String message) {
    super(message);

  }

}

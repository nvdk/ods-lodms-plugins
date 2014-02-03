package com.tenforce.lodms.transformers;

import com.tenforce.lodms.transformers.translators.bing.BingTranslator;
import com.tenforce.lodms.transformers.translators.TranslationApi;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.DCTERMS;

import java.util.Arrays;
import java.util.List;

public class TranslatorConfig {
  public static final TranslationApi DEFAULT_PROVIDER = new BingTranslator();
  public static final List<URI> DEFAULT_PREDICATES = Arrays.asList(DCTERMS.TITLE,DCTERMS.DESCRIPTION);
  private TranslationApi provider = DEFAULT_PROVIDER;
  private String providerClientID = "";
  private String providerClientSecret = "";

  private List<URI> predicates = DEFAULT_PREDICATES;

  public List<URI> getPredicates() {
    return predicates;
  }

  public void setPredicates(List<URI> predicates) {
    this.predicates = predicates;
  }

  public TranslationApi getProvider() {
    return provider;
  }

  public String getProviderClientSecret() {
    return providerClientSecret;
  }

  public void setProviderClientSecret(String providerClientSecret) {
    this.providerClientSecret = providerClientSecret;
  }

  public String getProviderClientID() {
    return providerClientID;
  }

  public void setProviderClientID(String providerClientID) {
    this.providerClientID = providerClientID;
  }
}

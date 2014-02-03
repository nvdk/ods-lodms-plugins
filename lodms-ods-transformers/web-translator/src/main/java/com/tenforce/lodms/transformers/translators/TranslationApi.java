package com.tenforce.lodms.transformers.translators;

import org.openrdf.model.Statement;

import java.util.Collection;

public interface TranslationApi {

  public void setClientId(String id);
  public void setClientSecret(String secret);
  public Collection<TranslatedStatement> translateStatements(Collection<Statement> statements);

}

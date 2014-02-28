package com.tenforce.lodms.transformers.translators;

import org.openrdf.model.Statement;

import java.util.Collection;
import java.util.List;

public interface TranslationApi {

  public void setClientId(String id);

  public void setClientSecret(String secret);

  public List<String> getWarnings();

  public Collection<TranslatedStatement> translateStatements(Collection<Statement> statements) throws TranslationException;

}

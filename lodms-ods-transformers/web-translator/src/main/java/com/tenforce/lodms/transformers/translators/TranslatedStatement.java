package com.tenforce.lodms.transformers.translators;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;

public class TranslatedStatement {
  private Statement originalStatement;
  private Statement translatedStatement;
  private String orgLanguage;

  public Statement getOriginalStatement() {
    return originalStatement;
  }

  public void setOriginalStatement(Statement originalStatement) {
    if (originalStatement.getObject() instanceof Literal) {
      this.originalStatement = originalStatement;
      this.orgLanguage = ((Literal) originalStatement.getObject()).getLanguage();
    }
  }

  public Statement getTranslatedStatement() {
    return translatedStatement;
  }

  public void setTranslatedStatement(Statement translatedStatement) {
    this.translatedStatement = translatedStatement;
  }
}

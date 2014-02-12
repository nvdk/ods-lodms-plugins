package com.tenforce.lodms.transformers;

import at.punkt.lodms.base.TransformerBase;
import at.punkt.lodms.integration.ConfigDialog;
import at.punkt.lodms.integration.ConfigDialogProvider;
import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.spi.transform.TransformContext;
import at.punkt.lodms.spi.transform.TransformException;
import com.google.common.base.Joiner;
import com.tenforce.lodms.transformers.translators.TranslatedStatement;
import com.tenforce.lodms.transformers.translators.TranslationApi;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WebTranslator extends TransformerBase<TranslatorConfig> implements ConfigDialogProvider<TranslatorConfig> {

  @Override
  public TranslatorConfig newDefaultConfig() {
    return new TranslatorConfig();
  }

  @Override
  protected void configureInternal(TranslatorConfig config) throws ConfigurationException {
  }

  @Override
  public void transform(Repository repository, URI graph, TransformContext context) throws TransformException {
    try {
      TranslationApi api = config.getProvider();
      api.setClientId(config.getProviderClientID());
      api.setClientSecret(config.getProviderClientSecret());
      Collection<TranslatedStatement> translatedStatements = api.translateStatements(getStatementsToTranslate(repository, graph));
      insertTranslatedStatements(repository, graph, translatedStatements);
    } catch (Exception e) {
      throw new TransformException(e);
    }
  }

  private void insertTranslatedStatements(Repository repository, URI graph, Collection<TranslatedStatement> translatedStatements) throws RepositoryException {
    RepositoryConnection con = repository.getConnection();
    try {
      for (TranslatedStatement s : translatedStatements) {
        con.remove(s.getOriginalStatement(), graph);
        con.add(s.getTranslatedStatement(), graph);
      }
    } finally {
      con.close();
    }
  }

  private Collection<Statement> getStatementsToTranslate(Repository repository, URI graph) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
    String queryString = "SELECT ?s ?p ?o " +
            "FROM <" + graph + "> " +
            "WHERE {" +
            queryForPredicates(config.getPredicates())
            +
            "}";
    RepositoryConnection con = repository.getConnection();
    Model statements = new LinkedHashModel();
    try {
      TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
      TupleQueryResult results = tupleQuery.evaluate();

      while (results.hasNext()) {
        BindingSet row = results.next();
        statements.add((URI) row.getValue("s"), (URI) row.getValue("p"), row.getValue("o"));
      }
    } finally {
      con.close();
    }

    return statements;
  }

  private String queryForPredicates(List<URI> predicates) {
    String format = "{" +
            "?s <[pred]> ?o." +
            "BIND (<[pred]> AS ?p)." +
            "FILTER (lang(?o) != 'en')" +
            "}";
    List<String> queries = new ArrayList<String>(predicates.size());
    for (URI pred : predicates) {
      queries.add(format.replace("[pred]", pred.stringValue()));
    }
    return Joiner.on(" UNION ").join(queries);
  }

  @Override
  public String getName() {
    return "Web Translations";
  }

  @Override
  public String getDescription() {
    return "Inserts automated translations for a list of literals using the configured service";
  }

  @Override
  public Resource getIcon(Application application) {
    return new ClassResource("/ods.png", application);
  }

  @Override
  public String asString() {
    return this.getName() + " " + config.getProvider().toString();
  }

  @Override
  public ConfigDialog getConfigDialog(TranslatorConfig config) {
    return new TranslatorDialog(config);
  }
}

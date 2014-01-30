package com.tenforce.lodms.transformers.validator;

import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;

import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ValidationExecutor {
  private RepositoryConnection connection;
  private URI context;
  private List<String> warnings = new ArrayList<String>();
  private ValidationLogWriter validationLogger;

  public ValidationExecutor(ValidationLogWriter logWriter, RepositoryConnection connection, URI context) {
    this.connection = connection;
    this.context = context;
    validationLogger = logWriter;
    validationLogger.info("configured for context: " + context.stringValue());
    String overview = getOverview();
    validationLogger.info(overview);
    warnings.add(overview);
  }

  public void validate(ValidationRule rule) throws TransformerException {
    try {
      String queryString = String.format("define input:default-graph-uri <%s> %s", context.stringValue(), rule.getSparqlQuery());
      TupleQuery query = connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
      TupleQueryResult result = query.evaluate();
      if (!result.getBindingNames().containsAll(Arrays.asList("s", "p", "o"))) {
        warnings.add(rule.getDescription() + " does not bind all required names ?s, ?p,?o");
        return;
      }

      int i = 0;
      while (result.hasNext()) {
        createWarning(rule.getSeverity(), rule.getMessage(), result.next());
        i++;
      }
      if (i > 0) {
        addWarningSummary(i, rule.getSeverity(), rule.getDescription());
      }
    } catch (Exception e) {
      throw new TransformerException(e.getMessage(), e);
    }
  }

  private void addWarningSummary(int i, String severity, String message) {
    String plural;
    if (i > 1)
      severity = severity + "s";
    warnings.add(message + ": " + i + " " + severity);
  }

  public List<String> getWarnings() {
    return warnings;
  }

  private String getOverview() {
    String rawQuery = "PREFIX dcterms: <http://purl.org/dc/terms/>\n" +
            "PREFIX dcat:<http://www.w3.org/ns/dcat#>\n" +
            "PREFIX foaf:<http://xmlns.com/foaf/0.1/>\n" +
            "SELECT (COUNT(?d) AS ?ds) (COUNT(?di) AS ?dist) (COUNT(?a) AS ?agent)\n" +
            "WHERE {\n" +
            "{?d a dcat:Dataset.}\n" +
            "UNION\n" +
            "{?di a dcat:Distribution}\n" +
            "UNION\n" +
            "{?a a foaf:Agent}\n" +
            "}";
    String queryString = String.format("define input:default-graph-uri <%s> %s", context.stringValue(), rawQuery);
    try {
      TupleQuery query = connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
      TupleQueryResult result = query.evaluate();
      BindingSet set = result.next();
      result.close();
      return String.format("Validated \n %s datasets \n %s distributions \n %s agents", set.getValue("ds").stringValue(), set.getValue("dist").stringValue(), set.getValue("agent").stringValue());

    } catch (Exception e) {
      return "Failed to retrieve class counts";
    }
  }

  private void createWarning(String severity, String message, BindingSet row) {
    String s = row.getValue("s").stringValue();
    String p = row.getValue("p").stringValue();
    String o = row.getValue("o").stringValue();
    String warning = "%s\n \t{%s %s %s}";
    validationLogger.write(severity, String.format(warning, message, s, p, o));
  }
}

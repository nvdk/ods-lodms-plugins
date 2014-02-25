package com.tenforce.lodms.transformers;

import at.punkt.lodms.base.TransformerBase;
import at.punkt.lodms.integration.ConfigDialog;
import at.punkt.lodms.integration.ConfigDialogProvider;
import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.spi.transform.TransformContext;
import at.punkt.lodms.spi.transform.TransformException;
import com.tenforce.lodms.ODSVoc;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import org.openrdf.model.URI;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public class ODSValueMapper extends TransformerBase<ODSValueMapperConfig> implements ConfigDialogProvider<ODSValueMapperConfig> {

  @Override
  public ConfigDialog getConfigDialog(ODSValueMapperConfig config) {
    return new ODSValueMapperDialog(config);
  }

  @Override
  public ODSValueMapperConfig newDefaultConfig() {
    return new ODSValueMapperConfig();
  }

  @Override
  protected void configureInternal(ODSValueMapperConfig config) throws ConfigurationException {
  }

  @Override
  public void transform(Repository repository, URI graph, TransformContext context) throws TransformException {
    try {
      RepositoryConnection con = repository.getConnection();
      try {
        // perform inserts first to allow n to n mapping
        for (Mapping mapping : config.getMappings()) {
          insertMappings(graph, con, mapping);
        }
        for (Mapping mapping : config.getMappings()) {
          removeOriginals(graph, con, mapping);
        }
      } catch (UpdateExecutionException e) {
        context.getWarnings().add(e.getMessage());
      } finally {
        con.close();
      }
    } catch (Exception e) {
      throw new TransformException(e.getMessage(), e);
    }
  }

  private void removeOriginals(URI graph, RepositoryConnection con, Mapping mapping) throws RepositoryException, MalformedQueryException, UpdateExecutionException {
    String deleteString;
    if (mapping.getOriginalValue().startsWith("http")) {
      deleteString = generateURIDeleteString(graph.stringValue(), mapping.getOriginalValue());
    } else {
      deleteString = generateDeleteString(graph.stringValue(), mapping.getOriginalValue());
    }
    Update deleteQuery = con.prepareUpdate(QueryLanguage.SPARQL, deleteString);
    deleteQuery.execute();
    con.commit();
  }

  private void insertMappings(URI graph, RepositoryConnection con, Mapping mapping) throws RepositoryException, MalformedQueryException, UpdateExecutionException {
    String insertString;
    if (!mapping.isValidMapping()) {
      insertString = generateOtherInsertString(graph.stringValue(), mapping.getOriginalValue());
    } else if (mapping.getOriginalValue().startsWith("http")) {
      insertString = generateURIInsertString(graph.stringValue(), mapping.getOriginalValue(), mapping.getHarmonizedValue());
    } else {
      insertString = generateInsertString(graph.stringValue(), mapping.getOriginalValue(), mapping.getHarmonizedValue());
    }
    Update insertQuery = con.prepareUpdate(QueryLanguage.SPARQL, insertString);
    insertQuery.execute();
    con.commit();
  }

  private String generateOtherInsertString(String graph, String originalValue) {
    String predicate = config.getMappedPredicate().getDcatProp().stringValue();
    String dcatClass = config.getMappedPredicate().getDcatClass().stringValue();
    if (originalValue.startsWith("http")) {
      String query = "WITH <%s> INSERT { ?s <%s> [a <%s>;<%s> %s]} WHERE {?s a <%s>.  {{?s <%s> \"%s\"} UNION {?s <%s> <%s>}} }";
      return String.format(query, graph, predicate, ODSVoc.ODS_OTHER_VALUE, ODSVoc.ODS_ORIGINAL_VALUE, originalValue, dcatClass, predicate, originalValue, predicate, originalValue);
    } else {
      String query = "WITH <%s> INSERT { ?s <%s> [a <%s>;<%s> %s]} WHERE {?s a <%s>.  {?s <%s> \"%s\" }";
      return String.format(query, graph, predicate, ODSVoc.ODS_OTHER_VALUE, ODSVoc.ODS_ORIGINAL_VALUE, originalValue, dcatClass, predicate, originalValue);
    }
  }

  private String generateURIInsertString(String graph, String originalValue, String newValue) {
    String predicate = config.getMappedPredicate().getDcatProp().stringValue();
    String dcatClass = config.getMappedPredicate().getDcatClass().stringValue();
    String query = "WITH <%s> INSERT { ?s <%s> <%s> } WHERE {?s a <%s>.  {{?s <%s> \"%s\"} UNION {?s <%s> <%s>}} }";
    return String.format(query, graph, predicate, newValue, dcatClass, predicate, originalValue, predicate, originalValue);
  }

  private String generateInsertString(String graph, String originalValue, String newValue) {
    String predicate = config.getMappedPredicate().getDcatProp().stringValue();
    String dcatClass = config.getMappedPredicate().getDcatClass().stringValue();
    String query = "WITH <%s> INSERT { ?s <%s> <%s> } WHERE {?s a <%s>.  ?s <%s> \"%s\"}";
    return String.format(query, graph, predicate, newValue, dcatClass, predicate, originalValue);
  }

  private String generateDeleteString(String graph, String originalValue) {
    String predicate = config.getMappedPredicate().getDcatProp().stringValue();
    String dcatClass = config.getMappedPredicate().getDcatClass().stringValue();
    String query = "WITH <%s> DELETE { ?s <%s> \"%s\".} WHERE {?s a <%s>.}";
    return String.format(query, graph, predicate, originalValue, dcatClass);
  }

  private String generateURIDeleteString(String graph, String originalValue) {
    String predicate = config.getMappedPredicate().getDcatProp().stringValue();
    String dcatClass = config.getMappedPredicate().getDcatClass().stringValue();
    String query = "WITH <%s> DELETE { ?s <%s> \"%s\". ?s <%s> <%s>  } WHERE {?s a <%s>.}";
    return String.format(query, graph, predicate, originalValue, predicate, originalValue, dcatClass);
  }

  @Override
  public String getName() {
    return "ODS Value Mapper";
  }

  @Override
  public String getDescription() {
    return "Use this plugin to create a value mapping to one of the controlled vocabularies specified in the DCAT profile.";
  }

  @Override
  public Resource getIcon(Application application) {
    return new ClassResource("/ods.png", application);
  }

  @Override
  public String asString() {
    return getName() + " for " + config.getMappedPredicate().getDcatDesc();
  }
}

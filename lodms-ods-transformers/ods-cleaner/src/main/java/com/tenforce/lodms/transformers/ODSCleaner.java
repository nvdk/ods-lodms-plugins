package com.tenforce.lodms.transformers;

import at.punkt.lodms.base.TransformerBase;
import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.spi.transform.TransformContext;
import at.punkt.lodms.spi.transform.TransformException;
import com.tenforce.lodms.ODSVoc;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import org.openrdf.model.URI;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public class ODSCleaner extends TransformerBase<Object> {
  @Override
  public void transform(Repository repository, URI graph, TransformContext context) throws TransformException {
    try {
      RepositoryConnection con = repository.getConnection();
      try {
        /*
       Creative use of property paths to delete all triples starting in a node
        */
        String deleteQuery = "DEFINE sql:log-enable 3 " +
                "WITH <" + graph + "> " +
                "DELETE {?foo ?bang ?bar } " +
                "WHERE {" +
                "   SELECT distinct(?foo) ?bang ?bar" +
                "   WHERE {" +
                "     ?s <" + ODSVoc.ODS_RAW_CATALOG + "> ?raw." +
                "     ?raw !<http://unexistingURI>* ?foo." +
                "     ?foo ?bang ?bar. " +
                "   }" +
                "}";
        Update q = con.prepareUpdate(QueryLanguage.SPARQL, deleteQuery);
        q.execute();
        con.commit();
      } catch (RepositoryException e) {
        throw new TransformException(e.getMessage(), e);
      } finally {
        con.close();
      }
    } catch (Exception e) {
      throw new TransformException(e.getMessage(), e);
    }
  }

  @Override
  public String getName() {
    return "ODS Cleaner";
  }

  @Override
  public String getDescription() {
    return "Cleans up any raw data present after harmonization. Only works if the virtuoso extractor is also part of the pipeline.";
  }

  @SuppressWarnings("ReturnOfNull")
  @Override
  public Resource getIcon(Application application) {
    return new ClassResource("/ods.png", application);
  }

  @Override
  public String asString() {
    return getName();
  }

  @Override
  protected void configureInternal(Object config) throws ConfigurationException {
  }
}

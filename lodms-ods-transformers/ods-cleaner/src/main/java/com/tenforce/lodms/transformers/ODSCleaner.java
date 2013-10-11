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
    private Repository virtuosoRepository;
    private int BATCH_SIZE = 50000;

    @Override
    public void transform(Repository repository, URI graph, TransformContext context) throws TransformException {
        try {
            RepositoryConnection con = repository.getConnection();
            System.out.println("autocommit: " + con.isAutoCommit());
            // i'm sorry you have to see this
            try {
                Update q = con.prepareUpdate(QueryLanguage.SPARQL, "DEFINE sql:log-enable 3 WITH <" + graph + "> DELETE {?vvvo ?vvvvp ?vvvvo} WHERE  {{?s <" + ODSVoc.ODS_RAW_CATALOG + "> ?v}{?v ?vp ?vo}{?vo ?vvp ?vvo}{?vvo ?vvvp ?vvvo}{?vvvo ?vvvvp ?vvvvo}}");
                q.setBinding("rawCatalogPredicate", ODSVoc.ODS_RAW_CATALOG);
                q.execute();
                q = con.prepareUpdate(QueryLanguage.SPARQL, "DEFINE sql:log-enable 3 WITH <" + graph + "> DELETE {?vvo ?vvvp ?vvvo} WHERE  {{?s ?rawCatalogPredicate ?v}{?v ?vp ?vo}{?vo ?vvp ?vvo}{?vvo ?vvvp ?vvvo}}");
                q.execute();
                q = con.prepareUpdate(QueryLanguage.SPARQL, "DEFINE sql:log-enable 3  WITH <" + graph + "> DELETE {?vo?vvp ?vvo} WHERE  {{?s ?rawCatalogPredicate ?rawCatalog}{?rawCatalog ?p ?o}{?o ?vp ?vo}}");
                q.execute();
                q = con.prepareUpdate(QueryLanguage.SPARQL, "DEFINE sql:log-enable 3  WITH <" + graph + "> DELETE {?v?vp ?vo} WHERE  {{?s ?rawCatalogPredicate ?rawCatalog}{?rawCatalog ?p ?o}}");
                q.execute();
                con.commit();
            } catch (RepositoryException e) {
                throw new TransformException(e.getMessage(), e);
            } finally {
                con.rollback();
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
        return new ClassResource("/com/tenforce/lodms/transform/ods.png", application);
    }

    @Override
    public String asString() {
        return getName();
    }

    @Override
    protected void configureInternal(Object config) throws ConfigurationException {
    }
}

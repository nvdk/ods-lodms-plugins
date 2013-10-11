package com.tenforce.lodms.transformers;

import at.punkt.lodms.base.TransformerBase;
import at.punkt.lodms.integration.ConfigDialog;
import at.punkt.lodms.integration.ConfigDialogProvider;
import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.spi.transform.TransformContext;
import at.punkt.lodms.spi.transform.TransformException;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;

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

                for (Mapping mapping : config.getMappings()) {
                    Literal orgValue = ValueFactoryImpl.getInstance().createLiteral(mapping.getOriginalValue());
                    String insertString ="WITH <"+ graph.stringValue() +"> INSERT {?s <" + config.getMappedPredicate().getDcatProp() + "> <"+ mapping.getHarmonizedValue() +">} WHERE {?s a ?klass. ?s <" + config.getMappedPredicate().getDcatProp() + ">  ?orgValue}";
                    Update insertQuery =  con.prepareUpdate(QueryLanguage.SPARQL,insertString);
                    insertQuery.setBinding("orgValue",orgValue);
                    insertQuery.setBinding("klass",config.getMappedPredicate().getDcatClass());
                    insertQuery.execute();
                    con.commit();
                    String deleteString = "WITH <"+ graph.stringValue() +"> DELETE {?s <" + config.getMappedPredicate().getDcatProp() + ">  " + orgValue + "} WHERE {?s a ?klass. ?s <" + config.getMappedPredicate().getDcatProp() + ">  "+ orgValue + " }";
                    Update deleteQuery =  con.prepareUpdate(QueryLanguage.SPARQL,deleteString);
                    deleteQuery.execute();
                }
            }
            catch (UpdateExecutionException e) {
                context.getWarnings().add(e.getMessage());
            }
            finally {
                con.close();
            }
        }
        catch (Exception e) {
            throw new TransformException(e.getMessage(),e);
        }
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

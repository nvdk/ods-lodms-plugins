package com.tenforce.lodms.loaders;

import at.punkt.lodms.integration.ConfigDialog;
import at.punkt.lodms.integration.ConfigDialogProvider;
import at.punkt.lodms.integration.ConfigurableBase;
import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.integration.UIComponent;
import at.punkt.lodms.spi.load.LoadContext;
import at.punkt.lodms.spi.load.LoadException;
import at.punkt.lodms.spi.load.Loader;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import org.apache.log4j.Logger;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public class VirtuosoLoader extends ConfigurableBase<VirtuosoLoaderConfig> implements Loader, UIComponent, ConfigDialogProvider<VirtuosoLoaderConfig> {
    protected Logger logger = Logger.getLogger(VirtuosoLoader.class);

    @Override
    public void load(Repository rpstr, URI uri, LoadContext lc) throws LoadException {
        String graph = config.getGraph();
        if (VirtuosoLoaderConfig.GRAPHSOURCE_CKANURI.equals(config.getGraphSource())) {
            graph = (String) lc.getCustomData().get("ckanExtractBaseUri");
        } else if (VirtuosoLoaderConfig.GRAPHSOURCE_ODSURI.equals(config.getGraphSource())) {
            graph = (String) lc.getCustomData().get("dcatTransformerGraph");
        }
        if (graph == null || graph.isEmpty())
            throw new LoadException("Graph URI can not be empty");

        try {
            RepositoryConnection connection = rpstr.getConnection();
            try {
                URI destinationGraph = new URIImpl(graph);
                if (config.isVersioned())
                    copyGraph(connection, destinationGraph, getBackupGraph(destinationGraph), false);
                copyGraph(connection, uri, destinationGraph, true);
            } finally {
                connection.close();
            }

        } catch (RepositoryException e) {
            throw new LoadException(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new LoadException(e.getMessage(), e);
        }
    }

    private URI getBackupGraph(URI graph) throws LoadException {
        return new URIImpl(graph.stringValue() + "previous");
    }

    private void copyGraph(RepositoryConnection connection, URI orgGraph, URI destGraph, boolean useCopy) throws LoadException {
        try {
            String action = useCopy ? "COPY" : "MOVE";
            String query = "define sql:log-enable 3 " + action + " <" + orgGraph + "> TO <" + destGraph + ">";
            connection.prepareGraphQuery(QueryLanguage.SPARQL, query).evaluate();
            connection.commit();
            connection.close();
        } catch (RepositoryException e) {
            throw new LoadException(e.getMessage(), e);
        } catch (QueryEvaluationException e) {
            logger.error(e.getMessage());
        } catch (MalformedQueryException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "Virtuoso Loader";
    }

    @Override
    public String getDescription() {
        return "Stores RDF statements in a virtuoso database. Please note that the specified graph is cleared before inserting triples.";
    }

    @Override
    public Resource getIcon(Application application) {
        return new ClassResource("/virt.png", application);
    }

    @Override
    public String asString() {
        return getName();
    }

    @Override
    protected void configureInternal(VirtuosoLoaderConfig config) throws ConfigurationException {

    }

    @Override
    public VirtuosoLoaderConfig newDefaultConfig() {
        return new VirtuosoLoaderConfig();
    }

    @Override
    public ConfigDialog getConfigDialog(VirtuosoLoaderConfig config) {
        return new VirtuosoLoaderConfigDialog(config);
    }
}
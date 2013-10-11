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
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import virtuoso.sesame2.driver.VirtuosoRepository;

public class VirtuosoLoader extends ConfigurableBase<VirtuosoLoaderConfig> implements Loader, UIComponent, ConfigDialogProvider<VirtuosoLoaderConfig> {
    protected Logger logger = Logger.getLogger(VirtuosoLoader.class);
    private VirtuosoRepository repository;
    private boolean versioned;
    public static final int BATCH_SIZE = 5000;

    @Override
    public void load(Repository rpstr, URI uri, LoadContext lc) throws LoadException {
        String graph = config.getGraph();
        if (VirtuosoLoaderConfig.GRAPHSOURCE_CKANURI.equals(config.getGraphSource()) && lc.getCustomData().get("ckanExtractBaseUri") != null) {
            graph = (String) lc.getCustomData().get("ckanExtractBaseUri");
        } else if (VirtuosoLoaderConfig.GRAPHSOURCE_ODSURI.equals(config.getGraphSource()) && lc.getCustomData().get("dcatTransformerGraph") != null) {
            graph = (String) lc.getCustomData().get("dcatTransformerGraph");
        }

        try {
            URI graphUri = rpstr.getValueFactory().createURI(graph);
            if (versioned) {
                backupGraph(graph);
            }
            RepositoryConnection virtuosoConnection = repository.getConnection();
            virtuosoConnection.setAutoCommit(false);
            virtuosoConnection.clear(graphUri);
            RepositoryResult<Statement> statements = rpstr.getConnection().getStatements(null, null, null, true, uri);
            int i = 0;
            while (statements.hasNext()) {
                Statement s = statements.next();
                virtuosoConnection.add(s, graphUri);
                if (i++ >= BATCH_SIZE) {
                    virtuosoConnection.commit();
                    i = 0;
                }
            }
            virtuosoConnection.commit();
            virtuosoConnection.close();
        } catch (RepositoryException e) {
            throw new LoadException(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new LoadException(e.getMessage(), e);
        }
    }

    private void backupGraph(String graph) throws LoadException {
        URI backupGraph = ValueFactoryImpl.getInstance().createURI(graph + "previous");
        URI orgGraph = ValueFactoryImpl.getInstance().createURI(graph);
        copyGraph(orgGraph, backupGraph);
    }

    private void copyGraph(URI orgGraph, URI destGraph) throws LoadException {
        RepositoryConnection virtuosoConnection;
        try {
            virtuosoConnection = repository.getConnection();
            String query = "define sql:log-enable 3 COPY <" + orgGraph + "> TO <" + destGraph + ">";
            virtuosoConnection.prepareGraphQuery(QueryLanguage.SPARQL, query).evaluate();
            virtuosoConnection.commit();
            virtuosoConnection.close();
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
        return new ClassResource("/com/tenforce/lodms/virtuoso/virt.png", application);
    }

    @Override
    public String asString() {
        return getName();
    }

    @Override
    protected void configureInternal(VirtuosoLoaderConfig config) throws ConfigurationException {
        String connectionString = "jdbc:virtuoso://" + config.getHost() + ':' + config.getPort();
        versioned = config.isVersioned();
        repository = new VirtuosoRepository(connectionString, config.getUserName(), config.getPassword(), true);

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
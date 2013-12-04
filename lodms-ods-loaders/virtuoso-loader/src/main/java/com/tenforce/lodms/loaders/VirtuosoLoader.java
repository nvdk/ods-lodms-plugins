package com.tenforce.lodms.loaders;

import at.punkt.lodms.integration.ConfigDialog;
import at.punkt.lodms.integration.ConfigDialogProvider;
import at.punkt.lodms.integration.ConfigurableBase;
import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.integration.UIComponent;
import at.punkt.lodms.spi.load.LoadContext;
import at.punkt.lodms.spi.load.LoadException;
import at.punkt.lodms.spi.load.Loader;
import at.punkt.lodms.util.BatchedRdfInserter;
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
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.RDFHandlerException;
import virtuoso.sesame2.driver.VirtuosoRepository;

public class VirtuosoLoader extends ConfigurableBase<VirtuosoLoaderConfig> implements Loader, UIComponent, ConfigDialogProvider<VirtuosoLoaderConfig> {
    protected Logger logger = Logger.getLogger(VirtuosoLoader.class);
    private VirtuosoRepository repository;
    private boolean versioned;
    public static final int BATCH_SIZE = 2000;

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
            RepositoryResult<Statement> statements = rpstr.getConnection().getStatements(null, null, null, true, uri);
            loadwithInserter(statements, graphUri);
            statements.close();
        } catch (RepositoryException e) {
            throw new LoadException(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new LoadException(e.getMessage(), e);
        } catch (RDFHandlerException e) {
            lc.getWarnings().add(e.getMessage());
        }
    }

    private void loadwithInserter(RepositoryResult<Statement> statements, URI graphUri) throws RepositoryException, RDFHandlerException {
        RepositoryConnection virtuosoConnection = repository.getConnection();
        virtuosoConnection.clear(graphUri);
        virtuosoConnection.setAutoCommit(false);
        try {
            RDFInserter inserter = new BatchedRdfInserter(virtuosoConnection, BATCH_SIZE);
            inserter.enforceContext(graphUri);
            while (statements.hasNext()) {
                Statement s = statements.next();
                inserter.handleStatement(s);
            }
            virtuosoConnection.commit();
        }
        finally {
            virtuosoConnection.close();
        }
    }

    private void backupGraph(String graph) throws LoadException {
        URI backupGraph = ValueFactoryImpl.getInstance().createURI(graph + "previous");
        URI orgGraph = ValueFactoryImpl.getInstance().createURI(graph);
        copyGraph(repository,orgGraph, backupGraph,false);
    }

    private void copyGraph(Repository repository,URI orgGraph, URI destGraph,boolean useCopy) throws LoadException {
        RepositoryConnection connection;
        try {
            connection = repository.getConnection();
            String action = useCopy ? "COPY" : "MOVE";
            String query = "define sql:log-enable 3 " + action +" <" + orgGraph + "> TO <" + destGraph + ">";
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
        String connectionString = "jdbc:virtuoso://" + config.getHost() + ':' + config.getPort();
        versioned = config.isVersioned();
        repository = new VirtuosoRepository(connectionString, config.getUserName(), config.getPassword(), true);
        logger.info("starting load");
        try {
            RepositoryConnection con = repository.getConnection();
            con.close();
        } catch (RepositoryException e) {
            logger.error(e.getMessage());
        }
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
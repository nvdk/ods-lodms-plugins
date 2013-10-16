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
import org.apache.log4j.Logger;
import org.openrdf.model.URI;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import java.util.List;

public class MultipleSparqlUpdateTransformer extends TransformerBase<MultipleSparqlUpdateConfig> implements ConfigDialogProvider<MultipleSparqlUpdateConfig> {
    protected Logger logger = Logger.getLogger(MultipleSparqlUpdateTransformer.class);

    /**
     * Returns a new {@link at.punkt.lodms.integration.ConfigDialog} instance that will be embedded in the
     * dialog window on configuration of this component.
     *
     * @param config An already existing configuration object<br/>
     *               {@code null} if this is the first configuration of the component
     * @return
     */
    @Override
    public ConfigDialog getConfigDialog(MultipleSparqlUpdateConfig config) {
        return new MultipleSparqlUpdateDialog(config);
    }

    /**
     * Returns a new (blank) JavaBean instance with its default values set.
     *
     * @return
     */
    @Override
    public MultipleSparqlUpdateConfig newDefaultConfig() {
        return new MultipleSparqlUpdateConfig();
    }

    @Override
    protected void configureInternal(MultipleSparqlUpdateConfig config) throws ConfigurationException {
    }

    /**
     * Transforms the cached RDF data in the repository.
     *
     * @param repository The repository where the RDF data is cached that should be transformed
     * @param graph      The graph that contains the RDF data which was extracted
     * @param context    The context containing meta information about this transformation process
     * @throws at.punkt.lodms.spi.transform.TransformException
     *          If the transformation fails, this exception has to be thrown
     */
    @Override
    public void transform(Repository repository, URI graph, TransformContext context) throws TransformException {
        try {
            RepositoryConnection connection = repository.getConnection();
            performUpdateQueries(config.getQueries(), connection, context.getWarnings(), graph);
        } catch (RepositoryException e) {
            logger.error(e.getMessage());
            logger.error(e.getStackTrace());
            throw new TransformException(e.getMessage(), e);
        }
    }

    private void performUpdateQueries(List<String> queries, RepositoryConnection connection, List<String> warnings, URI graph) {
        for (String q : queries) {
            try {
                String qString = "define input:default-graph-uri <" + graph.stringValue() + "> " + q;
                Update updateQuery = connection.prepareUpdate(QueryLanguage.SPARQL, qString);
                updateQuery.execute();
            } catch (RepositoryException e) {
                warnings.add(e.getMessage());
                logger.error(e.getMessage());
                logger.error(e.getStackTrace());
            } catch (MalformedQueryException e) {
                warnings.add(e.getMessage());
                logger.error(e.getMessage());
                logger.error(e.getStackTrace());
            } catch (UpdateExecutionException e) {
                warnings.add(e.getMessage());
                logger.error(e.getMessage());
                logger.error(e.getStackTrace());
            }
        }
    }

    /**
     * Returns a short, self-descriptive name of the component.
     *
     * @return
     */
    @Override
    public String getName() {
        return "Multiple SPARQL Update Transformer";
    }

    /**
     * Returns a description of what functionality this component provides.
     *
     * @return
     */
    @Override
    public String getDescription() {
        return "Transforms RDF data based on multiples SPARQL update queries.";
    }

    @Override
    public Resource getIcon(Application application) {
        return new ClassResource("/component.png", application);
    }

    /**
     * Returns a string representing the configured internal state of this component.<br/>
     * This will be used to display this component after having been configured.
     *
     * @return
     */
    @Override
    public String asString() {
        return getName() + ": " + config.getQueries().size() + " queries specified";
    }
}

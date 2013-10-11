import at.punkt.lodms.integration.ConfigBeanProvider;
import at.punkt.lodms.integration.ConfigurableBase;
import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.integration.UIComponent;
import at.punkt.lodms.spi.extract.ExtractContext;
import at.punkt.lodms.spi.extract.ExtractException;
import at.punkt.lodms.spi.extract.Extractor;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import virtuoso.sesame2.driver.VirtuosoRepository;


public class VirtuosoExtractor extends ConfigurableBase<VirtuosoExtractorConfig> implements Extractor, UIComponent, ConfigBeanProvider<VirtuosoExtractorConfig> {
    private VirtuosoRepository repository;
    private URI graph;
    private URI subject;
    private URI predicate;

    @Override
    public void extract(RDFHandler handler, ExtractContext context) throws ExtractException {
        context.getCustomData().put("virtuosoExtractorRepository", repository);
        context.getCustomData().put("virtuosoExtractorGraph", graph);
        try {
            RepositoryConnection virtuosoConnection = repository.getConnection();
            if (!virtuosoConnection.hasStatement(null,null,null,false,graph)) {
                context.cancelPipeline("no statements in specified graph:" + graph.stringValue());
                throw new ExtractException("no statements specified in graph");
            }

            virtuosoConnection.exportStatements(subject, predicate, null, true, handler, graph);
            virtuosoConnection.close();
        } catch (RepositoryException e) {
            throw new ExtractException(e.getMessage(), e);
        } catch (RDFHandlerException e) {
            throw new ExtractException(e.getMessage(), e);
        }
    }

    @Override
    public String getName() {
        return "Virtuoso Extractor";
    }

    @Override
    public String getDescription() {
        return "Extracts rdf statements from a virtuoso database.";
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
    protected void configureInternal(VirtuosoExtractorConfig config) throws ConfigurationException {
        String connectionString = "jdbc:virtuoso://" + config.getHost() + ':' + config.getPort();
        repository = new VirtuosoRepository(connectionString, config.getUserName(), config.getPassword(), true);
        ValueFactory valueFactory = repository.getValueFactory();
        if (config.getGraph() != null)
            graph = valueFactory.createURI(config.getGraph());
        if (config.getSubject() != null)
            subject = valueFactory.createURI(config.getSubject());
        if (config.getPredicate() != null)
            predicate = valueFactory.createURI(config.getPredicate());
    }

    @Override
    public VirtuosoExtractorConfig newDefaultConfig() {
        return new VirtuosoExtractorConfig();
    }
}
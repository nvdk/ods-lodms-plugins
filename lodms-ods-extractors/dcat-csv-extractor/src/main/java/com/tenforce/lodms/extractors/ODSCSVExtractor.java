package com.tenforce.lodms.extractors;

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
import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.ExtractionParameters;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.ExtractionResultImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.RDFHandler;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.model.Statement;


import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

public class ODSCSVExtractor extends ConfigurableBase<ODSCSVExtractorConfig> implements Extractor, UIComponent, ConfigBeanProvider<ODSCSVExtractorConfig> {
    @Override
    public ODSCSVExtractorConfig newDefaultConfig() {
        return new ODSCSVExtractorConfig();
    }

    @Override
    protected void configureInternal(ODSCSVExtractorConfig odscsvExtractorConfig) throws ConfigurationException {

    }

    @Override
    public void extract(RDFHandler rdfHandler, ExtractContext extractContext) throws ExtractException {
        try {
            Repository repository = new SailRepository(new MemoryStore());
            repository.initialize();
            RepositoryConnection con = repository.getConnection();
            RDFInserter inserter = new RDFInserter(con);
            inserter.enforceContext(new URIImpl("http://catalog"));
            extractCSVData(inserter, new URL(config.getCatalogCsv()));
            extractCSVData(inserter, new URL(config.getDatasetCsv()));
            extractCSVData(inserter, new URL(config.getRecordCsv()));
            extractCSVData(inserter, new URL(config.getDistributionCsv()));
            for (Statement s : con.getStatements(null, null, null, false).asList()) {
                rdfHandler.handleStatement(s);
            }
            con.close();
            repository.shutDown();
        } catch (Exception e) {
            throw new ExtractException(e.getMessage(), e);
        }
    }

    @Override
    public String getName() {
        return "DCAT CSV Extractor";
    }

    @Override
    public String getDescription() {
        return "Convert and import CSV's in DCAT format";
    }

    @Override
    public Resource getIcon(Application application) {
        return new ClassResource("/ods.png", application);
    }

    @Override
    public String asString() {
        return getName() + config.getCatalogCsv();
    }

    private void extractCSVData(RDFHandler handler, URL csvURL) throws ExtractException {
        try {
            ExtendedCSVExtractor csvExtractor = new ExtendedCSVExtractor();
            ExtractionParameters params = new ExtractionParameters(ExtractionParameters.ValidationMode.None);
            ExtractionContext ctx = new ExtractionContext("CSV", new URIImpl(config.getBaseUri()));
            ExtractionResult out = new ExtractionResultImpl(ctx, csvExtractor, new TripleHandlerBridge(handler));
            InputStream in = new BufferedInputStream(csvURL.openStream());
            try {
                csvExtractor.run(params, ctx, in, out);
            } finally {
                in.close();
            }
        } catch (Exception ex1) {
            throw new ExtractException(ex1);
        }

    }
}
